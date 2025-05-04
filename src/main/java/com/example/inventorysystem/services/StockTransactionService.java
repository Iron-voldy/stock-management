package com.example.inventorysystem.services;

import com.example.inventorysystem.models.StockTransaction;
import com.example.inventorysystem.repositories.StockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockTransactionService {
    @Autowired
    private StockTransactionRepository transactionRepository;

    // Create a new stock transaction
    public StockTransaction createStockTransaction(
            String productId,
            String productName,
            int quantity,
            StockTransaction.TransactionType transactionType,
            String userId,
            String notes
    ) {
        StockTransaction transaction = new StockTransaction();
        transaction.setId(transaction.generateTransactionId());
        transaction.setProductId(productId);
        transaction.setProductName(productName);
        transaction.setQuantity(quantity);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setUserId(userId);
        transaction.setNotes(notes);

        transactionRepository.saveTransaction(transaction);
        return transaction;
    }

    // Get all stock transactions
    public List<StockTransaction> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    // Get transactions for a specific product
    public List<StockTransaction> getTransactionsByProductId(String productId) {
        return transactionRepository.getTransactionsByProductId(productId);
    }

    // Calculate current stock level for a product
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

    // Get stock status by product
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

    // Identify low stock products (less than 10 units)
    public List<String> getLowStockProducts() {
        Map<String, Integer> stockStatus = getStockStatusByProduct();

        return stockStatus.entrySet().stream()
                .filter(entry -> entry.getValue() < 10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}