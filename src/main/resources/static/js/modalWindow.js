let selectedFilter = null;


function openFilterSpecificModal() {
    const addDeleteModal = bootstrap.Modal.getInstance(document.getElementById('addDeleteModal'));


    if (addDeleteModal) {
        addDeleteModal.hide();
    }


    const addDeleteModalElement = document.getElementById('addDeleteModal');
    addDeleteModalElement.addEventListener('hidden.bs.modal', function showSpecificModal() {
        let modalId;


        switch (selectedFilter) {
            case 'pickupLocation':
                modalId = 'locationModal';
                break;
            case 'seatCount':
                modalId = 'seatCountModal';
                break;
            case 'carMake':
                modalId = 'carMakeModal';
                break;
            default:
                alert('Unknown filter selected!');
                return;
        }


        setTimeout(function() {
            const specificModal = new bootstrap.Modal(document.getElementById(modalId));
            specificModal.show();
        }, 100);


        addDeleteModalElement.removeEventListener('hidden.bs.modal', showSpecificModal);
    });
}



function openAddDeleteModal(filter) {
    selectedFilter = filter;
    const manageFiltersModal = bootstrap.Modal.getInstance(document.getElementById('manageFiltersModal'));


    if (manageFiltersModal) {
        manageFiltersModal.hide();
    }


    const manageFiltersModalElement = document.getElementById('manageFiltersModal');
    manageFiltersModalElement.addEventListener('hidden.bs.modal', function openSecondModal() {
        const addDeleteModal = new bootstrap.Modal(document.getElementById('addDeleteModal'));
        addDeleteModal.show();


        manageFiltersModalElement.removeEventListener('hidden.bs.modal', openSecondModal);
    });
}


document.getElementById('pickupLocationBtn').addEventListener('click', function () {
    openAddDeleteModal('pickupLocation');
});
document.getElementById('seatCountBtn').addEventListener('click', function () {
    openAddDeleteModal('seatCount');
});
document.getElementById('carMakeBtn').addEventListener('click', function () {
    openAddDeleteModal('carMake');
});


document.getElementById('deleteBtn').addEventListener('click', openFilterSpecificModal);


document.getElementById('saveCarMakeBtn').addEventListener('click', function () {
    const carMakeInput = document.getElementById('carMakeInput').value.trim();

    if (carMakeInput) {

        fetch('/car-make/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ make: carMakeInput })
        })
        .then(response => {
            if (response.ok) {
                alert('Car Make added successfully!');
                const addCarMakeModal = bootstrap.Modal.getInstance(document.getElementById('addCarMakeModal'));
                addCarMakeModal.hide();


                location.reload();
            } else {
                response.text().then(text => {
                    alert('Error adding Car Make! The Car Make already exists in the database.');
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error adding Car Make! The Car Make already exists in the database.');
        });
    } else {
        alert('Please enter a valid Car Make!');
    }
});




document.getElementById('saveLocationBtn').addEventListener('click', function () {
    const newLocation = document.getElementById('newLocationInput').value.trim();

    if (newLocation) {

        fetch('/add-location', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ location: newLocation })
        })
        .then(response => {
            if (response.ok) {
                alert('Location added successfully!');
                const addLocationModal = bootstrap.Modal.getInstance(document.getElementById('addLocationModal'));
                addLocationModal.hide();


                location.reload();
            } else {
                alert('Error adding location! The location already exists in the database.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error adding location! The location already exists in the database.');
        });
    } else {
        alert('Please enter a valid location!');
    }
});


document.getElementById('saveSeatCountButton').addEventListener('click', function () {
    const seatCountId = document.getElementById('seatCountModalAddlId').value.trim();

    if (seatCountId) {

        fetch('/add-seat-count', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ seatCountId: seatCountId })
        })
        .then(response => {
            if (response.ok) {
                alert('Seat count added successfully!');
                const todoSeatCountModal = bootstrap.Modal.getInstance(document.getElementById('todoSeatCountModal'));
                todoSeatCountModal.hide();


                location.reload();
            } else {
                alert('Error adding seat count! The seat count may already exist or there was a problem.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error adding seat count! Please try again.');
        });
    } else {
        alert('Please select a valid seat count!');
    }
});



document.getElementById('addBtn').addEventListener('click', function () {
    const addDeleteModal = bootstrap.Modal.getInstance(document.getElementById('addDeleteModal'));
    addDeleteModal.hide();

    switch (selectedFilter) {
        case 'pickupLocation':

            const addLocationModal = new bootstrap.Modal(document.getElementById('addLocationModal'));
            addLocationModal.show();
            break;

        case 'seatCount':

            const todoSeatCountModal = new bootstrap.Modal(document.getElementById('todoSeatCountModal'));
            todoSeatCountModal.show();
            break;

        case 'carMake':
            const addCarMakeModal = new bootstrap.Modal(document.getElementById('addCarMakeModal'));
            addCarMakeModal.show();
            break;

        default:
            alert('Unknown filter selected!');
    }
});


document.getElementById('backBtn').addEventListener('click', function () {
    const addDeleteModal = bootstrap.Modal.getInstance(document.getElementById('addDeleteModal'));


    if (addDeleteModal) {
        addDeleteModal.hide();
    }


    const addDeleteModalElement = document.getElementById('addDeleteModal');
    addDeleteModalElement.addEventListener('hidden.bs.modal', function showManageFiltersModal() {
        const manageFiltersModal = new bootstrap.Modal(document.getElementById('manageFiltersModal'));
        manageFiltersModal.show();


        addDeleteModalElement.removeEventListener('hidden.bs.modal', showManageFiltersModal);
    });
});


