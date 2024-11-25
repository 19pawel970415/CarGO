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

document.addEventListener("DOMContentLoaded", function() {
    const priceMinInput = document.getElementById("priceMin");
    const priceMaxInput = document.getElementById("priceMax");
    const yearMinInput = document.getElementById("yearMin");
    const yearMaxInput = document.getElementById("yearMax");
    const searchForm = document.getElementById("searchForm");

    // Function to check if price range is valid
    function isPriceRangeValid() {
        const priceMin = parseFloat(priceMinInput.value);
        const priceMax = parseFloat(priceMaxInput.value);

        if (priceMin > priceMax) {
            alert("Min price cannot be greater than max price.");
            return false;
        }

        if (priceMax < priceMin) {
            alert("Max price cannot be less than min price.");
            return false;
        }

        return true;
    }

    // Function to check if year range is valid
    function isYearRangeValid() {
        const yearMin = parseInt(yearMinInput.value);
        const yearMax = parseInt(yearMaxInput.value);

        if (yearMin > yearMax) {
            alert("Min year cannot be greater than max year.");
            return false;
        }

        if (yearMax < yearMin) {
            alert("Max year cannot be less than min year.");
            return false;
        }

        return true;
    }

    // Add event listener to validate before form submission
    searchForm.addEventListener("submit", function(event) {
        if (!isPriceRangeValid() || !isYearRangeValid()) {
            event.preventDefault(); // Prevent form submission if validation fails
        }
    });
});