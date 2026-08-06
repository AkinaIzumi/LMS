document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".datatable").forEach((table) => {
        if (window.jQuery && window.jQuery.fn.DataTable) {
            window.jQuery(table).DataTable({
                responsive: true,
                pageLength: 10,
                lengthMenu: [5, 10, 25, 50],
            });
        }
    });

    const sidebar = document.querySelector(".sidebar");
    document.querySelectorAll("[data-sidebar-toggle]").forEach((button) => {
        button.addEventListener("click", () => sidebar?.classList.toggle("show"));
    });

    document.addEventListener("DOMContentLoaded", () => {
    // ============================
    // Delete Confirmation Modal (JS Fetch)
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

        // Delete ခလုတ်ကို နှိပ်လိုက်သည့်အခါ
        const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
        if (confirmDeleteBtn) {
            confirmDeleteBtn.addEventListener("click", function () {
                if (!deleteUrl) return;

                fetch(deleteUrl, {
                    method: 'DELETE',
                })
                .then(response => {
                    if (response.ok) {
                        // အောင်မြင်ပါက Modal ကိုပိတ်ပြီး Page ကို Reload လုပ်မည် (သို့မဟုတ် Table row ကို ဖြုတ်မည်)
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

        // Focus fix for aria-hidden warning
        deleteModal.addEventListener("hide.bs.modal", function () {
            if (document.activeElement && deleteModal.contains(document.activeElement)) {
                document.activeElement.blur();
            }
        });
    }
});

    const showPassword = document.querySelector("#showPassword");
    const password = document.querySelector("#password");
    showPassword?.addEventListener("change", () => {
        if (password) password.type = showPassword.checked ? "text" : "password";
    });

    // ============================
    // Return Confirmation Modal
    // ============================

    const returnModal = document.getElementById("returnModal");

    if (returnModal) {
        returnModal.addEventListener("show.bs.modal", function(event) {

            const button = event.relatedTarget;

            const bookName = button.getAttribute("data-book");

            document.getElementById("bookName").textContent = bookName;

        });
    }

});