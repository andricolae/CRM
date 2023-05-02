package com.example.application.data.service;

import com.example.application.data.entity.Product;
import com.example.application.data.repository.CompanyRepository;
import com.example.application.data.repository.ContactRepository;
import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import com.example.application.data.repository.ProductRepository;
import com.example.application.data.repository.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CRMService {
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final StatusRepository statusRepository;
    private final ProductRepository productRepository;

    public CRMService(ContactRepository contactRepository,
                      CompanyRepository companyRepository,
                      StatusRepository statusRepository,
                      ProductRepository productRepository) {

        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.statusRepository = statusRepository;
        this.productRepository = productRepository;
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
