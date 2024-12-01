// Function to open modal and fetch car data via AJAX
function openEditCarModal(event) {
   var carId = event.target.getAttribute('data-id');

   // Sending AJAX request to the server to fetch car details
   fetch('/car/' + carId)
      .then(response => response.json())
      .then(data => {
         // Ustawianie wartości w polach
         document.getElementById('modalCarId').value = carId;
         document.getElementById('carMake').innerText = 'Make: ' + data.make.name;
         document.getElementById('carModel').innerText = 'Model: ' + data.model;
         document.getElementById('carVin').innerText = 'VIN: ' + data.vin;
         document.getElementById('carYear').innerText = 'Year: ' + data.yearOfProduction;
         document.getElementById('carChassisType').innerText = 'Chassis Type: ' + data.chassisType;
         document.getElementById('carGearboxType').innerText = 'Gearbox Type: ' + data.gearboxType;
         document.getElementById('carFuelType').innerText = 'Fuel Type: ' + data.fuelType;
         document.getElementById('carSeatCount').innerText = 'Seat Count: ' + data.seatCount.count;

         // Edytowalne pola
         document.getElementById('carLocationInput').value = data.location.city;
         document.getElementById('carRegistrationNumberInput').value = data.registrationNumber;
         document.getElementById('carPricePerDayInput').value = data.pricePerDay;

         // Show the modal
         $('#editCarModal').modal('show');
      })
      .catch(error => {
         console.error('Error fetching car details:', error);
      });
}

// Handling "Close" button click
document.getElementById('closeEditCarModalBtn').addEventListener('click', function() {
   $('#editCarModal').modal('hide');
   $('.modal-backdrop').remove();
   $('body').removeClass('modal-open');
});

document.getElementById('saveChangesBtn').addEventListener('click', function() {
   var updatedCarData = {
      id: document.getElementById('modalCarId').value,
      location: { city: document.getElementById('carLocationInput').value },
      registrationNumber: document.getElementById('carRegistrationNumberInput').value,
      pricePerDay: parseFloat(document.getElementById('carPricePerDayInput').value)
   };

   fetch('/car/update', {
      method: 'PUT',  // Zakładam, że używamy metody PUT
      headers: {
         'Content-Type': 'application/json'
      },
      body: JSON.stringify(updatedCarData)
   })
      .then(response => {
         if (response.ok) {
            alert('Changes saved successfully!');
            $('#editCarModal').modal('hide');
            $('.modal-backdrop').remove();
            $('body').removeClass('modal-open');
            location.reload();
         } else {
            throw new Error('Failed to save changes.');
         }
      })
      .catch(error => {
         console.error('Error saving changes:', error);
         alert('Could not save changes. Please try again.');
      });
});
