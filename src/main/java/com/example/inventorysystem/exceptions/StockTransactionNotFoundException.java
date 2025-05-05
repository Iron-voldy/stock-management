package com.example.inventorysystem.exceptions;

public class StockTransactionNotFoundException extends RuntimeException {
    public StockTransactionNotFoundException(String message) {
        super(message);
    }

    public StockTransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}