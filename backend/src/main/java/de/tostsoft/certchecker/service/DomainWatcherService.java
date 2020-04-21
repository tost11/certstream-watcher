package de.tostsoft.certchecker.service;

import de.tostsoft.certchecker.dto.DomainWatcherDTO;
import de.tostsoft.certchecker.model.DomainWatcher;
import de.tostsoft.certchecker.model.User;
import de.tostsoft.certchecker.repository.DomainWatcherRepository;
import de.tostsoft.certchecker.repository.LoggedDomainRepository;
import de.tostsoft.certchecker.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainWatcherService{

    @Autowired
    private DomainWatcherRepository domainWatcherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoggedDomainRepository loggedDomainRepository;

    private DomainWatcher converDomainWatcherDTO(DomainWatcherDTO domainWatcherDTO){
        return DomainWatcher.builder()
                .id(domainWatcherDTO.getId())
                .active(domainWatcherDTO.getActive())
                .regex(domainWatcherDTO.getRegex())
                .searchTerm(domainWatcherDTO.getSearchTerm())
                .sendMail(domainWatcherDTO.getSendMail())
                .mailOnUpdate(domainWatcherDTO.getMailOnUpdate())
                .build();
    }

    public DomainWatcher saveWatcher(User user, DomainWatcherDTO domainWatcherDTO){
        if(domainWatcherDTO.getId() == null){
            DomainWatcher domainWatcher=converDomainWatcherDTO(domainWatcherDTO);
            domainWatcher.setUser(user);
            domainWatcher.setLoggedDomains(loggedDomainRepository.findAllByNameContaining(domainWatcher.getSearchTerm()));
            return domainWatcherRepository.save(domainWatcher);
        }
        DomainWatcher domainWatcher=domainWatcherRepository.findByIdAndUserId(domainWatcherDTO.getId(), user.getId());
        if(domainWatcher == null){
            return null;
        }

        domainWatcher.setActive(domainWatcherDTO.getActive());
        domainWatcher.setRegex(domainWatcherDTO.getRegex());
        domainWatcher.setSendMail(domainWatcherDTO.getSendMail());
        domainWatcher.setMailOnUpdate(domainWatcherDTO.getMailOnUpdate());

        if(!StringUtils.equals(domainWatcher.getSearchTerm(), domainWatcherDTO.getSearchTerm())){
            domainWatcher.setSearchTerm(domainWatcherDTO.getSearchTerm());
            domainWatcher.setLoggedDomains(loggedDomainRepository.findAllByNameContaining(domainWatcher.getSearchTerm()));
        }
        return domainWatcherRepository.save(domainWatcher);
    }

    public DomainWatcher setWatcherActive(User user, long watcherId, boolean active){
        DomainWatcher domainWatcher=domainWatcherRepository.findByIdAndUserId(watcherId, user.getId());
        if(domainWatcher == null){
            return null;
        }
        domainWatcher.setActive(active);
        return domainWatcherRepository.save(domainWatcher);
    }

    public List<DomainWatcher> getDomainWatchers(User user){
        return domainWatcherRepository.findAllByUser(user);
    }
}
