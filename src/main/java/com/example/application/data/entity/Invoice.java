package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

@Entity
public class Invoice extends AbstractEntity {
    @NotNull
    private Long id;

    @NotNull
    private Date date;

    @NotNull
    private int company_id;

    @NotNull
    private double total;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
