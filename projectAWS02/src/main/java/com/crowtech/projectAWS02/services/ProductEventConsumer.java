package com.crowtech.projectAWS02.services;

import com.crowtech.projectAWS02.models.Envelope;
import com.crowtech.projectAWS02.models.ProductEvent;
import com.crowtech.projectAWS02.models.SnsMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;

@Service
public class ProductEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(
            ProductEventConsumer.class
    );

    private ObjectMapper objectMapper;

    @Autowired
    public ProductEventConsumer(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receivedProductEvent(TextMessage textMessage) throws JMSException, IOException{

        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        Envelope envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);
        ProductEvent productEvent = objectMapper.readValue(envelope.getData(), ProductEvent.class);

        LOG.info("Product event received - Event: {} - ProductId: {} - ", envelope.getEventType(), productEvent.getProductId());


    }



}
