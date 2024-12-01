// Function to open modal and fetch car data via AJAX
function openEditCarModal(event) {
   var carId = event.target.getAttribute('data-id');

   // Sending AJAX request to the server to fetch car details
   fetch('/car/' + carId)  // Change the endpoint based on your backend setup
      .then(response => response.json())
      .then(data => {
         // Setting the car details in the modal
         document.getElementById('carMake').innerText = 'Make: ' + data.make.name;
         document.getElementById('carModel').innerText = 'Model: ' + data.model;
         document.getElementById('carVin').innerText = 'VIN: ' + data.vin;
         document.getElementById('carYear').innerText = 'Year: ' + data.yearOfProduction;
         document.getElementById('carChassisType').innerText = 'Chassis Type: ' + data.chassisType;
         document.getElementById('carGearboxType').innerText = 'Gearbox Type: ' + data.gearboxType;
         document.getElementById('carFuelType').innerText = 'Fuel Type: ' + data.fuelType;
         document.getElementById('carSeatCount').innerText = 'Seat Count: ' + data.seatCount.count;
         document.getElementById('carLocation').innerText = 'Location: ' + data.location.city;
         document.getElementById('carRegistrationNumber').innerText = 'Registration Number: ' + data.registrationNumber;
         document.getElementById('carPricePerDay').innerText = 'Price Per Day: ' + data.pricePerDay;

         // Show the modal
         $('#editCarModal').modal('show');
      })
      .catch(error => {
         console.error('Error fetching car details:', error);
      });
}

// Handling "Save changes" button click
document.getElementById('saveChangesBtn').addEventListener('click', function() {
   alert("Changes saved for car with ID: " + document.getElementById('modalCarId').innerText);
});

// Handling "Close" button click
document.getElementById('closeEditCarModalBtn').addEventListener('click', function() {
   $('#editCarModal').modal('hide');
   $('.modal-backdrop').remove();
   $('body').removeClass('modal-open');
});