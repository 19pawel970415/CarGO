document.addEventListener("DOMContentLoaded", function() {
            const startDateInput = document.getElementById("startDate");
            const endDateInput = document.getElementById("endDate");

            if (!startDateInput.value && !endDateInput.value) {
                alert("Entering Start Date, End Date and clicking 'Search Now' enables the rent buttons. For now, you're remaining in the preview mode.");
            }
        });