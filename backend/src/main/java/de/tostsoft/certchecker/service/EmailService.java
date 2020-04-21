package de.tostsoft.certchecker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService{

    Logger logger=LoggerFactory.getLogger(EmailService.class);

    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailUser;

    public EmailService(JavaMailSender javaMailSender){
        this.javaMailSender=javaMailSender;
    }

    public void sendMail(String toEmail, String subject, String message){

        var mailMessage=new SimpleMailMessage();

        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);

        mailMessage.setText(message);
        mailMessage.setFrom(mailUser);

        javaMailSender.send(mailMessage);

        logger.info("Send mail to "+toEmail);
    }
}
