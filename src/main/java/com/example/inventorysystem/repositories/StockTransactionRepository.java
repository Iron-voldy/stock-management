package com.example.inventorysystem.repositories;

import com.example.inventorysystem.models.StockTransaction;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class StockTransactionRepository {
    private static final String STOCK_TRANSACTIONS_FILE = "stock_transactions.txt";
    private static final String TEMP_FILE = "stock_transactions_temp.txt";

    // Create a new stock transaction
    public StockTransaction saveTransaction(StockTransaction transaction) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STOCK_TRANSACTIONS_FILE, true))) {
            String transactionLine = formatTransactionForFile(transaction);
            writer.write(transactionLine);
            writer.newLine();
            return transaction;
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

    // Get transaction by ID
    public Optional<StockTransaction> getTransactionById(String transactionId) {
        return getAllTransactions().stream()
                .filter(t -> t.getId().equals(transactionId))
                .findFirst();
    }

    // Get transactions for a specific product
    public List<StockTransaction> getTransactionsByProductId(String productId) {
        return getAllTransactions().stream()
                .filter(t -> t.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    // Update a transaction
    public StockTransaction updateTransaction(StockTransaction updatedTransaction) {
        try {
            // Read all transactions
            List<StockTransaction> transactions = getAllTransactions();

            // Find and replace the transaction
            boolean found = false;
            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getId().equals(updatedTransaction.getId())) {
                    transactions.set(i, updatedTransaction);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new RuntimeException("Transaction not found");
            }

            // Write all transactions back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(STOCK_TRANSACTIONS_FILE))) {
                for (StockTransaction transaction : transactions) {
                    writer.write(formatTransactionForFile(transaction));
                    writer.newLine();
                }
            }

            return updatedTransaction;
        } catch (IOException e) {
            throw new RuntimeException("Error updating stock transaction", e);
        }
    }

    // Delete a transaction by ID
    public void deleteTransaction(String transactionId) {
        try {
            // Read all transactions
            List<StockTransaction> transactions = getAllTransactions();

            // Remove the specified transaction
            transactions.removeIf(t -> t.getId().equals(transactionId));

            // Write remaining transactions back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(STOCK_TRANSACTIONS_FILE))) {
                for (StockTransaction transaction : transactions) {
                    writer.write(formatTransactionForFile(transaction));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting stock transaction", e);
        }
    }

    // Delete all transactions for a specific product
    public void deleteTransactionsByProductId(String productId) {
        try {
            // Read all transactions
            List<StockTransaction> transactions = getAllTransactions();

            // Remove transactions for the specified product
            transactions.removeIf(t -> t.getProductId().equals(productId));

            // Write remaining transactions back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(STOCK_TRANSACTIONS_FILE))) {
                for (StockTransaction transaction : transactions) {
                    writer.write(formatTransactionForFile(transaction));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting stock transactions for product", e);
        }
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