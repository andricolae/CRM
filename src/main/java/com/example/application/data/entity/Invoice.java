package com.example.application.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

@Entity
public class Invoice extends AbstractEntity {
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private java.time.LocalDate date;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @NotNull
    private Company company_id;

    @NotNull
    private double total;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.time.LocalDate getDate() {
        return date;
    }

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
}
