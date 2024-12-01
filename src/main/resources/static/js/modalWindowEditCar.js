// Funkcja otwierająca modal
function openEditCarModal(event) {
   // Pobieramy ID samochodu z atrybutu data-id
   var carId = event.target.getAttribute('data-id');

   // Ustawiamy ID w modalu
   document.getElementById('modalCarId').innerText = 'Car id: ' + carId;

   // Otwieramy modal
   $('#editCarModal').modal('show');
}

// Obsługa przycisku "Save changes"
document.getElementById('saveChangesBtn').addEventListener('click', function() {
   alert("Changes saved for car with ID: " + document.getElementById('modalCarId').innerText);
});

// Obsługa przycisku "Close"
document.getElementById('closeEditCarModalBtn').addEventListener('click', function() {
   // Zamykamy modal
   $('#editCarModal').modal('hide');

   // Usuwamy tło (backdrop), jeśli nie znika automatycznie
   $('.modal-backdrop').remove();

   // Zabezpieczamy, aby nie było pozostałości po backdropie
   $('body').removeClass('modal-open');
});