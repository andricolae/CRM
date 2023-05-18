package com.example.application.views.list;

import com.example.application.data.entity.Product;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class ProductForm extends FormLayout {

    Binder<Product> binder = new BeanValidationBinder<>(Product.class);

    TextField name = new TextField("Name");

    TextField price = new TextField("Price");

    TextField description = new TextField("Description");

    Button save = new Button("Save");

    Button delete = new Button("Delete");
    private Product product;

    public ProductForm() {
        addClassName("product-form");
        binder.bindInstanceFields(this);

        add(
            name,
            price,
            description,
            createButtonLayout()
        );
    }

    public void setProduct(Product product) {
        this.product = product;
        binder.readBean(product);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> validateAndSave());

        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickShortcut(Key.DELETE);
        delete.addClickListener(event -> fireEvent(new ProductForm.DeleteEvent(this, product)));

        return new HorizontalLayout(save, delete);
    }

    private void validateAndSave() {
        try{
            binder.writeBean(product);
            fireEvent(new ProductForm.SaveEvent(this, product));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    /* **************************** Events **************************** */
    public static abstract class ProductFormEvent extends ComponentEvent<ProductForm> {
        private Product product;

        protected ProductFormEvent(ProductForm source, Product product) {
            super(source, false);
            this.product = product;
        }
        public Product getProduct() {
            return product;
        }
    }

    public static class SaveEvent extends ProductForm.ProductFormEvent {
        SaveEvent(ProductForm source, Product product) {
            super(source, product);
        }
    }

    public static class DeleteEvent extends ProductForm.ProductFormEvent {
        DeleteEvent(ProductForm source, Product product) {
            super(source, product);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<ProductForm.DeleteEvent> listener) {
        return addListener(ProductForm.DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<ProductForm.SaveEvent> listener) {
        return addListener(ProductForm.SaveEvent.class, listener);
    }
}
