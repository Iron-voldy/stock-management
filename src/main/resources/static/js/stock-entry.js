document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('stockEntryForm');

    if (form) {
        form.addEventListener('submit', function(event) {
            let isValid = true;

            // Get form fields
            const productId = document.getElementById('productId');
            const productName = document.getElementById('productName');
            const quantity = document.getElementById('quantity');
            const userId = document.getElementById('userId');

            // Validate Product ID
            if (!productId.value.trim()) {
                productId.classList.add('is-invalid');
                isValid = false;
            } else {
                productId.classList.remove('is-invalid');
            }

            // Validate Product Name
            if (!productName.value.trim()) {
                productName.classList.add('is-invalid');
                isValid = false;
            } else {
                productName.classList.remove('is-invalid');
            }

            // Validate Quantity
            if (!quantity.value || parseInt(quantity.value) <= 0) {
                quantity.classList.add('is-invalid');
                isValid = false;
            } else {
                quantity.classList.remove('is-invalid');
            }

            // Validate User ID
            if (!userId.value.trim()) {
                userId.classList.add('is-invalid');
                isValid = false;
            } else {
                userId.classList.remove('is-invalid');
            }

            if (!isValid) {
                event.preventDefault();
            }
        });
    }
});