package com.crowtech.projectAWS01.controllers;

import com.crowtech.projectAWS01.enums.EventType;
import com.crowtech.projectAWS01.models.Product;
import com.crowtech.projectAWS01.repositories.ProductRepository;
import com.crowtech.projectAWS01.services.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private ProductRepository productRepository;
    private ProductPublisher productPublisher;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductPublisher productPublisher) {
        this.productRepository = productRepository;
        this.productPublisher = productPublisher;
    }

    @GetMapping
    public Iterable<Product> findAll(){
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product){
        Product productCreated = productRepository.save(product);

        productPublisher.publishProductEvent(productCreated, EventType.PRODUCT_CREATED, "joao");
        return new ResponseEntity<Product>(productCreated, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("id") Long id){
        if (productRepository.existsById(id)){
            product.setId(id);

            Product productUpdated = productRepository.save(product);

            productPublisher.publishProductEvent(productUpdated, EventType.PRODUCT_UPDATED, "Thiago");
            return new ResponseEntity<Product>(productUpdated , HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") Long id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            productRepository.delete(product);
            productPublisher.publishProductEvent(product, EventType.PRODUCT_DELETED, "hannah");
            return new ResponseEntity<Product>(product, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping(path = "/code")
    public ResponseEntity<Product> findByCode (@RequestParam String code){
        Optional<Product> optionalProduct = productRepository.findByCode(code);
        return optionalProduct.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}
