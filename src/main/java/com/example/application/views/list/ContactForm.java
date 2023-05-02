package com.example.application.views.list;

import com.example.application.data.entity.Product;
import com.example.application.data.service.EmailService;
import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import com.example.application.data.service.MailSenderConfig;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ContactForm extends FormLayout {
    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);
    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    EmailField email = new EmailField("Email");

    TextField tel = new TextField("Phone");
    TextField mentions = new TextField("Mentions");
    ComboBox<Status> status = new ComboBox<>("Staus");
    ComboBox<Company> company = new ComboBox<>("Company");
    ComboBox<Product> product = new ComboBox<>("Products");
    TextField emailSubject = new TextField("Subject...");
    TextArea message = new TextArea("Message...");
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

    public ContactForm(List<Company> companies, List<Status> statuses, List<Product> products) {
        addClassName("contact-form");

        binder.bindInstanceFields(this);

        company.setItems(companies);
        company.setItemLabelGenerator(Company::getName);

        status.setItems(statuses);
        status.setItemLabelGenerator(Status::getName);

        status.setRenderer(new ComponentRenderer<>(str -> {
            Span text = new Span(String.valueOf(str.getName()));
            if (str.getName().equals("Imported lead"))
                text.getStyle().set("color", "blue");
            if (str.getName().equals("Not contacted"))
                text.getStyle().set("color", "red");
            if (str.getName().equals("Contacted"))
                text.getStyle().set("color", "orange");
            if (str.getName().equals("Customer"))
                text.getStyle().set("color", "green");
            if (str.getName().equals("Closed (lost)"))
                text.getStyle().set("color", "black");
            return text;
        }));

        product.setItems(products);
        product.setItemLabelGenerator(Product::getName);

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
          tel,
          company,
          status,
          mentions,
          product,
          emailSubject,
          message,
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

        product.addValueChangeListener(event -> message.setValue(message.getValue() + "\n" + event.getValue().getName() + " Price: " + event.getValue().getPrice() + " Lei"));

        sendEmail.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        MailSenderConfig mailSenderConfig = new MailSenderConfig();
        JavaMailSender javaMailSender = mailSenderConfig.javaMailSender();
        EmailService emailService = new EmailService(javaMailSender);
        sendEmail.addClickListener(event -> {
            if (inputStream == null)
                emailService.sendNoAttach("mycrm586@gmail.com", email.getValue(), emailSubject.getValue(), message.getValue());
            else {
                try {
                    emailService.send("mycrm586@gmail.com", email.getValue(), emailSubject.getValue(), message.getValue(), attachName, inputStream);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Notification notification = Notification.show("Email Sent!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.BOTTOM_STRETCH);
            message.setValue("---" + DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .format(LocalDateTime.now()) + "---" + "\n" + message.getValue());
            validateAndSave();
        });

        return new HorizontalLayout(save, delete, cancel, sendEmail);
    }

    private void validateAndSave() {
        try{
            binder.writeBean(contact);
            fireEvent(new SaveEvent(this, contact));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    /* **************************** Events **************************** */
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
