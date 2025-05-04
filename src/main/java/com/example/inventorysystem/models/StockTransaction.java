package com.example.inventorysystem.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTransaction {
    private String id;
    private String productId;
    private String productName;
    private int quantity;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
    private String userId;
    private String notes;

    // Enum for transaction types
    public enum TransactionType {
        STOCK_IN,     // Receiving new stock
        STOCK_OUT,    // Selling or using stock
        ADJUSTMENT    // Correcting stock levels
    }

    // Method to generate unique transaction ID
    public String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}