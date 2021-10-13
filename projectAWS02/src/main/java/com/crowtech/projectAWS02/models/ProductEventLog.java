package com.crowtech.projectAWS02.models;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.crowtech.projectAWS02.enums.EventType;
import org.springframework.data.annotation.Id;

@DynamoDBTable(tableName = "product-events")
public class ProductEventLog {
    public ProductEventLog() {
    }

    @Id
    private ProductEventKey productEventKey;

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "eventType")
    private EventType eventType;

    @DynamoDBAttribute(attributeName = "productId")
    private Long productId;

    @DynamoDBAttribute(attributeName = "username")
    private String userName;

    @DynamoDBAttribute(attributeName = "timestamp")
    private Long timestamp;

    @DynamoDBAttribute(attributeName = "ttl")
    private Long ttl;

    @DynamoDBHashKey(attributeName = "pk")
    public String getPk(){
        return this.productEventKey != null ? this.productEventKey.getPk() : null;
    }

    public void setPk(String pk){
        if (this.productEventKey == null){
            this.productEventKey = new ProductEventKey();
        }
        this.productEventKey.setPk(pk);
    }

    @DynamoDBRangeKey(attributeName = "sk")
    public String getSk(){
        return this.productEventKey != null ? this.productEventKey.getSk() : null;
    }

    public void setSk(String sk){
        if (this.productEventKey == null){
            this.productEventKey = new ProductEventKey();
        }
        this.productEventKey.setSk(sk);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}
