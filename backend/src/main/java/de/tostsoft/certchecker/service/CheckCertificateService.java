package de.tostsoft.certchecker.service;

import de.tostsoft.certchecker.Converter;
import de.tostsoft.certchecker.model.*;
import de.tostsoft.certchecker.repository.DomainUpdateRepository;
import de.tostsoft.certchecker.repository.DomainWatcherRepository;
import de.tostsoft.certchecker.repository.LoggedDomainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class CheckCertificateService{

    @Autowired
    private SimpMessagingTemplate webSocket;

    private ThreadPoolExecutor threadPoolExecutor=(ThreadPoolExecutor)Executors.newFixedThreadPool(10);

    @Autowired
    private DomainWatcherRepository domainWatcherRepository;

    @Autowired
    private LoggedDomainRepository loggedDomainRepository;

    @Autowired
    private DomainUpdateRepository domainUpdateRepository;

    private Logger logger=LoggerFactory.getLogger(CheckCertificateService.class);

    @Autowired
    private EmailService emailService;

    private long counter=0;

    @Value("${ignoredExpressions}")
    private List<String> ignoredExpressions;

    @Value("${logEverything:false}")
    private boolean logEverything;

    public void handleDomain(DomainInfo domainInfo){
        counter++;
        if(counter%1000 == 0){
            logger.info("Logged another 1000 domains");
        }
        if(threadPoolExecutor.getQueue().size() > 10000){
            logger.warn("Domains in queue above 10000, so skip this one");
            return;
        }
        threadPoolExecutor.execute(()->{
            internHandleDomain(domainInfo);
        });
    }

    private synchronized LoggedDomain saveCreate(LoggedDomain loggedDomain){
        LoggedDomain ret=loggedDomainRepository.findByName(loggedDomain.getName());//TODO make better request in once
        if(ret != null){
            return ret;
        }
        return loggedDomainRepository.save(loggedDomain);
    }

    private void sendDomainInfoMail(User user, LoggedDomain loggedDomain, DomainWatcher domainWatcher, boolean newOne){
        if(StringUtils.isEmpty(user.getNotifyMail())){
            return;
        }
        if(user.getConfirmUUID() != null){
            return;
        }
        emailService.sendMail(user.getNotifyMail(),
                newOne?"Certstream Watcher new Domain":"Certstream Watcher Domain Update",
                "Certstream Watcher want to inform you a Certificate was registered lately\n"+
                        "New domain is: "+loggedDomain.getName()+"\n"+
                        "Matches Domain Watcher with search Term"+(domainWatcher.getRegex()?"(regex)":"")+": "+domainWatcher.getSearchTerm());
    }

    private void internHandleDomain(DomainInfo domainInfo){

        //check if ignored
        for(String ignoredExpression : ignoredExpressions){
            if(domainInfo.getDomain().matches(ignoredExpression)){
                logger.debug("Ingored domain:{} by expression; {}", domainInfo.getDomain(), ignoredExpression);
                return;
            }
        }

        LoggedDomain loggedDomain=loggedDomainRepository.findByName(domainInfo.getDomain());
        if(loggedDomain != null){
            DomainUpdate domainUpdate=domainUpdateRepository.save(DomainUpdate.builder().loggedDomain(loggedDomain).date(domainInfo.getDate()).build());
            loggedDomain.getDomainUpdates().add(domainUpdate);//set after save for return to watchers

            List<DomainWatcher> domainWatchers=domainWatcherRepository.findAllByLoggedDomainsContaining(loggedDomain);
            if(domainWatchers.isEmpty()){
                return;
            }
            for(DomainWatcher domainWatcher : domainWatchers){
                if(!domainWatcher.getActive()){
                    continue;
                }
                webSocket.convertAndSend("/domain/"+domainWatcher.getUser().getId(), Converter.convertLoggedDomain(loggedDomain));
            }
            logger.info("Updated existing url entry: "+domainInfo.getDomain());
            checkAndSendUpdateMail(domainWatchers, loggedDomain, domainInfo);
            return;
        }

        /*Pair<Boolean,LoggedDomain> loggedDomain=getCreate(domainInfo.getDomain());
        DomainUpdate domainUpdate=domainUpdateRepository.save(DomainUpdate.builder().loggedDomain(loggedDomain.getRight()).date(domainInfo.getDate()).build());
        loggedDomain.getValue().getDomainUpdates().add(domainUpdate);//set after save for return to watchers*/

        List<DomainWatcher> domainWatchers=domainWatcherRepository.findTest(domainInfo.getDomain());
        if(domainWatchers.isEmpty() && !logEverything){
            return;
        }

        loggedDomain=LoggedDomain.builder()
                .name(domainInfo.getDomain())
                .domainUpdates(new ArrayList<>())
                .build();
        loggedDomain=saveCreate(loggedDomain);
        DomainUpdate domainUpdate=domainUpdateRepository.save(DomainUpdate.builder().loggedDomain(loggedDomain).date(domainInfo.getDate()).build());
        loggedDomain.getDomainUpdates().add(domainUpdate);//set after save for return to watchers

        for(DomainWatcher domainWatcher : domainWatchers){
            insertValues(domainWatcher, loggedDomain);
        }

        for(DomainWatcher domainWatcher : domainWatchers){
            if(domainWatcher.getActive()){
                webSocket.convertAndSend("/domain/"+domainWatcher.getUser().getId(), Converter.convertLoggedDomain(loggedDomain));
            }
            if(domainWatcher.getSendMail()){
                //todo bundle mails to user
                sendDomainInfoMail(domainWatcher.getUser(), loggedDomain, domainWatcher, true);
            }
        }
        /*
        
        Map<Long,Pair<User,List<DomainWatcher>>> map = new HashMap();
        List<DomainWatcher> info = domainWatcherRepository.findTest(domainInfo.getDomain());
        for(DomainWatcher domainWatcher:info){
            Pair<User,List<DomainWatcher>> list = map.get(domainWatcher.getUser().getId());
            if(list == null){
                list = new ImmutablePair<User,List<DomainWatcher>>(domainWatcher.getUser(),new ArrayList<>());
                map.put(domainWatcher.getUser().getId(),list);
            }
            list.getValue().add(domainWatcher);
        }

        map.forEach((k,v)->{
            List<LoggedDomain> userLogged = new ArrayList<>();
            //System.out.println("Added to User: "+v.getKey().getId());
            for (DomainWatcher domainWatcher : v.getValue()) {
                LoggedDomain loggedDomain = LoggedDomain.builder().domain(domainInfo.getDomain()).domainWatcher(domainWatcher).userId(v.getKey().getId()).build();
                userLogged.add(loggedDomain);
            }
            List<LoggedDomain> res = new ArrayList<>();
            loggedDomainRepository.saveAll(userLogged).forEach(res::add);;
            webSocket.convertAndSend("/domain/"+v.getKey().getId(), Converter.convertLoggedDomains(res));
            //todo check send mail
        });*/
    }

    private void checkAndSendUpdateMail(List<DomainWatcher> watchers, LoggedDomain loggedDomain, DomainInfo domainInfo){
        Optional<DomainUpdate> domainUpdate=loggedDomain.getDomainUpdates().stream().max((u1, u2)->u1.getDate().compareTo(u2.getDate()));
        if(domainUpdate.isEmpty()){
            return;
        }
        int diffInDays=(int)((domainInfo.getDate().getTime()-domainUpdate.get().getDate().getTime())/(1000*60*60*24));
        if(diffInDays > 3){
            for(DomainWatcher watcher : watchers){
                if(!watcher.getMailOnUpdate()){
                    continue;
                }
                sendDomainInfoMail(watcher.getUser(), loggedDomain, watcher, false);
            }
        }
    }


    @Autowired
    private NamedParameterJdbcTemplate jdbcTmpl;

    public void insertValues(DomainWatcher domainWatcher, LoggedDomain loggedDomain){
        StringBuilder sql=new StringBuilder();
        sql.append("INSERT INTO domain_watcher_logged_domains(domain_watcher_id,logged_domains_id) ");
        sql.append("VALUES(:watcher_id,:logged_id) ");

        Map<String,Object> params=new HashMap<String,Object>();
        params.put("watcher_id", domainWatcher.getId());
        params.put("logged_id", loggedDomain.getId());

        jdbcTmpl.update(sql.toString(), params);
    }
}
