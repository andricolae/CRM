package com.example.application.data.repository;

import com.example.application.data.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("select c from Company c " +
            "where lower(c.name) like lower(concat('%', :searchTerm, '%')) ")
    List<Company> search(@Param("searchTerm") String searchTerm);
}
