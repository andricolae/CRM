package com.example.application.data.repository;

import com.example.application.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p " +
            "where lower(p.name) like lower(concat('%', :searchTerm, '%')) ")
    List<Product> search(@Param("searchTerm") String searchTerm);

}
