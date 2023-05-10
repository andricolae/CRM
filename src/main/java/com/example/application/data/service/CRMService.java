package com.example.application.data.service;

import com.example.application.data.entity.*;
import com.example.application.data.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CRMService {
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final StatusRepository statusRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;

    public CRMService(ContactRepository contactRepository,
                      CompanyRepository companyRepository,
                      StatusRepository statusRepository,
                      ProductRepository productRepository,
                      InvoiceRepository invoiceRepository) {

        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.statusRepository = statusRepository;
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
    }
    public List<Contact> findAllContacts(String filterText) {
        if(filterText == null || filterText.isEmpty())
            return contactRepository.findAll();
        else {
            System.out.println(contactRepository.searchCompany(filterText));
            return contactRepository.search(filterText);
        }
    }

    public List<Contact> findAllByCompany(String filterText) {
        if(filterText == null || filterText.isEmpty())
            return contactRepository.findAll();
        else {
            //System.out.println(contactRepository.searchCompany(filterText));
            return contactRepository.searchCompany(filterText);
        }
    }

    public List<Company> findAllCompanies(String filterText) {
        if (filterText == null || filterText.isEmpty())
            return companyRepository.findAll();
        else
            return companyRepository.search(filterText);
    }

    public List<Product> findAllProducts(String filterText) {
        if (filterText == null || filterText.isEmpty())
            return productRepository.findAll();
        else
            return productRepository.search(filterText);
    }

    public List<Invoice> findAllInvoices(String filterText) {
        if (filterText == null || filterText.isEmpty())
            return invoiceRepository.findAll();
        else
            return invoiceRepository.search(filterText);
    }

    public long countContacts(){
        return contactRepository.count();
    }
    public void deleteContact(Contact contact){
        contactRepository.delete(contact);
    }
    public void deleteCompany(Company company){
        companyRepository.delete(company);
    }
    public void deleteProduct(Product product){
        productRepository.delete(product);
    }

    public void saveContact(Contact contact){
        if(contact == null) {
            System.err.println("Contact is null!");
            return;
        }
        contactRepository.save(contact);
    }
    public void saveCompany(Company company){
        if(company == null) {
            System.err.println("Company is null!");
            return;
        }
        companyRepository.save(company);
    }
    public void saveProduct(Product product) {
        if(product == null) {
            System.err.println("Product is null!");
            return;
        }
        productRepository.save(product);
    }

    public void saveInvoice(Invoice invoice) {
        if(invoice == null) {
            System.err.println("Invoice is null!");
            return;
        }
        invoiceRepository.save(invoice);
    }

    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }
    public List<Status> findAllStatuses() {
        return statusRepository.findAll();
    }
    public List<Product> retrieveProducts() {
        return productRepository.findAll();
    }
}
