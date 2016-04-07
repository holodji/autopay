package com.peterservice.autopay;

import org.springframework.context.support.GenericXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        new GenericXmlApplicationContext("/spring/root-context.xml");
    }
}
