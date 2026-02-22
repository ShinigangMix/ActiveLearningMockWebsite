document.addEventListener("DOMContentLoaded", function() {
    var openFormBtn = document.getElementById("openFormBtn");
    var closeFormBtn = document.getElementById("closeFormBtn");
    var popupForm = document.getElementById("popupForm");
    var disableOutsideForm = document.getElementById("disableOutsideForm");

    openFormBtn.addEventListener("click", function() {
        disableOutsideForm.style.display = "block";
        popupForm.style.display = "block";
    });

    closeFormBtn.addEventListener("click", function() {
        disableOutsideForm.style.display = "none";
        popupForm.style.display = "none";
    });
});