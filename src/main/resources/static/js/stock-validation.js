// Common validation functions for stock operations
function validateStockForm(formId) {
    const form = document.getElementById(formId);

    if (!form) return;

    form.addEventListener('submit', function(event) {
        let isValid = true;

        // Get common form fields
        const productId = form.querySelector('[name="productId"]');
        const quantity = form.querySelector('[name="quantity"]') ||
                         form.querySelector('[name="adjustmentQuantity"]');
        const userId = form.querySelector('[name="userId"]');

        // Validate Product ID if present
        if (productId && !productId.value.trim()) {
            productId.classList.add('is-invalid');
            isValid = false;
        } else if (productId) {
            productId.classList.remove('is-invalid');
        }

        // Validate Quantity if present
        if (quantity && (!quantity.value || parseInt(quantity.value) <= 0)) {
            quantity.classList.add('is-invalid');
            isValid = false;
        } else if (quantity) {
            quantity.classList.remove('is-invalid');
        }

        // Validate User ID if present
        if (userId && !userId.value.trim()) {
            userId.classList.add('is-invalid');
            isValid = false;
        } else if (userId) {
            userId.classList.remove('is-invalid');
        }

        // Additional field validations can be added based on the form

        if (!isValid) {
            event.preventDefault();
        }
    });
}

// Initialize validations when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Validate stock entry form
    validateStockForm('stockEntryForm');

    // Validate stock adjustment form
    validateStockForm('stockAdjustmentForm');

    // Validate stock edit form
    validateStockForm('editTransactionForm');
});