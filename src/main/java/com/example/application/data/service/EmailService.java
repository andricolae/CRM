package com.example.application.data.service;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.UploadI18N;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private JavaMailSender mailSender;

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

    /*
    public void send(String from, String to, String subject, String body, String attachName, Map<String, InputStream> attachments) throws MessagingException, IOException {

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        File file = new File("./attach");
        FileUtils.copyInputStreamToFile(inputStream, file);

        FileSystemResource fileSR = new FileSystemResource(file);
        helper.addAttachment(attachName, fileSR);

        mailSender.send(msg);
    }
    */

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

    public void sendDoc(String from, String to, String subject, String body, List<Pair<String, Document>> attachments) throws MessagingException, IOException {
        Multipart multi = new MimeMultipart();
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        for (Pair<String, Document> attachment : attachments) {
            MimeBodyPart bodyPart = new MimeBodyPart();
            //ByteArrayDataSource dataSource = new ByteArrayDataSource(attachment.getRight(), "application/octet-stream");
            //ByteArrayDataSource dataSource = new ByteArrayDataSource(, "application/octet-stream");
            //bodyPart.setDataHandler(new DataHandler(dataSource));
            bodyPart.setFileName(attachment.getLeft());
            multi.addBodyPart(bodyPart);
        }
        msg.setContent(multi);
        mailSender.send(msg);
    }
}