package de.tostsoft.certchecker.service;

import de.tostsoft.certchecker.dto.RegistrationDTO;
import de.tostsoft.certchecker.dto.UserUpdateDTO;
import de.tostsoft.certchecker.model.User;
import de.tostsoft.certchecker.repository.UserRepository;
import de.tostsoft.certchecker.security.WebSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Transactional
    public User findUser(String username){
        return userRepository.findByUsername(username);
    }

    @Autowired
    private EmailService emailService;

    Logger logger=LoggerFactory.getLogger(UserService.class);

    @Value("${mail.confirm-url:http://localhost:8080/api/user/confirm/}")
    private String mailConfirmUrl;

    @Transactional
    public User getUser(long userId){
        return userRepository.findById(userId);
    }

    public synchronized User createUser(RegistrationDTO registrationDTO){
        User user=userRepository.findByUsername(registrationDTO.getUsername());
        if(user != null){
            return null;
        }
        user=User.builder()
                .username(registrationDTO.getUsername())
                .password(webSecurityConfig.passwordEncoder().encode(registrationDTO.getPassword()))
                .notifyMail(registrationDTO.getNotifyMail())
                .build();
        return userRepository.save(user);
    }

    public User updateUser(User user, UserUpdateDTO userUpdateDTO){
        if(userUpdateDTO.getPassword() != null){
            user.setPassword(webSecurityConfig.passwordEncoder().encode(userUpdateDTO.getPassword()));
        }
        if(userUpdateDTO.getNotifyMail() != null){
            user=userRepository.findById(user.getId()).get();//needet because other object is from authentication and fetch lazy not working for some reason
            user.setNotifyMail(userUpdateDTO.getNotifyMail());
            if(user.getConfirmedMails().contains(userUpdateDTO.getNotifyMail())){
                user.setConfirmUUID(null);
            }else{
                user.setConfirmUUID(UUID.randomUUID());
                emailService.sendMail(user.getNotifyMail(),
                        "Confirm Certsteram-Watcher mail-address",
                        "Please confirm you email-address by using the link below\n"+mailConfirmUrl+user.getConfirmUUID());
                logger.info("ConfirmUrl: {}", mailConfirmUrl+user.getConfirmUUID());
            }
        }
        return userRepository.save(user);
    }

    public boolean confirmMail(UUID uuid){
        User user=userRepository.findByConfirmUUID(uuid);
        if(user == null){
            return false;
        }
        user.setConfirmUUID(null);
        user.getConfirmedMails().add(user.getNotifyMail());
        userRepository.save(user);
        return true;
    }

}
