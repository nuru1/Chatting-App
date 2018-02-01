package com.example.asif.chat;

/**
 * Created by asif on 28-Jan-18.
 */

public class Req {
    public String request_type;

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public Req() {
    }

    public Req(String request_type) {
        this.request_type = request_type;
    }


}
