package com.produto.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailService {
    @Autowired
    JavaMailSender mailSender;

    @Async
    public void sendDeleteUserEmail(String toEmail,
                             UUID deleteUserToken)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Confirmação de Exclusão de Conta");
        message.setText("Clique no link para confirmar a exclusão da sua conta: "
                + "http://localhost:8080/auth/confirm-deletion?token=" + deleteUserToken);

        mailSender.send(message);
    }

}
