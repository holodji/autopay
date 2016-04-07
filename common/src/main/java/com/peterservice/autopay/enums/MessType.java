package com.peterservice.autopay.enums;

public enum MessType {
    CREDITED("pay.credited"),
    WRITTEN_OFF("pay.writtenOf"),
    STATUS("pay.status");

    String key;



    MessType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
