package com.devansh.pixsel.model;

public class SmsInfo {

    public String to;
    public String text;
    public String imageurl;

    public SmsInfo(String to, String text, String imageurl) {
        this.to = to;
        this.text = text;
        this.imageurl = imageurl;
    }
}
