package com.example.inventorysystem.repositories;

import com.example.inventorysystem.models.StockTransaction;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StockTransactionRepository {
    private static final String STOCK_TRANSACTIONS_FILE = "stock_transactions.txt";

    // Create a new stock transaction
    public void saveTransaction(StockTransaction transaction) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STOCK_TRANSACTIONS_FILE, true))) {
            String transactionLine = formatTransactionForFile(transaction);
            writer.write(transactionLine);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error saving stock transaction", e);
        }
    }

    // Read all stock transactions
    public List<StockTransaction> getAllTransactions() {
        List<StockTransaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(STOCK_TRANSACTIONS_FILE))) {
            transactions = reader.lines()
                    .map(this::parseTransactionFromFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            // If file doesn't exist, return empty list
            return transactions;
        }
        return transactions;
    }

    // Get transactions for a specific product
    public List<StockTransaction> getTransactionsByProductId(String productId) {
        return getAllTransactions().stream()
                .filter(t -> t.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    // Format transaction for file storage
    private String formatTransactionForFile(StockTransaction transaction) {
        return String.join("|",
                transaction.getId(),
                transaction.getProductId(),
                transaction.getProductName(),
                String.valueOf(transaction.getQuantity()),
                transaction.getTransactionType().name(),
                transaction.getTransactionDate().toString(),
                transaction.getUserId(),
                transaction.getNotes()
        );
    }

    // Parse transaction from file line
    private StockTransaction parseTransactionFromFile(String line) {
        String[] parts = line.split("\\|");
        StockTransaction transaction = new StockTransaction();
        transaction.setId(parts[0]);
        transaction.setProductId(parts[1]);
        transaction.setProductName(parts[2]);
        transaction.setQuantity(Integer.parseInt(parts[3]));
        transaction.setTransactionType(StockTransaction.TransactionType.valueOf(parts[4]));
        transaction.setTransactionDate(LocalDateTime.parse(parts[5]));
        transaction.setUserId(parts[6]);
        transaction.setNotes(parts.length > 7 ? parts[7] : "");
        return transaction;
    }
}