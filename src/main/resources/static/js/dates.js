function validateDates() {
    // Pobieranie wartości dat
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;

    // Kontener na komunikat błędu
    const errorMessage = document.getElementById("error-message");
    errorMessage.textContent = ""; // Wyczyszczenie poprzednich błędów

    // Sprawdzanie, czy daty są uzupełnione
    if (!startDate || !endDate) {
        errorMessage.textContent = "Proszę uzupełnić oba pola daty.";
        return false; // Zatrzymanie przesyłania formularza
    }

    // Sprawdzanie, czy data końcowa jest wcześniejsza niż początkowa
    if (new Date(startDate) > new Date(endDate)) {
        errorMessage.textContent = "Data początkowa nie może być późniejsza niż data końcowa.";
        return false; // Zatrzymanie przesyłania formularza
    }

    return true; // Formularz jest poprawny
}