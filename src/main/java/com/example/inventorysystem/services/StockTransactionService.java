package com.example.inventorysystem.services;

import com.example.inventorysystem.models.StockTransaction;
import com.example.inventorysystem.repositories.StockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockTransactionService {
    @Autowired
    private StockTransactionRepository transactionRepository;

    // Create a new stock transaction with validation
    public StockTransaction createStockTransaction(
            String productId,
            String productName,
            int quantity,
            StockTransaction.TransactionType transactionType,
            String userId,
            String notes
    ) {
        // Validate input
        validateStockTransaction(productId, productName, quantity, userId);

        StockTransaction transaction = new StockTransaction();
        transaction.setId(transaction.generateTransactionId());
        transaction.setProductId(productId);
        transaction.setProductName(productName);
        transaction.setQuantity(quantity);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setUserId(userId);
        transaction.setNotes(notes);

        return transactionRepository.saveTransaction(transaction);
    }

    // Update an existing transaction
    public StockTransaction updateStockTransaction(
            String transactionId,
            String productId,
            String productName,
            int quantity,
            StockTransaction.TransactionType transactionType,
            String userId,
            String notes
    ) {
        // Validate input
        validateStockTransaction(productId, productName, quantity, userId);

        // Retrieve existing transaction
        Optional<StockTransaction> existingTransaction =
                transactionRepository.getTransactionById(transactionId);

        if (existingTransaction.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found with ID: " + transactionId);
        }

        StockTransaction updatedTransaction = existingTransaction.get();
        updatedTransaction.setProductId(productId);
        updatedTransaction.setProductName(productName);
        updatedTransaction.setQuantity(quantity);
        updatedTransaction.setTransactionType(transactionType);
        updatedTransaction.setUserId(userId);
        updatedTransaction.setNotes(notes);

        return transactionRepository.updateTransaction(updatedTransaction);
    }

    // Delete a transaction by ID
    public void deleteStockTransaction(String transactionId) {
        // Validate transaction exists before deletion
        Optional<StockTransaction> transaction =
                transactionRepository.getTransactionById(transactionId);

        if (transaction.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found with ID: " + transactionId);
        }

        transactionRepository.deleteTransaction(transactionId);
    }

    // Delete all transactions for a specific product
    public void deleteTransactionsByProductId(String productId) {
        // Validate product ID
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        transactionRepository.deleteTransactionsByProductId(productId);
    }

    // Existing methods remain the same...
    public List<StockTransaction> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    public Optional<StockTransaction> getTransactionById(String transactionId) {
        return transactionRepository.getTransactionById(transactionId);
    }

    public List<StockTransaction> getTransactionsByProductId(String productId) {
        return transactionRepository.getTransactionsByProductId(productId);
    }

    // Existing calculation methods...
    public int getCurrentStockLevel(String productId) {
        List<StockTransaction> transactions = getTransactionsByProductId(productId);

        return transactions.stream()
                .mapToInt(transaction -> {
                    switch (transaction.getTransactionType()) {
                        case STOCK_IN:
                            return transaction.getQuantity();
                        case STOCK_OUT:
                            return -transaction.getQuantity();
                        case ADJUSTMENT:
                            return transaction.getQuantity();
                        default:
                            return 0;
                    }
                })
                .sum();
    }

    public Map<String, Integer> getStockStatusByProduct() {
        List<StockTransaction> allTransactions = getAllTransactions();

        return allTransactions.stream()
                .collect(Collectors.groupingBy(
                        StockTransaction::getProductId,
                        Collectors.summingInt(transaction -> {
                            switch (transaction.getTransactionType()) {
                                case STOCK_IN:
                                    return transaction.getQuantity();
                                case STOCK_OUT:
                                    return -transaction.getQuantity();
                                case ADJUSTMENT:
                                    return transaction.getQuantity();
                                default:
                                    return 0;
                            }
                        })
                ));
    }

    public List<String> getLowStockProducts() {
        Map<String, Integer> stockStatus = getStockStatusByProduct();

        return stockStatus.entrySet().stream()
                .filter(entry -> entry.getValue() < 10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Input validation method
    private void validateStockTransaction(
            String productId,
            String productName,
            int quantity,
            String userId
    ) {
        // Validate Product ID
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        // Validate Product Name
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product Name cannot be null or empty");
        }

        // Validate Quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }

        // Validate User ID
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
}