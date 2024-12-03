document.getElementById('addCarBtn').addEventListener('click', function () {
    // Pobranie wartości z formularza
    const make = document.getElementById('carMakeInputAdd').value;
    const model = document.getElementById('carModelInputAdd').value;
    const vin = document.getElementById('carVinInput').value;
    const registrationNumber = document.getElementById('carRegInput').value;
    const yearOfProduction = parseInt(document.getElementById('carYearInput').value);
    const chassisType = document.getElementById('carChassisTypeInput').value;
    const gearboxType = document.getElementById('carGearboxTypeInput').value;
    const fuelType = document.getElementById('carFuelTypeInput').value;
    const seatCount = document.getElementById('carSeatCountInput').value;
    const pricePerDay = parseFloat(document.getElementById('carPriceInput').value);
    const location = document.getElementById('carLocationInputAdd').value;

    // Walidacja
    if (!make || !model || !vin || !registrationNumber || !yearOfProduction || !chassisType || !gearboxType || !fuelType || !seatCount || !pricePerDay || !location) {
        alert('All fields must be filled!');
        return; // Zatrzymanie dalszego działania, jeśli któreś pole jest puste
    }

    // Walidacja roku produkcji
    const currentYear = new Date().getFullYear();
    if (yearOfProduction < 1900 || yearOfProduction > currentYear) {
        alert('Year of production must be between 1900 and ' + currentYear + '.');
        return;
    }

    // Tworzenie obiektu z danymi samochodu
    const newCarData = {
        make: { name: make },
        model: model,
        vin: vin,
        registrationNumber: registrationNumber,
        yearOfProduction: yearOfProduction,
        chassisType: chassisType,
        gearboxType: gearboxType,
        fuelType: fuelType,
        seatCount: { id: parseInt(seatCount) },
        pricePerDay: pricePerDay,
        location: { city: location }
    };

    // Wysłanie danych na serwer
    fetch('/car/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newCarData)
    })
    .then(response => {
        if (response.ok) {
            alert('Car added successfully!');
            document.getElementById('addCarModal').classList.remove('show');
            document.body.classList.remove('modal-open');
            document.querySelector('.modal-backdrop').remove();
            console.log(location);  // Check if 'location' is the native location object
            window.location.reload();
        } else {
            return response.text().then(text => { throw new Error(text); });
        }
    })
    .catch(error => {
        console.error('Error adding car:', error);
        // Show appropriate alert based on error message
         if (error.message.includes('Car with the same VIN already exists')) {
              alert('A car with the same VIN already exists!');
         } else if (error.message.includes('Car with the same Registration Number already exists')) {
              alert('A car with the same Registration Number already exists!');
         }
    });
});