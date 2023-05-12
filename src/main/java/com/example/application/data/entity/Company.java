package com.example.application.data.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Formula;

import java.util.LinkedList;
import java.util.List;

@Entity
public class Company extends AbstractEntity {
    @NotBlank
    private String name;

    private String cif;

    private String com;

    private String address;

    private String tel;

    private String email;

    @OneToMany(mappedBy = "company")
    @Nullable
    private List<Contact> employees = new LinkedList<>();

    @Formula("(select count(c.id) from Contact c where c.company_id = id)")
    private int employeeCount;
    public int getEmployeeCount() {
        return employeeCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public List<Contact> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Contact> employees) {
        this.employees = employees;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Client: " +
                name + '\n' +
                "cif: '" + cif + '\n' +
                "address: " + address + '\n' +
                "tel: " + tel + '\n' +
                "email: " + email + '\n';
    }
}
