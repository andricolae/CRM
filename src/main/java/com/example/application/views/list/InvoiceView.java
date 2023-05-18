package com.example.application.views.list;

import com.example.application.data.entity.Invoice;
import com.example.application.data.service.CRMService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;


@Route(value = "invoice", layout = MainLayout.class)
@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Invoices | CRM")
@PermitAll
public class InvoiceView extends VerticalLayout {
    Grid<Invoice> grid = new Grid<>(Invoice.class);
    TextField filterText =  new TextField();
    InvoiceForm iF;
    private final CRMService service;
    Long idLast;

    public InvoiceView(CRMService service) {
        this.service = service;
        addClassName("invoice-view");
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
        iF.setInvoice(null);
        iF.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllInvoices(filterText.getValue()));
    }

    private Component getContent() {
        VerticalLayout content = new VerticalLayout(grid, iF);
        iF.setWidth("75%");
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        idLast = service.getLast().getId();
        iF = new InvoiceForm(service.findAllCompanies(), service.retrieveProducts(), idLast);
        iF.setWidth("25em");
        iF.addSaveListener(this::saveInvoice);
        iF.setVisible(true);
    }

    private void saveInvoice(InvoiceForm.SaveEvent saveEvent) {
        service.saveInvoice(saveEvent.getInvoice());
        updateList();
        closeEditor();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by company...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> updateList());

        Button addInvoiceButton = new Button("Add Invoice");
        addInvoiceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addInvoiceButton.addClickListener(event -> addInvoice());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addInvoiceButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("invoice-grid");
        grid.setSizeFull();
        grid.setColumns("id");
        grid.addColumn("date");
        grid.addColumn(invoice -> invoice.getCompany().getName()).setHeader("Company");
        grid.addColumn("total");
        grid.addColumn(e -> {
            if (e.isPaid())
                return "Paid";
            else
                return "Not Paid";
        }).setHeader("PaymentStatus");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setPartNameGenerator(invoice -> {
            if (invoice.isPaid())
                return "customer";
            else
                return "contacted";
        });
        grid.asSingleSelect().addValueChangeListener(e -> editInvoice(e.getValue()));
    }

    private void addInvoice() {
        grid.asSingleSelect().clear();
        editInvoice(new Invoice());
    }

    private void editInvoice(Invoice invoice) {
        if(invoice == null)
            closeEditor();
        else {
            iF.setInvoice(invoice);
            iF.setVisible(true);
            iF.setTempId(service.getLast().getId());
            addClassName("editing");
        }
    }
}
