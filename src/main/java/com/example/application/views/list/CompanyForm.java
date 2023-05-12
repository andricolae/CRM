package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class CompanyForm extends FormLayout{
    Binder<Company> binder = new BeanValidationBinder<>(Company.class);
    TextField name = new TextField("Name");
    TextField cif = new TextField("CIF");
    TextField com = new TextField("Nr. Reg. Com.");

    TextField tel = new TextField("Phone");
    TextField address = new TextField("Address");
    EmailField email = new EmailField("Email");
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    private Company company;

    public CompanyForm() {
        addClassName("company-form");

        binder.bindInstanceFields(this);

        add(
            name,
            cif,
            com,
            address,
            tel,
            email,
            createButtonLayout()
        );
    }

    public void setCompany(Company company) {
        this.company = company;
        binder.readBean(company);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> validateAndSave());

        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickShortcut(Key.DELETE);
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, company)));

        return new HorizontalLayout(save, delete);
    }

    private void validateAndSave() {
        try{
            binder.writeBean(company);
            fireEvent(new SaveEvent(this, company));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    /* **************************** Events **************************** */
    public static abstract class CompanyFormEvent extends ComponentEvent<CompanyForm> {
        private Company company;

        protected CompanyFormEvent(CompanyForm source, Company company) {
            super(source, false);
            this.company = company;
        }
        public Company getCompany() {
            return company;
        }
    }

    public static class SaveEvent extends CompanyFormEvent {
        SaveEvent(CompanyForm source, Company company) {
            super(source, company);
        }
    }

    public static class DeleteEvent extends CompanyFormEvent {
        DeleteEvent(CompanyForm source, Company company) {
            super(source, company);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
}
