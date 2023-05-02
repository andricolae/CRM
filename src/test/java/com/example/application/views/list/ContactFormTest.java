package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Product;
import com.example.application.data.entity.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContactFormTest {
    private List<Company> companies;
    private List<Status> statuses;
    private List<Product> products;
    private Contact marcUsher;
    private Company company1;
    private Company company2;
    private Status status1;
    private Status status2;
    private Product product1;
    private Product product2;

    @BeforeEach  
    public void setupData() {
        companies = new ArrayList<>();
        company1 = new Company();
        company1.setName("Vaadin Ltd");
        company2 = new Company();
        company2.setName("IT Mill");
        companies.add(company1);
        companies.add(company2);

        statuses = new ArrayList<>();
        status1 = new Status();
        status1.setName("Status 1");
        status2 = new Status();
        status2.setName("Status 2");
        statuses.add(status1);
        statuses.add(status2);

        products = new ArrayList<>();
        product1 = new Product();
        product1.setName("Product 1");
        product2 = new Product();
        product2.setName("Product 2");
        products.add(product1);
        products.add(product2);

        marcUsher = new Contact();
        marcUsher.setFirstName("Marc");
        marcUsher.setLastName("Usher");
        marcUsher.setEmail("marc@usher.com");
        marcUsher.setStatus(status1);
        marcUsher.setCompany(company2);
    }

    @Test
    public void formFieldsPopulated() {
        ContactForm cF = new ContactForm(companies, statuses, products);
        cF.setContact(marcUsher);
        Assertions.assertEquals("Marc", cF.firstName.getValue());
        Assertions.assertEquals("Usher", cF.lastName.getValue());
        Assertions.assertEquals("marc@usher.com", cF.email.getValue());
        Assertions.assertEquals(company2, cF.company.getValue());
        Assertions.assertEquals(status1, cF.status.getValue());
    }

    @Test
    public void saveEventHasCorrectValues() {
        ContactForm form = new ContactForm(companies, statuses, products);
        Contact contact = new Contact();
        form.setContact(contact);

        form.firstName.setValue("Jane");
        form.lastName.setValue("Doe");
        form.email.setValue("jane@doe.com");
        form.company.setValue(company1);
        form.status.setValue(status2);
        AtomicReference<Contact> savedContact = new AtomicReference<>(null);
        form.addSaveListener(e -> savedContact.set(e.getContact()));
        form.save.click();
        Contact saved = savedContact.get();
        Assertions.assertEquals("Jane", saved.getFirstName());
        Assertions.assertEquals("Doe", saved.getLastName());
        Assertions.assertEquals("jane@doe.com", saved.getEmail());
        Assertions.assertEquals(company1, saved.getCompany());
        Assertions.assertEquals(status2, saved.getStatus());
    }
}