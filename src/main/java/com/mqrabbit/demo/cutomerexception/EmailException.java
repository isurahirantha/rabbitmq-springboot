package com.mqrabbit.demo.cutomerexception;

public class EmailException extends Exception {
    public EmailException(String message) {
        super(message);
    }
}
