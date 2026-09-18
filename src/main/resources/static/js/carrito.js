const deleteButtons = document.querySelectorAll(".delete-btn");

deleteButtons.forEach(btn => {

    btn.addEventListener("click", () => {

        const item = btn.closest(".cart-item");

        item.style.opacity = "0";

        setTimeout(() => {
            item.remove();
        }, 300);

    });

});