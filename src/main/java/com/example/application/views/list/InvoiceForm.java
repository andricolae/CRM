package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Invoice;
import com.example.application.data.entity.Product;
import com.example.application.data.service.EmailService;
import com.example.application.data.service.MailSenderConfig;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.swing.*;
import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceForm extends FormLayout {
    Binder<Invoice> binder = new BeanValidationBinder<>(Invoice.class);
    ComboBox<Company> company = new ComboBox<>("Company");
    ComboBox<Product> product = new ComboBox<>("Products");
    TextArea items = new TextArea("Items");
    TextField pieces = new TextField("Nr. of Items");
    TextField total = new TextField("Total");
    Button save = new Button("Save");
    Button addProduct = new Button("Add Product");
    List<Pair<String, InputStream>> attachments;

    private Invoice invoice;

    /*
        - logo
        - client company info
        - our company info
        - series & nr. + date
        - pay before
     */

    public InvoiceForm(List<Company> companies, List<Product> products) {
        addClassName("invoice-form");

        binder.bindInstanceFields(this);

        company.setItems(companies);
        company.setItemLabelGenerator(Company::getName);

        product.setItems(products);
        product.setItemLabelGenerator(Product::getName);

        total.setValue("1.0");

        items.setValue("");

        items.setReadOnly(true);

        add(
                company,
                product,
                items,
                pieces,
                total,
                createButtonLayout()
        );
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        binder.readBean(invoice);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> {
            invoice.setDate(LocalDate.now());
            generateInvoice();
            validateAndSave();
        });

        addProduct.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addProduct.addClickListener(event -> updateValues());

        return new HorizontalLayout(save, addProduct);
    }

    private void updateValues() {
        if (pieces.getValue().equals("")) {
            Notification.show("Insert number of items!").addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            double tempPrice = Double.parseDouble(pieces.getValue()) * product.getValue().getPrice();
            double totalSum = tempPrice + Double.parseDouble(total.getValue());
            total.setValue(String.format("%.2f", totalSum));
            items.setValue(items.getValue() + "\n" + "---" +
                product.getValue().getName() + "--- Price: " +
                product.getValue().getPrice() + " Lei ---  Nr. of Items: " +
                pieces.getValue() + "--- Total: " + product.getValue().getPrice() *
                Double.parseDouble(pieces.getValue()) + " Lei");
            pieces.setValue("");
        }
    }

    private void validateAndSave() {
        if (company.getValue() == null) {
            Notification.show("Choose a Company!").addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else if (items.getValue().equals("")) {
            Notification.show("Choose at least an Item!").addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            try {
                binder.writeBean(invoice);
                fireEvent(new SaveEvent(this, invoice));
                items.setValue("");
                product.setValue(null);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateInvoice() {

        System.out.println(company.getValue().getName());
        String fileName = company.getValue().getName() + "_" + LocalDate.now() + ".pdf";
        Document document = new Document();

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {

            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("Invoice for " + company.getValue().getName()));
            document.add(new Paragraph("Billing Address: " + company.getValue().getAddress()));

            document.add(new Paragraph(
                    "**************************************************" +
                           "---Payable in 15 working days since receiving!---" +
                           "**************************************************"));
            document.close();

            //DatabaseService.saveInvoiceDocument(fileName, outputStream);

            Notification.show("Invoice generated and saved!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        attachments = new ArrayList<>();
        InputStream inputStream = new MultiFileMemoryBuffer().getInputStream(fileName);
        attachments.add(Pair.of(fileName, inputStream));
        sendInvoice();
    }

    private void sendInvoice() {
        MailSenderConfig mailSenderConfig = new MailSenderConfig();
        JavaMailSender javaMailSender = mailSenderConfig.javaMailSender();
        EmailService emailService = new EmailService(javaMailSender);
        try {
            emailService.send("andreicalutiu@gmail.com", company.getValue().getEmail(), "Invoice_" + LocalDate.now(),
                    "Hi, attached is your invoice!", attachments);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Notification notification = Notification.show("Email Sent!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_STRETCH);
    }

    /* **************************** Events **************************** */
    public static abstract class InvoiceFormEvent extends ComponentEvent<InvoiceForm> {
        private Invoice invoice;

        protected InvoiceFormEvent(InvoiceForm source, Invoice invoice) {
            super(source, false);
            this.invoice = invoice;
        }
        public Invoice getInvoice() {
            return invoice;
        }
    }

    public static class SaveEvent extends InvoiceFormEvent {
        SaveEvent(InvoiceForm source, Invoice invoice) {
            super(source, invoice);
        }
    }

    public static class DeleteEvent extends InvoiceFormEvent {
        DeleteEvent(InvoiceForm source, Invoice invoice) {
            super(source, invoice);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
}
