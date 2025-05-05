package com.example.inventorysystem.services;

import com.example.inventorysystem.exceptions.InvalidTransactionException;
import com.example.inventorysystem.models.StockTransaction;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public void validateNewTransaction(StockTransaction transaction) {
        // Validate required fields
        if (transaction.getProductId() == null || transaction.getProductId().trim().isEmpty()) {
            throw new InvalidTransactionException("Product ID cannot be empty");
        }

        if (transaction.getProductName() == null || transaction.getProductName().trim().isEmpty()) {
            throw new InvalidTransactionException("Product Name cannot be empty");
        }

        if (transaction.getQuantity() <= 0) {
            throw new InvalidTransactionException("Quantity must be greater than zero");
        }

        if (transaction.getUserId() == null || transaction.getUserId().trim().isEmpty()) {
            throw new InvalidTransactionException("User ID cannot be empty");
        }

        if (transaction.getTransactionType() == null) {
            throw new InvalidTransactionException("Transaction Type must be specified");
        }
    }

    public void validateExistingTransaction(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction ID cannot be empty");
        }
    }
}