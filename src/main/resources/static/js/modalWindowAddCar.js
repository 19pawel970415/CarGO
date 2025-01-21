document.getElementById('carImageInput').addEventListener('click', function () {
    alert('Choose only .jpg files. The default size of the photos is 224 x 150. Any other size may cause an unexpected result on the website!');
});

document.getElementById('addCarBtn').addEventListener('click', function () {

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
    const imageFile = document.getElementById('carImageInput').files[0];


    if (!make || !model || !vin || !registrationNumber || !yearOfProduction || !chassisType || !gearboxType || !fuelType || !seatCount || !pricePerDay || !location || !imageFile) {
        alert('All fields must be filled, including the image!');
        return;
    }


    const currentYear = new Date().getFullYear();
    if (yearOfProduction < 1900 || yearOfProduction > currentYear) {
        alert('Year of production must be between 1900 and ' + currentYear + '.');
        return;
    }


        if (imageFile && !imageFile.name.toLowerCase().endsWith('.jpg')) {
            alert('The format of the photo is incorrect! Choose a .jpg file!');
            return;
        }


    const formData = new FormData();
    const carData = {
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

    formData.append('carData', new Blob([JSON.stringify(carData)], { type: 'application/json' }));
    formData.append('image', imageFile);


    fetch('/car/add', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            alert('Car added successfully!');
            document.getElementById('addCarModal').classList.remove('show');
            document.body.classList.remove('modal-open');
            document.querySelector('.modal-backdrop').remove();
            window.location.reload();
        } else {
            return response.text().then(text => { throw new Error(text); });
        }
    })
    .catch(error => {
        console.error('Error adding car:', error);

         if (error.message.includes('Car with the same VIN already exists')) {
              alert('A car with the same VIN already exists!');
         } else if (error.message.includes('Car with the same Registration Number already exists')) {
              alert('A car with the same Registration Number already exists!');
         } else {
              alert('An error occurred while adding the car.');
         }
    });
});


function updateSuggestions(inputId, dataListId, url) {
    const input = document.getElementById(inputId);
    const dataList = document.getElementById(dataListId);

    input.addEventListener('input', function () {
        const query = input.value;

        if (query.length > 0) {
            fetch(`${url}?query=${encodeURIComponent(query)}`)
                .then(response => response.json())
                .then(data => {
                    dataList.innerHTML = '';
                    data.forEach(item => {
                        const option = document.createElement('option');
                        option.value = item;
                        dataList.appendChild(option);
                    });
                })
                .catch(error => console.error('Error fetching suggestions:', error));
        }
    });
}


updateSuggestions('carMakeInputAdd', 'carMakeSuggestions', '/api/car-makes');
updateSuggestions('carLocationInputAdd', 'locationSuggestions', '/api/locations');

