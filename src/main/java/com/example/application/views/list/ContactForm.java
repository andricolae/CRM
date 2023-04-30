package com.example.application.views.list;

import com.example.application.Application;
import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import com.example.application.data.service.EmailService;
import com.example.application.data.service.MailSenderConfig;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ContactForm extends FormLayout {
    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);
    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    EmailField email = new EmailField("Email");
    ComboBox<Status> status = new ComboBox<>("Staus");
    ComboBox<Company> company = new ComboBox<>("Company");
    TextField emailSubject = new TextField("Subject...");
    TextArea emailBody = new TextArea("Message...");
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button cancel = new Button("Cancel");
    Button sendEmail = new Button("Send Email");
    //Button sendWithAttach = new Button("Send Attach");
    String attachName;
    MultiFileMemoryBuffer buffer;
    Upload upload;
    InputStream inputStream;
    private Contact contact;

    public ContactForm(List<Company> companies, List<Status> statuses) {
        addClassName("contact-form");

        binder.bindInstanceFields(this);

        company.setItems(companies);
        company.setItemLabelGenerator(Company::getName);

        status.setItems(statuses);
        status.setItemLabelGenerator(Status::getName);

        buffer = new MultiFileMemoryBuffer();
        upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            attachName = event.getFileName();
            inputStream = buffer.getInputStream(attachName);
        });

        add(
          firstName,
          lastName,
          email,
          company,
          status,
          emailSubject,
          emailBody,
          upload,
          createButtonLayout()
        );
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        binder.readBean(contact);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> validateAndSave());

        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickShortcut(Key.DELETE);
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, contact)));

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        sendEmail.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        MailSenderConfig mailSenderConfig = new MailSenderConfig();
        JavaMailSender javaMailSender = mailSenderConfig.javaMailSender();
        EmailService emailService = new EmailService(javaMailSender);
        sendEmail.addClickListener(event -> {
            if (inputStream == null)
                emailService.sendNoAttach("mycrm586@gmail.com", email.getValue(), emailSubject.getValue(), emailBody.getValue());
            else {
                try {
                    emailService.send("mycrm586@gmail.com", email.getValue(), emailSubject.getValue(), emailBody.getValue(), attachName, inputStream);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Notification notification = Notification.show("Email Sent!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.BOTTOM_STRETCH);
        });

        /*sendWithAttach.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        MailSenderConfig mailSenderConfig1 = new MailSenderConfig();
        JavaMailSender javaMailSender1 = mailSenderConfig1.javaMailSender();
        EmailService emailService1 = new EmailService(javaMailSender1);
        sendWithAttach.addClickListener(event -> {
            try {
                emailService1.send("mycrm586@gmail.com", email.getValue(), emailSubject.getValue(), emailBody.getValue(), attachName, inputStream);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Notification notification = Notification.show("Email Sent!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.BOTTOM_STRETCH);
        });*/

        return new HorizontalLayout(save, delete, sendEmail);
    }

    private void validateAndSave() {
        try{
            binder.writeBean(contact);
            fireEvent(new SaveEvent(this, contact));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
        private Contact contact;

        protected ContactFormEvent(ContactForm source, Contact contact) {
            super(source, false);
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }

    public static class SaveEvent extends ContactFormEvent {
        SaveEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {
        DeleteEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }

    }

    public static class CloseEvent extends ContactFormEvent {
        CloseEvent(ContactForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
