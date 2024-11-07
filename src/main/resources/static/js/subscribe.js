document.addEventListener("DOMContentLoaded", function() {
    // Znajdź formularz subskrypcji
    const subscribeForm = document.querySelector('form[action="/subscribe"]');

    if (subscribeForm) {
        subscribeForm.addEventListener("submit", function(event) {
            event.preventDefault();  // Zatrzymaj domyślne przesyłanie formularza

            const emailInput = subscribeForm.querySelector('textarea[name="email"]');
            const email = emailInput.value.trim();

            if (email === "") {
                alert("Please enter a valid email address.");
                return;
            }

            // Przygotowanie danych do wysłania
            const formData = new FormData();
            formData.append("email", email);

            // Wyślij żądanie POST do serwera
            fetch("/subscribe", {
                method: "POST",
                body: formData
            })
            .then(response => {
                if (response.ok) {
                    return response.text();
                } else {
                    throw new Error("Subscription failed.");
                }
            })
            .then(data => {
                // Pokaż wiadomość o sukcesie
                alert("Thank you for subscribing!");

                // Przewiń stronę na górę
                window.scrollTo({
                    top: 0,
                    behavior: "smooth"
                });

                // Wyczyść pole e-mail
                emailInput.value = "";
            })
            .catch(error => {
                alert("There was an error. Please try again later.");
                console.error("Error:", error);
            });
        });
    }
});
