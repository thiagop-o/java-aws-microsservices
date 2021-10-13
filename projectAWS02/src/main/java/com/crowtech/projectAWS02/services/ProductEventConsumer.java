package com.crowtech.projectAWS02.services;

import com.crowtech.projectAWS02.models.Envelope;
import com.crowtech.projectAWS02.models.ProductEvent;
import com.crowtech.projectAWS02.models.ProductEventLog;
import com.crowtech.projectAWS02.models.SnsMessage;
import com.crowtech.projectAWS02.repositories.ProductEventLogRepository;
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
import java.time.Duration;
import java.time.Instant;

@Service
public class ProductEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(
            ProductEventConsumer.class
    );

    private ObjectMapper objectMapper;
    private ProductEventLogRepository productEventLogRepository;

    @Autowired
    public ProductEventConsumer(ObjectMapper objectMapper, ProductEventLogRepository productEventLogRepository){
        this.objectMapper = objectMapper;
        this.productEventLogRepository = productEventLogRepository;
    }

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receivedProductEvent(TextMessage textMessage) throws JMSException, IOException{

        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        Envelope envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);
        ProductEvent productEvent = objectMapper.readValue(envelope.getData(), ProductEvent.class);

        LOG.info("Product event received - Event: {} - ProductId: {} - ", envelope.getEventType(), productEvent.getProductId());

        ProductEventLog productEventLog = buildProductEventLog(envelope, productEvent);
        productEventLogRepository.save(productEventLog);

    }

    private ProductEventLog buildProductEventLog(Envelope envelope, ProductEvent productEvent){
        Long timestamp = Instant.now().toEpochMilli();

        ProductEventLog productEventLog = new ProductEventLog();
        productEventLog.setPk(productEvent.getCode());
        productEventLog.setSk(envelope.getEventType() + "_" + timestamp);
        productEventLog.setEventType(envelope.getEventType());
        productEventLog.setProductId(productEventLog.getProductId());
        productEventLog.setUserName(productEventLog.getUserName());
        productEventLog.setTimestamp(timestamp);
        productEventLog.setTtl(Instant.now().plus(Duration.ofMinutes(10)).getEpochSecond());

        return productEventLog;

    }


}