document.getElementById('backLocationBtn').addEventListener('click', function () {
    const locationModal = bootstrap.Modal.getInstance(document.getElementById('locationModal'));
    locationModal.hide();

    const manageFiltersModal = new bootstrap.Modal(document.getElementById('manageFiltersModal'));
    manageFiltersModal.show();
});

document.getElementById('backSeatCountBtn').addEventListener('click', function () {
    const seatCountModal = bootstrap.Modal.getInstance(document.getElementById('seatCountModal'));
    seatCountModal.hide();

    const manageFiltersModal = new bootstrap.Modal(document.getElementById('manageFiltersModal'));
    manageFiltersModal.show();
});

document.getElementById('backCarMakeBtn').addEventListener('click', function () {
    const carMakeModal = bootstrap.Modal.getInstance(document.getElementById('carMakeModal'));
    carMakeModal.hide();

    const manageFiltersModal = new bootstrap.Modal(document.getElementById('manageFiltersModal'));
    manageFiltersModal.show();
});


document.getElementById('deleteLocationBtn').addEventListener('click', function () {

    const confirmDeleteModal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
    confirmDeleteModal.show();
});


document.getElementById('deleteSeatCountButton').addEventListener('click', function () {

    const confirmDeleteModal = new bootstrap.Modal(document.getElementById('confirmDeleteSeatCountModal'));
    confirmDeleteModal.show();
});


document.getElementById('confirmDeleteModal').addEventListener('hidden.bs.modal', function () {

    const locationModal = new bootstrap.Modal(document.getElementById('locationModal'));
    locationModal.show();
});


document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
    const selectedLocation = document.getElementById('locationToDelete').value;
    if (selectedLocation) {

        fetch('/delete-location', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ location: selectedLocation })
        })
        .then(response => {
            if (response.ok) {

                const locationModal = bootstrap.Modal.getInstance(document.getElementById('locationModal'));
                locationModal.hide();
                const confirmDeleteModal = bootstrap.Modal.getInstance(document.getElementById('confirmDeleteModal'));
                confirmDeleteModal.hide();


                document.getElementById('locationToDelete').value = '';


                alert('Location deleted successfully!');


                location.reload();
            } else {
                alert('Error deleting location! At least one of the cars or/and reservations is assigned to this location. Delete the car(s) and/or the reservation(s) first to delete this location.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error deleting location! At least one of the cars or/and reservations is assigned to this location. Delete the car(s) and/or the reservation(s) first to delete this location.');
        });
    } else {
        alert('Please select a location to delete!');
    }
});


function showConfirmDeleteModal() {
    const confirmDeleteCarMakeModal = new bootstrap.Modal(document.getElementById('confirmDeleteCarMakeModal'));
    confirmDeleteCarMakeModal.show();
}


function hideModals() {
    const carMakeModal = bootstrap.Modal.getInstance(document.getElementById('carMakeModal'));
    if (carMakeModal) {
        carMakeModal.hide();
    }

    const confirmDeleteCarMakeModal = bootstrap.Modal.getInstance(document.getElementById('confirmDeleteCarMakeModal'));
    if (confirmDeleteCarMakeModal) {
        confirmDeleteCarMakeModal.hide();
    }
}


document.getElementById('deleteCarMakeBtn').addEventListener('click', showConfirmDeleteModal);


document.getElementById('confirmDeleteCarMakeBtn').addEventListener('click', function () {
    const selectedCarMake = document.getElementById('carMakeToDelete').value;

    if (!selectedCarMake) {
        alert('Please select a Car Make to delete!');
        return;
    }


    fetch('/delete-car-make', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ make: selectedCarMake })
    })
    .then(response => {
        if (response.ok) {
            // Po pomyślnym usunięciu marki samochodu, ukrywamy modale
            hideModals();


            document.getElementById('carMakeToDelete').value = '';
            alert('Car Make deleted successfully!');

            location.reload();
        } else {
            alert('Error deleting Car Make! At least one of the cars is assigned to this Car Make. Delete the car(s) first.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error deleting Car Make! At least one of the cars is assigned to this Car Make. Delete the car(s) first.');
    });
});


document.getElementById('confirmDeleteSeatCountBtn').addEventListener('click', function () {
    const selectedSeatCountId = document.getElementById('seatCountModalDeleteId').value;

    if (!selectedSeatCountId) {
        alert('Please select a Seat Count to delete!');
        return;
    }


    fetch('/delete-seat-count', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ seatCountId: selectedSeatCountId })
    })
    .then(response => {
        if (response.ok) {

            hideModals();


            document.getElementById('seatCountModalDeleteId').value = '';
            alert('Seat Count deleted successfully!');


            location.reload();
        } else {
            alert('Error deleting Seat Count! It is assigned to a car or cars. Delete the car(s) before deleting this Seat Count.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error deleting Seat Count! It is assigned to a car or cars. Delete the car(s) before deleting this Seat Count.');
    });
});




document.addEventListener('shown.bs.modal', function () {
    const scrollbarWidth = window.innerWidth - document.documentElement.clientWidth;
    if (scrollbarWidth > 0) {
        document.body.style.paddingRight = `${scrollbarWidth}px`;
    }
});
document.addEventListener('hidden.bs.modal', function () {
    document.body.style.paddingRight = '';
});
