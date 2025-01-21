function validateDates() {

    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;


    const errorMessage = document.getElementById("error-message");
    errorMessage.textContent = "";


    if (!startDate || !endDate) {
        errorMessage.textContent = "Proszę uzupełnić oba pola daty.";
        return false;
    }


    if (new Date(startDate) > new Date(endDate)) {
        errorMessage.textContent = "Data początkowa nie może być późniejsza niż data końcowa.";
        return false;
    }

    return true;
}