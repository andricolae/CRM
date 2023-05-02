package com.example.application.views.list;

import com.example.application.data.service.CRMService;
import com.example.application.data.entity.Company;
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

@Route(value = "company", layout = MainLayout.class)
@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Company Info List | CRM")
@PermitAll
public class CompanyView extends VerticalLayout {
    Grid<Company> grid = new Grid<>(Company.class);
    TextField filterText =  new TextField();
    CompanyForm cF;
    private CRMService service;

    public CompanyView(CRMService service) {
        this.service = service;
        addClassName("company-view");
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
        cF.setCompany(null);
        cF.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllCompanies(filterText.getValue()));
    }

    private Component getContent() {
        VerticalLayout content = new VerticalLayout(grid, cF);
        cF.setWidth("75%");
        //content.setFlexGrow(2, grid);
        //content.setFlexGrow(1, cF);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        cF = new CompanyForm();
        cF.setWidth("25em");
        cF.addSaveListener(this::saveCompany); // <1>
        cF.addDeleteListener(this::deleteCompany); // <2>
    }

    private void deleteCompany(CompanyForm.DeleteEvent deleteEvent) {
        service.deleteCompany(deleteEvent.getCompany());
        updateList();
        closeEditor();
    }

    private void saveCompany(CompanyForm.SaveEvent saveEvent) {
        service.saveCompany(saveEvent.getCompany());
        updateList();
        closeEditor();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by company...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> updateList());

        Button addCompanyButton = new Button("Add company");
        addCompanyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addCompanyButton.addClickListener(event -> addCompany());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addCompanyButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("company-grid");
        grid.setSizeFull();
        grid.setColumns("name", "cif", "com", "address", "tel");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editCompany(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private void addCompany() {
        grid.asSingleSelect().clear();
        editCompany(new Company());
    }

    private void editCompany(Company company) {
        if(company == null)
            closeEditor();
        else {
            cF.setCompany(company);
            cF.setVisible(true);
            addClassName("editing");
        }
    }
}
