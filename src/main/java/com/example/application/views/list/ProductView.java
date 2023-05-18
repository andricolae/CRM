package com.example.application.views.list;

import com.example.application.data.entity.Product;
import com.example.application.data.service.CRMService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;
import com.vaadin.flow.component.textfield.TextField;

@Route(value = "product", layout = MainLayout.class)
@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Products | CRM")
@PermitAll
public class ProductView extends VerticalLayout {
    Grid<Product> grid = new Grid<>(Product.class);
    TextField filterText = new TextField();
    ProductForm cF;
    private final CRMService service;

    public ProductView(CRMService service) {
        this.service = service;
        addClassName("product-view");
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
        cF.setProduct(null);
        cF.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllProducts(filterText.getValue()));
    }

    private Component getContent() {
        VerticalLayout content = new VerticalLayout(grid, cF);
        cF.setWidth("75%");
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        cF = new ProductForm();
        cF.setWidth("25em");
        cF.addSaveListener(this::saveProduct);
        cF.addDeleteListener(this::deleteProduct);
    }

    private void deleteProduct(ProductForm.DeleteEvent deleteEvent) {
        service.deleteProduct(deleteEvent.getProduct());
        updateList();
        closeEditor();
    }

    private void saveProduct(ProductForm.SaveEvent saveEvent) {
        service.saveProduct(saveEvent.getProduct());
        updateList();
        closeEditor();
    }

     private Component getToolbar() {
        filterText.setPlaceholder("Filter by price...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(event -> updateList());

        Button addProductButton = new Button("Add product");
         addProductButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
         addProductButton.addClickListener(event -> addProduct());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addProductButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("product-grid");
        grid.setSizeFull();
        grid.setColumns("name", "price", "description");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editProduct(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private void addProduct() {
        grid.asSingleSelect().clear();
        editProduct(new Product());
    }

    private void editProduct(Product product) {
        if(product == null)
            closeEditor();
        else {
            cF.setProduct(product);
            cF.setVisible(true);
            addClassName("editing");
        }
    }
}