package com.example.inventorysystem.controllers;

import com.example.inventorysystem.models.StockTransaction;
import com.example.inventorysystem.services.StockTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequestMapping("/stock")
public class StockTransactionController {
    private static final Logger logger = Logger.getLogger(StockTransactionController.class.getName());

    @Autowired
    private StockTransactionService stockTransactionService;

    // Stock Entry Page - GET
    @GetMapping("/entry")
    public String showStockEntryPage(Model model) {
        model.addAttribute("transactionTypes", StockTransaction.TransactionType.values());
        model.addAttribute("transaction", new StockTransaction());
        return "stock-entry";
    }

    // Stock Entry Process - POST (Create Operation)
    @PostMapping("/entry")
    public String processStockEntry(
            @RequestParam String productId,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam StockTransaction.TransactionType transactionType,
            @RequestParam String userId,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes
    ) {
        try {
            StockTransaction transaction = stockTransactionService.createStockTransaction(
                    productId, productName, quantity, transactionType, userId, notes
            );

            logger.info("Stock transaction created: " + transaction.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Stock entry processed successfully!");
            return "redirect:/stock/overview";
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to create stock transaction: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stock/entry";
        }
    }

    // Stock Overview Page - GET (Read Operation)
    @GetMapping("/overview")
    public String showStockOverviewPage(Model model) {
        model.addAttribute("transactions", stockTransactionService.getAllTransactions());
        model.addAttribute("stockStatus", stockTransactionService.getStockStatusByProduct());
        model.addAttribute("lowStockProducts", stockTransactionService.getLowStockProducts());
        return "stock-overview";
    }

    // Transaction Details Page - GET
    @GetMapping("/transaction/{transactionId}")
    public String viewTransactionDetails(
            @PathVariable String transactionId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Optional<StockTransaction> transaction =
                    stockTransactionService.getTransactionById(transactionId);

            if (transaction.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Transaction not found");
                return "redirect:/stock/overview";
            }

            model.addAttribute("transaction", transaction.get());
            return "stock-transaction-details";
        } catch (Exception e) {
            logger.warning("Error viewing transaction details: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error retrieving transaction details");
            return "redirect:/stock/overview";
        }
    }

    // Edit Transaction Page - GET
    @GetMapping("/edit/{transactionId}")
    public String showEditTransactionPage(
            @PathVariable String transactionId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Optional<StockTransaction> transaction =
                    stockTransactionService.getTransactionById(transactionId);

            if (transaction.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Transaction not found");
                return "redirect:/stock/overview";
            }

            model.addAttribute("transaction", transaction.get());
            model.addAttribute("transactionTypes",
                    StockTransaction.TransactionType.values());
            return "stock-edit";
        } catch (Exception e) {
            logger.warning("Error loading edit transaction page: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error loading transaction for editing");
            return "redirect:/stock/overview";
        }
    }

    // Update Transaction - POST (Update Operation)
    @PostMapping("/edit")
    public String processTransactionEdit(
            @RequestParam String transactionId,
            @RequestParam String productId,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam StockTransaction.TransactionType transactionType,
            @RequestParam String userId,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes
    ) {
        try {
            StockTransaction updatedTransaction =
                    stockTransactionService.updateStockTransaction(
                            transactionId, productId, productName, quantity,
                            transactionType, userId, notes
                    );

            logger.info("Transaction updated: " + transactionId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Transaction updated successfully!");
            return "redirect:/stock/overview";
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to update transaction: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stock/edit/" + transactionId;
        }
    }

    // Delete Transaction - GET (Delete Operation)
    @GetMapping("/delete/{transactionId}")
    public String deleteTransaction(
            @PathVariable String transactionId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            stockTransactionService.deleteStockTransaction(transactionId);

            logger.info("Transaction deleted: " + transactionId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Transaction deleted successfully!");
            return "redirect:/stock/overview";
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to delete transaction: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stock/overview";
        }
    }

    // Delete All Transactions for a Product
    @GetMapping("/delete-product/{productId}")
    public String deleteProductTransactions(
            @PathVariable String productId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            stockTransactionService.deleteTransactionsByProductId(productId);

            logger.info("All transactions deleted for product: " + productId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "All transactions for product deleted successfully!");
            return "redirect:/stock/overview";
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to delete product transactions: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stock/overview";
        }
    }

    // Stock Adjustment Page
    @GetMapping("/adjustment")
    public String showStockAdjustmentPage(Model model) {
        model.addAttribute("stockStatus", stockTransactionService.getStockStatusByProduct());
        return "stock-adjustment";
    }

    // Process Stock Adjustment
    @PostMapping("/adjustment")
    public String processStockAdjustment(
            @RequestParam String productId,
            @RequestParam int adjustmentQuantity,
            @RequestParam String userId,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes
    ) {
        try {
            StockTransaction transaction = stockTransactionService.createStockTransaction(
                    productId,
                    "Stock Adjustment",
                    adjustmentQuantity,
                    StockTransaction.TransactionType.ADJUSTMENT,
                    userId,
                    notes
            );

            logger.info("Stock adjustment processed: " + transaction.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Stock adjustment processed successfully!");
            return "redirect:/stock/overview";
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to process stock adjustment: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stock/adjustment";
        }
    }
}