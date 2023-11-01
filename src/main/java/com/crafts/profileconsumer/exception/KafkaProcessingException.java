package com.crafts.profileconsumer.exception;

public class KafkaProcessingException extends Exception {
    public KafkaProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
