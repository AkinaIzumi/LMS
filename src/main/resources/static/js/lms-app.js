document.addEventListener("DOMContentLoaded", () => {
    
    // ============================
    // 1. DataTables Initialization
    // ============================
    document.querySelectorAll(".datatable").forEach((table) => {
        if (window.jQuery && window.jQuery.fn.DataTable) {
            window.jQuery(table).DataTable({
                responsive: true,
                pageLength: 10,
                lengthMenu: [5, 10, 25, 50],
            });
        }
    });

    // ============================
    // 2. Sidebar Toggle
    // ============================
    const sidebar = document.querySelector(".sidebar");
    document.querySelectorAll("[data-sidebar-toggle]").forEach((button) => {
        button.addEventListener("click", () => sidebar?.classList.toggle("show"));
    });

    // ============================
    // 3. Delete Confirmation Modal (JS Fetch)
    // ============================
    const deleteModal = document.getElementById("deleteModal");
    let deleteUrl = "";

    if (deleteModal) {
        deleteModal.addEventListener("show.bs.modal", function (event) {
            const button = event.relatedTarget;
            deleteUrl = button.getAttribute("data-delete-url");
            const itemName = button.getAttribute("data-delete-name") || "this item";

            const targetName = deleteModal.querySelector("#deleteItemName");
            if (targetName) targetName.textContent = itemName;
        });

        const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
        if (confirmDeleteBtn) {
            confirmDeleteBtn.addEventListener("click", function () {
                if (!deleteUrl) return;

                fetch(deleteUrl, {
                    method: 'DELETE',
                })
                .then(response => {
                    if (response.ok) {
                        const modalInstance = bootstrap.Modal.getInstance(deleteModal);
                        modalInstance.hide();
                        window.location.reload(); 
                    } else {
                        alert("Failed to delete the item.");
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert("An error occurred.");
                });
            });
        }

        deleteModal.addEventListener("hide.bs.modal", function () {
            if (document.activeElement && deleteModal.contains(document.activeElement)) {
                document.activeElement.blur();
            }
        });
    }

    // ============================
    // 4. Show Password Toggle
    // ============================
    const showPassword = document.querySelector("#showPassword");
    const password = document.querySelector("#password");
    showPassword?.addEventListener("change", () => {
        if (password) password.type = showPassword.checked ? "text" : "password";
    });

    // ============================
    // 5. Return Confirmation Modal & Fetch
    // ============================
    const returnModal = document.getElementById("returnModal");
    let returnUrl = "";

    if (returnModal) {
        returnModal.addEventListener("show.bs.modal", function(event) {
            const button = event.relatedTarget;
            returnUrl = button.getAttribute("data-return-url");
            const bookName = button.getAttribute("data-book") || "this book";

            const bookNameElement = document.getElementById("bookName");
            if (bookNameElement) bookNameElement.textContent = bookName;
        });

        const confirmReturnBtn = document.getElementById("confirmReturnBtn");
        if (confirmReturnBtn) {
            confirmReturnBtn.addEventListener("click", function() {
                if (!returnUrl) return;

                const statusSelect = document.getElementById("returnStatusSelect"); 
                const returnStatus = statusSelect ? statusSelect.value : "RETURNED";

                fetch(returnUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ returnStatus: returnStatus }) 
                })
                .then(response => response.text())
                .then(message => {
                    alert(message);
                    const modalInstance = bootstrap.Modal.getInstance(returnModal);
                    modalInstance.hide();
                    window.location.reload(); 
                })
            });
        }
    }

});