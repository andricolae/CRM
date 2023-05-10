package com.example.application.data.repository;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Invoice;
import com.example.application.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("select i from Invoice i " +
            "inner join Company c where lower(c.name) like lower(concat('%', :searchTerm, '%'))" +
            "and i.company_id = c.id")
    List<Invoice> search(@Param("searchTerm") String searchTerm);
}
