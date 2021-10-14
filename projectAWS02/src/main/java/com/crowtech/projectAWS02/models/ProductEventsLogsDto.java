package com.crowtech.projectAWS02.models;

import com.crowtech.projectAWS02.enums.EventType;

public class ProductEventsLogsDto {

    private final String code;
    private final EventType eventType;
    private final Long productId;
    private final String username;
    private final Long timestamp;

    public ProductEventsLogsDto(ProductEventLog productEventLog){
        this.code = productEventLog.getPk();
        this.eventType = productEventLog.getEventType();
        this.productId = productEventLog.getProductId();
        this.username = productEventLog.getUserName();
        this.timestamp = productEventLog.getTimestamp();
    }

    public String getCode() {
        return code;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Long getProductId() {
        return productId;
    }

    public String getUsername() {
        return username;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
