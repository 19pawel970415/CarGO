let searchCompleted = false;

window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    const startDate = urlParams.get('startDate');
    const endDate = urlParams.get('endDate');

    if (startDate && endDate) {
        searchCompleted = true;
    }
};

function setSearchCompleted() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    if (startDate && endDate) {
        searchCompleted = true;
    } else {
        searchCompleted = false;
        alert("Please enter both Start Date and End Date before clicking 'Search Now'.");
    }
}

function checkSearchCompleted(event) {
    if (!searchCompleted) {
        event.preventDefault();
        alert("Enter the dates and click 'Search Now' first!");
    }
}