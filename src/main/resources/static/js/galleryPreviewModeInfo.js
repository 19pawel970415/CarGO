document.addEventListener("DOMContentLoaded", function() {
            const startDateInput = document.getElementById("startDate");
            const endDateInput = document.getElementById("endDate");

            if (!startDateInput.value && !endDateInput.value) {
                alert("Entering Start Date, End Date and clicking 'Search Now' enables the rent buttons. For now, you're remaining in the preview mode.");
            }
        });


document.addEventListener("DOMContentLoaded", function () {
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");
    const searchForm = document.getElementById("searchForm");

    // Function to check if the start date is valid
    function isStartDateValid(startDate, endDate) {
        const today = new Date().toISOString().split('T')[0]; // Current date in YYYY-MM-DD format
        const startDateObj = new Date(startDate);
        const endDateObj = new Date(endDate);

        // Validate if start date is not before today
        if (startDate < today) {
            alert("Start date cannot be in the past.");
            return false;
        }

        // Validate if start date is not after the end date
        if (endDate && startDateObj > endDateObj) {
            alert("Start date cannot be later than end date.");
            return false;
        }

        return true;
    }

    // Add event listener to validate dates before form submission
    searchForm.addEventListener("submit", function (event) {
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;

        if (!isStartDateValid(startDate, endDate)) {
            event.preventDefault(); // Prevent form submission
        }
    });
});