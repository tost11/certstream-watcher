package de.tostsoft.certchecker;

import de.tostsoft.certchecker.model.DomainInfo;
import de.tostsoft.certchecker.service.CheckCertificateService;
import io.calidog.certstream.CertStreamCertificate;
import io.calidog.certstream.CertStreamMessageData;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;

@SpringBootApplication
@Configuration
@EnableScheduling
public class CertcheckerApplication implements CommandLineRunner{
    public static void main(String[] args){
        SpringApplication.run(CertcheckerApplication.class, args);
    }

    Logger logger=LoggerFactory.getLogger(CertcheckerApplication.class);

    @Autowired
    private CheckCertificateService checkCertificateService;

    private boolean debug=false;

    @PostConstruct
    void init(){
        if(debug){
            Thread t=new Thread(()->{
                while(true){
                    DomainInfo domainInfo=new DomainInfo();
                    domainInfo.setDate(new Date());
                    domainInfo.setDomain(RandomString.make(10)+"."+RandomString.make(2));
                    checkCertificateService.handleDomain(domainInfo);
                    try{
                        Thread.sleep(1000);
                    }catch(InterruptedException ex){
                        break;
                    }
                }
            });
            t.start();
        }else{
            MyCertstream.onMessage(msg->{
                try{
                    CertStreamMessageData data=(CertStreamMessageData)FieldUtils.readField(msg, "data", true);
                    CertStreamCertificate leaf=(CertStreamCertificate)FieldUtils.readField(data, "leafCert", true);
                    HashMap<String,String> subject=(HashMap<String,String>)FieldUtils.readField(leaf, "subject", true);

                    String cn=subject.get("CN");
                    if(!StringUtils.isEmpty(cn)){
                        DomainInfo domainInfo=DomainInfo.builder().domain(cn).date(new Date()).build();
                        checkCertificateService.handleDomain(domainInfo);
                    }
                }catch(IllegalAccessException ex){
                    logger.error("Error casting cerstream message; ", ex);
                }
            });
        }

        /*:()->{
            Gson g=new Gson();
            while(true){
                try{
                    Process process=Runtime.getRuntime().exec("python log.py");
                    BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
                    while(process.isAlive()){
                        String line=reader.readLine();
                        if(line != null){
                            DomainInfo domainInfo=g.fromJson(line, DomainInfo.class);
                            domainInfo.setDate(new Date());
                            //System.out.println(domainInfo);
                            checkCertificateService.handleDomain(domainInfo);
                        }else{
                            logger.error("Cerstream watcher failed restart");
                            break;
                        }
                    }
                    logger.info("Process exited with value {}", process.exitValue());
                }catch(IOException ex){
                    logger.info("Exception by listening on cert-stream", ex);
                }
            }
        });
        t.start();
         */
    }

    @Override
    public void run(String... args) throws Exception{
        for(String arg : args){
            if(arg.equals("debug")){
                debug=true;
                break;
            }
        }
    }
}