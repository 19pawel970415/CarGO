let carIdToDelete = null;


function openDeleteCarModal(event) {
    const carId = event.target.getAttribute('data-id');
    if (!carId || isNaN(carId)) {
        console.error('Invalid car ID');
        alert('Invalid car ID');
        return;
    }
    carIdToDelete = carId;
    $('#deleteCarModal').modal('show');
}


$(document).on('click', '#noDeleteCarBtn', function() {

    $('#deleteCarModal').modal('hide');


    $('.modal-backdrop').remove();
});


$(document).on('click', '#confirmDeleteCarBtn', function() {
    if (carIdToDelete) {
        deleteCar(carIdToDelete);
        $('#deleteCarModal').modal('hide');
    } else {
        console.error('No valid car ID to delete');
        alert('No valid car ID to delete');
    }
});


function deleteCar(carId) {
    console.log('Sending DELETE request for car ID:', carId);
    fetch(`/car/deleteCar/${carId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            alert('Car deleted successfully!');
            window.location.reload();
        } else if (response.status === 409) {
            return response.text().then(text => { throw new Error(text); });
        } else {
            return response.text().then(text => { throw new Error(text); });
        }
    })
    .catch(error => {
        console.error('Error deleting car:', error);
        alert(error.message);
        $('.modal-backdrop').remove();
        $('body').removeClass('modal-open');
    });
}
