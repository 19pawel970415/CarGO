document.addEventListener("DOMContentLoaded", function() {

    const subscribeForm = document.querySelector('form[action="/subscribe"]');

    if (subscribeForm) {
        subscribeForm.addEventListener("submit", function(event) {
            event.preventDefault();

            const emailInput = subscribeForm.querySelector('textarea[name="email"]');
            const email = emailInput.value.trim();

            if (email === "") {
                alert("Please enter a valid email address.");
                return;
            }


            const formData = new FormData();
            formData.append("email", email);


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

                alert("Thank you for subscribing!");


                window.scrollTo({
                    top: 0,
                    behavior: "smooth"
                });


                emailInput.value = "";
            })
            .catch(error => {
                alert("There was an error. Please try again later.");
                console.error("Error:", error);
            });
        });
    }
});
