package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Invoice;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import javax.swing.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class InvoiceForm extends FormLayout {
    Binder<Invoice> binder = new BeanValidationBinder<>(Invoice.class);
    ComboBox<Company> company = new ComboBox<>("Company");
    TextField total = new TextField("Total");
    Button save = new Button("Save");
    private Invoice invoice;

    /*
        TODO -> invoice content
            - logo
            - client company info
            - our company info
            - series & nr. + date
            - ITEMS
            - PRICE AND VAT RATE
            - TOTAL
            - pay before
     */

    public InvoiceForm(List<Company> companies) {
        addClassName("invoice-form");

        binder.bindInstanceFields(this);

        company.setItems(companies);
        company.setItemLabelGenerator(Company::getName);

        add(
                company,
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
            validateAndSave();
        });
        return new HorizontalLayout(save);
    }

    private void validateAndSave() {
        try{
            binder.writeBean(invoice);
            fireEvent(new SaveEvent(this, invoice));
        } catch (ValidationException e) {
            e.printStackTrace();
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
