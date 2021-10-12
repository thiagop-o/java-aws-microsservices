package com.crowtech.projectAWS01.services;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.crowtech.projectAWS01.enums.EventType;
import com.crowtech.projectAWS01.models.Envelope;
import com.crowtech.projectAWS01.models.Product;
import com.crowtech.projectAWS01.models.ProductEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProductPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(ProductPublisher.class);
    private AmazonSNS sns;
    private Topic productEventsTopic;
    private ObjectMapper objectMapper;

    public ProductPublisher(AmazonSNS sns, @Qualifier("productEventsTopic")Topic productEventsTopic, ObjectMapper objectMapper) {
        this.sns = sns;
        this.productEventsTopic = productEventsTopic;
        this.objectMapper = objectMapper;
    }

    public void publishProductEvent(Product product, EventType eventType, String username){
        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductId(product.getId());
        productEvent.setCode(product.getCode());
        productEvent.setUserName(username);

        Envelope envelope = new Envelope();
        envelope.setEventType(eventType);


        try {
            envelope.setData(objectMapper.writeValueAsString(productEvent));

            sns.publish(
                    productEventsTopic.getTopicArn(),
                    objectMapper.writeValueAsString(envelope)
            );
        } catch (JsonProcessingException e) {
            LOG.error("Failed to create product event message");
        }
    }
}
