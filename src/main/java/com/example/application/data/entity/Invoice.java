package com.example.application.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Invoice {

    @NotNull
    @Id
    @Column(name = "id", unique = true, nullable = false)
    //@GeneratedValue(generator = "emp_seq", strategy = GenerationType.SEQUENCE)
    //@SequenceGenerator(name="emp_seq", sequenceName = "emp_sequence", initialValue =3000, allocationSize = 1)
    private Long id;

    @NotNull
    private java.time.LocalDate date;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @NotNull
    private Company company_id;

    @NotNull
    private double total;
    private boolean paid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.time.LocalDate getDate() {
        return date;
    }

    public Invoice() {}

    public void setDate(java.time.LocalDate date) {
        this.date = date;
    }

    public Company getCompany() {
        return company_id;
    }

    public void setCompany(Company company_id) {
        this.company_id = company_id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
