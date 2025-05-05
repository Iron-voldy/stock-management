package com.example.inventorysystem.controllers;

import com.example.inventorysystem.models.StockTransaction;
import com.example.inventorysystem.services.StockTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockTransactionController {

    @Autowired
    private StockTransactionService stockTransactionService;

    // Stock Entry Page
    @GetMapping("/entry")
    public String showStockEntryPage(Model model) {
        model.addAttribute("transactionTypes", StockTransaction.TransactionType.values());
        return "stock-entry";
    }

    // Process Stock Entry
    @PostMapping("/entry")
    public String processStockEntry(
            @RequestParam String productId,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam StockTransaction.TransactionType transactionType,
            @RequestParam String userId,
            @RequestParam(required = false) String notes,
            Model model
    ) {
        StockTransaction transaction = stockTransactionService.createStockTransaction(
                productId, productName, quantity, transactionType, userId, notes
        );

        model.addAttribute("message", "Stock entry processed successfully!");
        return "stock-entry";
    }

    // Stock Overview Page
    @GetMapping("/overview")
    public String showStockOverviewPage(Model model) {
        model.addAttribute("transactions", stockTransactionService.getAllTransactions());
        model.addAttribute("stockStatus", stockTransactionService.getStockStatusByProduct());
        model.addAttribute("lowStockProducts", stockTransactionService.getLowStockProducts());
        return "stock-overview";
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
            Model model
    ) {
        StockTransaction transaction = stockTransactionService.createStockTransaction(
                productId,
                "Stock Adjustment",
                adjustmentQuantity,
                StockTransaction.TransactionType.ADJUSTMENT,
                userId,
                notes
        );

        model.addAttribute("successMessage", "Stock adjustment processed successfully!");
        return "redirect:/stock/adjustment";
    }
}