package com.crowtech.projectAWS02.controllers;

import com.crowtech.projectAWS02.models.ProductEventsLogsDto;
import com.crowtech.projectAWS02.repositories.ProductEventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class ProductEventLogController {

    private ProductEventLogRepository productEventLogRepository;

    @Autowired
    public ProductEventLogController(ProductEventLogRepository productEventLogRepository) {
        this.productEventLogRepository = productEventLogRepository;
    }

    @GetMapping("/events")
    public List<ProductEventsLogsDto> getAllEvents(){
        return StreamSupport.stream(productEventLogRepository.findAll().spliterator(), false)
                .map(ProductEventsLogsDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}")
    public List<ProductEventsLogsDto> getByCode(@PathVariable String code){
        return productEventLogRepository.findAllByPk(code)
                .stream()
                .map(ProductEventsLogsDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}/{event}")
    public List<ProductEventsLogsDto> findByCodeAndEventType(@PathVariable String code,
                                                             @PathVariable String event){
        return productEventLogRepository.findAllByPkAndSkStartsWith(code,event)
                .stream()
                .map(ProductEventsLogsDto::new)
                .collect(Collectors.toList());
    }

}
