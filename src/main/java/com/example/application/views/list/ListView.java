package com.example.application.views.list;

import com.example.application.data.entity.Contact;
import com.example.application.data.service.CRMService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;

@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Contacts List | CRM")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class ListView extends VerticalLayout {
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText =  new TextField();
    TextField filterByCompany = new TextField();
    ContactForm cF;
    private final CRMService service;
    public ListView(CRMService service) {
        this.service = service;
        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(
          getToolbar(),
          getContent()
        );
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        cF.setContact(null);
        cF.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllContacts(filterText.getValue()));
    }

    private void updateSearchedByCompany() { grid.setItems(service.findAllByCompany(filterByCompany.getValue())); }

    private Component getContent() {
        VerticalLayout content = new VerticalLayout(grid, cF);
        cF.setWidth("75%");
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        cF = new ContactForm(service.findAllCompanies(), service.findAllStatuses(), service.retrieveProducts());
        cF.setWidth("30em");
        cF.addSaveListener(this::saveContact);
        cF.addDeleteListener(this::deleteContact);
        cF.addCloseListener(e -> closeEditor());
    }

    private void saveContact(ContactForm.SaveEvent event){
        service.saveContact(event.getContact());
        updateList();
        closeEditor();
    }

    private void deleteContact(ContactForm.DeleteEvent event){
        service.deleteContact(event.getContact());
        updateList();
        closeEditor();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> updateList());

        filterByCompany.setPlaceholder("Filter by company...");
        filterByCompany.setClearButtonVisible(true);
        filterByCompany.setValueChangeMode(ValueChangeMode.LAZY);
        filterByCompany.addValueChangeListener(event -> updateSearchedByCompany());

        Button addContactButton = new Button("Add contact");
        addContactButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addContactButton.addClickListener(event -> addContact());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, filterByCompany, addContactButton);
        toolbar.addClassName("toolbar");

        return toolbar;
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email");
        grid.addColumn(contact -> contact.getCompany().getName()).setHeader("Company");
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editContact(e.getValue()));

        grid.setPartNameGenerator(contact -> {
            if (contact.getStatus().getName().equals("Imported lead"))
                return "imported-lead";
            if (contact.getStatus().getName().equals("Not contacted"))
                return "not-contacted";
            if (contact.getStatus().getName().equals("Contacted"))
                return "contacted";
            if (contact.getStatus().getName().equals("Customer"))
                return "customer";
            if (contact.getStatus().getName().equals("Closed (lost)"))
                return "lost";
            return null;
        });
    }

    private void editContact(Contact contact) {
        if(contact == null)
            closeEditor();
        else {
            cF.setContact(contact);
            cF.setVisible(true);
            addClassName("editing");
        }
    }
}
