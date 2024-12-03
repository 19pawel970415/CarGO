let carIdToDelete = null;  // Zmienna do przechowywania id samochodu do usunięcia

// Funkcja, która wywoływana jest po kliknięciu "Delete"
function openDeleteCarModal(event) {
    const carId = event.target.getAttribute('data-id');  // Pobieramy ID samochodu z atrybutu data-id
    if (!carId || isNaN(carId)) {
        console.error('Invalid car ID');
        alert('Invalid car ID');
        return;
    }
    carIdToDelete = carId;  // Zapamiętujemy ID samochodu
    $('#deleteCarModal').modal('show');  // Pokazujemy modal
}

// Funkcja obsługująca kliknięcie na przycisk "Close"
$(document).on('click', '#noDeleteCarBtn', function() {
    // Ukrywamy modal
    $('#deleteCarModal').modal('hide');

    // Usuwamy backdrop, jeśli nie został automatycznie usunięty
    $('.modal-backdrop').remove();
});

// Funkcja, która zostanie wywołana po kliknięciu "Confirm"
$(document).on('click', '#confirmDeleteCarBtn', function() {
    if (carIdToDelete) {
        deleteCar(carIdToDelete);  // Wywołujemy funkcję usuwania
        $('#deleteCarModal').modal('hide');  // Zamykamy modal po kliknięciu "Yes"
    } else {
        console.error('No valid car ID to delete');
        alert('No valid car ID to delete');
    }
});

// Funkcja do usuwania samochodu
function deleteCar(carId) {
    console.log('Sending DELETE request for car ID:', carId);  // Debug log
    fetch(`/car/deleteCar/${carId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            alert('Car deleted successfully!');
            document.getElementById('deleteCarModal').classList.remove('show');
            document.body.classList.remove('modal-open');
            document.querySelector('.modal-backdrop').remove();
            window.location.reload();
        } else {
            return response.text().then(text => { throw new Error(text); });
        }
    })
    .catch(error => {
        console.error('Error deleting car:', error);
        alert('Failed to delete the car.');
    });
}
