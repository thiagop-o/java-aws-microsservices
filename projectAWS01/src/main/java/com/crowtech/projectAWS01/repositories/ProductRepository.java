package com.crowtech.projectAWS01.repositories;

import com.crowtech.projectAWS01.models.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findByCode(String code);



}
