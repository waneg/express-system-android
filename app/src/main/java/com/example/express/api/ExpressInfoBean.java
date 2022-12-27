package com.example.express.api;

public class ExpressInfoBean {
    private String expressId;
    private String sender;
    private String receiver;
    private String origin;
    private String destination;
    private String courierId;

    public ExpressInfoBean(){}

    public ExpressInfoBean(String expressId, String sender, String receiver, String origin, String destination, String courierId) {
        this.expressId = expressId;
        this.sender = sender;
        this.receiver = receiver;
        this.origin = origin;
        this.destination = destination;
        this.courierId = courierId;
    }

    public String getExpressId() {
        return expressId;
    }

    public void setExpressId(String expressId) {
        this.expressId = expressId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCourierId() {
        return courierId;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }
}
