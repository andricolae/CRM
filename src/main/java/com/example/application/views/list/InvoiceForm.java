package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Invoice;
import com.example.application.data.entity.Product;
import com.example.application.data.service.EmailService;
import com.example.application.data.service.MailSenderConfig;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;
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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceForm extends FormLayout {
    private final double EXTRACT_VAT = 0.84;
    Binder<Invoice> binder = new BeanValidationBinder<>(Invoice.class);
    ComboBox<Company> company = new ComboBox<>("Company");
    ComboBox<Product> product = new ComboBox<>("Products");
    TextArea items = new TextArea("Items");
    TextField pieces = new TextField("Nr. of Items");
    TextField total = new TextField("Total");
    Button save = new Button("Generate and Send");
    Button addProduct = new Button("Add Product");
    Button paid = new Button("Paid");
    List<Pair<String, InputStream>> attachments;
    List<Item> itemsPicked;
    Long tempId;

    public void setTempId(Long tempId) {
        this.tempId = tempId;
    }

    private Invoice invoice;

    public InvoiceForm(List<Company> companies, List<Product> products, Long tempId) {
        addClassName("invoice-form");

        this.tempId = tempId;

        binder.bindInstanceFields(this);

        company.setItems(companies);
        company.setItemLabelGenerator(Company::getName);

        product.setItems(products);
        product.setItemLabelGenerator(Product::getName);

        total.setValue("1.0");

        items.setValue("");

        items.setReadOnly(true);

        itemsPicked = new ArrayList<>();

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
        if (invoice != null)
            if (invoice.getTotal() != 0) {
                company.setReadOnly(true);
                product.setReadOnly(true);
                pieces.setReadOnly(true);
                total.setReadOnly(true);
            }
            else {
                items.setValue("");
                product.setValue(null);
                company.setReadOnly(false);
                product.setReadOnly(false);
                pieces.setReadOnly(false);
                total.setReadOnly(false);
            }
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> {
            invoice.setId(tempId + 1);
            invoice.setDate(LocalDate.now());
            if(!items.getValue().equals("")) {
                try {
                    generateInvoice();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            validateAndSave();
        });

        addProduct.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addProduct.addClickListener(event -> updateValues());

        paid.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        paid.addClickListener(event -> {
            try {
                setInvoicePaid();
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        });

        return new HorizontalLayout(save, addProduct, paid);
    }

    private void setInvoicePaid() throws ValidationException {
        invoice.setPaid(true);
        binder.writeBean(invoice);
        fireEvent(new SaveEvent(this, invoice));
    }

    private void updateValues() {
        if (pieces.getValue().equals("")) {
            Notification.show("Insert number of items!").addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            double tempPrice = Double.parseDouble(pieces.getValue()) * product.getValue().getPrice();
            double totalSum = tempPrice + Double.parseDouble(total.getValue());

            Item temp = new Item();
            temp.setProd(product.getValue().getName());
            temp.setPrice(product.getValue().getPrice());
            temp.setQuantity(Integer.parseInt(pieces.getValue()));
            temp.setTot(tempPrice);
            itemsPicked.add(temp);

            total.setValue(String.format("%.2f", totalSum));

            items.setValue(items.getValue() + "\n" + "---" +
                product.getValue().getName() + "--- Price: " +
                product.getValue().getPrice() + " Lei ---  Nr. of Items: " +
                pieces.getValue() + "--- Total: " + String.format("%.2f", tempPrice) + " Lei");

            pieces.setValue("");
        }
    }

    public void validateAndSave() {
        if (company.getValue() != null && !items.getValue().equals("")) {
            if (!invoice.isPaid()) {
                try {
                    binder.writeBean(invoice);
                    fireEvent(new SaveEvent(this, invoice));
                } catch (ValidationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
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
    }

    private void generateInvoice() throws IOException {

        String fileName = company.getValue().getName() + "_" + LocalDate.now() + ".pdf";

        Document invoiceDoc = new Document(PageSize.A4);

        PdfWriter.getInstance(invoiceDoc, new FileOutputStream("./invoices/" + fileName));

        invoiceDoc.open();

        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTitle.setSize(20);
        Font fontHeader = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontHeader.setSize(15);

        Paragraph paragraphTitle = new Paragraph("INVOICE | no. " + (tempId + 1), fontTitle);
        Paragraph paragraphDate = new Paragraph("Date: " + invoice.getDate(), fontHeader);
        Paragraph paragraphDue = new Paragraph("Due in 15 days since receiving\n\n", fontHeader);

        paragraphTitle.setAlignment(Paragraph.ALIGN_CENTER);
        paragraphDate.setAlignment(Paragraph.ALIGN_CENTER);
        paragraphDue.setAlignment(Paragraph.ALIGN_CENTER);

        invoiceDoc.add(paragraphTitle);
        invoiceDoc.add(paragraphDate);
        invoiceDoc.add(paragraphDue);

        PdfPTable tableCompanyInfo = new PdfPTable(2);

        tableCompanyInfo.setWidthPercentage(100f);
        tableCompanyInfo.setWidths(new int[] { 3, 3 });
        tableCompanyInfo.setSpacingBefore(5);

        PdfPCell cellCompanyInfo = new PdfPCell();

        cellCompanyInfo.setBackgroundColor(CMYKColor.CYAN);
        cellCompanyInfo.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.COURIER_OBLIQUE);
        font.setColor(CMYKColor.BLACK);

        cellCompanyInfo.setPhrase(new Phrase("Billed To", font));
        tableCompanyInfo.addCell(cellCompanyInfo);
        cellCompanyInfo.setPhrase(new Phrase("Company Info", font));
        tableCompanyInfo.addCell(cellCompanyInfo);

        System.out.println(invoice.getCompany());
        tableCompanyInfo.addCell(company.getValue().toString());
        String ourInfo = """
                MY COMPANY INC.\s
                cif: 84503274\s
                address: Sibiu, str. Ludos, nr. 2\s
                tel: 0762947233\s
                email: andreicalutiu@gmail.com\s
                payment info: RO11BTRLRONCRT04498364""";
        tableCompanyInfo.addCell(ourInfo);

        invoiceDoc.add(tableCompanyInfo);

        Paragraph paragraphBlank = new Paragraph("\n\n", fontTitle);
        invoiceDoc.add(paragraphBlank);

        PdfPTable tableItems = new PdfPTable(4);

        tableItems.setWidthPercentage(100f);
        tableItems.setWidths(new int[] { 3, 3, 3, 3 });
        tableItems.setSpacingBefore(5);

        PdfPCell cellItems = new PdfPCell();

        cellItems.setBackgroundColor(CMYKColor.YELLOW);
        cellItems.setPadding(5);

        font.setColor(CMYKColor.BLACK);

        cellItems.setPhrase(new Phrase("Item", font));
        tableItems.addCell(cellItems);
        cellItems.setPhrase(new Phrase("Price", font));
        tableItems.addCell(cellItems);
        cellItems.setPhrase(new Phrase("Quantity", font));
        tableItems.addCell(cellItems);
        cellItems.setPhrase(new Phrase("Total", font));
        tableItems.addCell(cellItems);

        for (Item item : itemsPicked) {
            tableItems.addCell(item.getProd());
            tableItems.addCell(String.format("%.2f", item.getPrice()));
            tableItems.addCell(String.valueOf(item.getQuantity()));
            tableItems.addCell(String.format("%.2f", item.getTot()));
        }

        invoiceDoc.add(tableItems);

        invoiceDoc.add(paragraphBlank);

        double vat = Double.parseDouble(total.getValue()) - (Double.parseDouble(total.getValue()) * EXTRACT_VAT);
        Paragraph paragraphVat = new Paragraph("VAT: " + String.format("%.2f", vat), fontHeader);
        Paragraph paragraphTotal = new Paragraph("Total: " + total.getValue(), fontHeader);

        paragraphVat.setAlignment(Paragraph.ALIGN_RIGHT);
        paragraphTotal.setAlignment(Paragraph.ALIGN_RIGHT);

        invoiceDoc.add(paragraphVat);
        invoiceDoc.add(paragraphTotal);

        invoiceDoc.add(paragraphBlank);

        Paragraph paragraphClosure1 = new Paragraph("Thank you for doing business with us!", fontHeader);
        Paragraph paragraphClosure2 = new Paragraph("Valid without signature and/or stamp!", fontHeader);

        paragraphClosure1.setAlignment(Paragraph.ALIGN_CENTER);
        paragraphClosure2.setAlignment(Paragraph.ALIGN_CENTER);

        invoiceDoc.add(paragraphClosure1);
        invoiceDoc.add(paragraphClosure2);

        invoiceDoc.close();

        InputStream in = new FileInputStream("./invoices/" + fileName);
        attachments = new ArrayList<>();
        attachments.add(Pair.of(fileName, in));

        sendInvoice();
    }

    private void sendInvoice() {
        MailSenderConfig mailSenderConfig = new MailSenderConfig();
        JavaMailSender javaMailSender = mailSenderConfig.javaMailSender();
        EmailService emailService = new EmailService(javaMailSender);
        try {
            emailService.send("andreicalutiu@gmail.com", company.getValue().getEmail(), "Invoice_" + LocalDate.now(),
                    "Hi, attached is your invoice!", attachments);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
        Notification notification = Notification.show("Email Sent!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_STRETCH);
    }

    /* **************************************************************** */
    public static class Item {
        public String prod;
        public double price;
        public int quantity;
        public double tot;

        public Item() {}

        public String getProd() {
            return prod;
        }

        public void setProd(String prod) {
            this.prod = prod;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTot() {
            return tot;
        }

        public void setTot(double tot) {
            this.tot = tot;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "prod='" + prod + '\'' +
                    ", price=" + price +
                    ", quantity=" + quantity +
                    ", tot=" + tot +
                    '}';
        }
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
