package com.example.monster.sampleproject.Entities;

public class Post{

    public String id;
    public String radio_name;
    public String stream_url;
    public String status;
    public String operation_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRadio_name() {
        return radio_name;
    }

    public void setRadio_name(String radio_name) {
        this.radio_name = radio_name;
    }

    public String getStream_url() {
        return stream_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperation_time() {
        return operation_time;
    }

    public void setOperation_time(String operation_time) {
        this.operation_time = operation_time;
    }
}