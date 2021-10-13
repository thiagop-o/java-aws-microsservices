package com.crowtech.projectAWS02.repositories;

import com.crowtech.projectAWS02.models.ProductEventKey;
import com.crowtech.projectAWS02.models.ProductEventLog;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface ProductEventLogRepository extends CrudRepository <ProductEventLog, ProductEventKey>{

    List<ProductEventLog> findAllByPk(String code);

    List<ProductEventLog> findAllByPkAndSkStartsWith(String code, String eventType);

}
