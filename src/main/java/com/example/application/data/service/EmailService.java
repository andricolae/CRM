package com.example.application.data.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNoAttach(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void send(String from, String to, String subject, String body, List<Pair<String, InputStream>> attachments) throws MessagingException, IOException {
        Multipart multi = new MimeMultipart();
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        for (Pair<String, InputStream> attachment : attachments) {
            MimeBodyPart bodyPart = new MimeBodyPart();
            ByteArrayDataSource dataSource = new ByteArrayDataSource(attachment.getRight(), "application/octet-stream");
            bodyPart.setDataHandler(new DataHandler(dataSource));
            bodyPart.setFileName(attachment.getLeft());
            multi.addBodyPart(bodyPart);
        }
        msg.setContent(multi);
        mailSender.send(msg);
    }
}