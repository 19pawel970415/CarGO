let selectedFilter = null; // Przechowywanie wybranego filtra

// Funkcja do otwierania modala odpowiedniego dla filtra
function openFilterSpecificModal() {
    const addDeleteModal = bootstrap.Modal.getInstance(document.getElementById('addDeleteModal'));

    // Zamknij drugi modal, jeśli jest otwarty
    if (addDeleteModal) {
        addDeleteModal.hide();
    }

    // Poczekaj na zamknięcie drugiego modala, zanim otworzysz odpowiedni
    const addDeleteModalElement = document.getElementById('addDeleteModal');
    addDeleteModalElement.addEventListener('hidden.bs.modal', function showSpecificModal() {
        let modalId;

        // Wybór odpowiedniego modala na podstawie wybranego filtra
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

        // Otwórz wybrany modal
        const specificModal = new bootstrap.Modal(document.getElementById(modalId));
        specificModal.show();

        // Usuń nasłuchiwanie, aby uniknąć wielokrotnego wywołania
        addDeleteModalElement.removeEventListener('hidden.bs.modal', showSpecificModal);
    });
}

// Funkcja do otwierania drugiego modala
function openAddDeleteModal(filter) {
    selectedFilter = filter; // Zapisanie wybranego filtra
    const manageFiltersModal = bootstrap.Modal.getInstance(document.getElementById('manageFiltersModal'));

    // Zamknij pierwszy modal, jeśli jest otwarty
    if (manageFiltersModal) {
        manageFiltersModal.hide();
    }

    // Poczekaj na zamknięcie pierwszego modala, zanim otworzysz drugi
    const manageFiltersModalElement = document.getElementById('manageFiltersModal');
    manageFiltersModalElement.addEventListener('hidden.bs.modal', function openSecondModal() {
        const addDeleteModal = new bootstrap.Modal(document.getElementById('addDeleteModal'));
        addDeleteModal.show();

        // Usuń nasłuchiwanie, aby uniknąć wielokrotnego wywołania
        manageFiltersModalElement.removeEventListener('hidden.bs.modal', openSecondModal);
    });
}

// Obsługa przycisków w pierwszym oknie modalnym
document.getElementById('pickupLocationBtn').addEventListener('click', function () {
    openAddDeleteModal('pickupLocation');
});
document.getElementById('seatCountBtn').addEventListener('click', function () {
    openAddDeleteModal('seatCount');
});
document.getElementById('carMakeBtn').addEventListener('click', function () {
    openAddDeleteModal('carMake');
});

// Obsługa przycisku Delete w drugim oknie modalnym
document.getElementById('deleteBtn').addEventListener('click', openFilterSpecificModal);

// Obsługa przycisku Add w drugim oknie modalnym (na razie brak specyficznej logiki)
document.getElementById('addBtn').addEventListener('click', function () {
    alert('Add action triggered!');
    const addDeleteModal = bootstrap.Modal.getInstance(document.getElementById('addDeleteModal'));
    addDeleteModal.hide(); // Zamknij drugie okno modalne
});

// Funkcja obsługi przycisku Back
document.getElementById('backBtn').addEventListener('click', function () {
    const addDeleteModal = bootstrap.Modal.getInstance(document.getElementById('addDeleteModal'));

    // Zamknij modal Add/Delete
    if (addDeleteModal) {
        addDeleteModal.hide();
    }

    // Poczekaj, aż modal Add/Delete zostanie zamknięty, zanim otworzy się Manage Filters
    const addDeleteModalElement = document.getElementById('addDeleteModal');
    addDeleteModalElement.addEventListener('hidden.bs.modal', function showManageFiltersModal() {
        const manageFiltersModal = new bootstrap.Modal(document.getElementById('manageFiltersModal'));
        manageFiltersModal.show();

        // Usuń nasłuchiwanie, aby uniknąć wielokrotnego wywołania
        addDeleteModalElement.removeEventListener('hidden.bs.modal', showManageFiltersModal);
    });
});

// Nasłuchiwanie na przyciski Back dla konkretnych modalów
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

// Obsługa przycisku Delete w modal Location
document.getElementById('deleteLocationBtn').addEventListener('click', function () {
    // Pokaż modal z potwierdzeniem usunięcia
    const confirmDeleteModal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
    confirmDeleteModal.show();
});

// Obsługa przycisku Anuluj w modalu potwierdzenia
document.getElementById('confirmDeleteModal').addEventListener('hidden.bs.modal', function () {
    // Po zamknięciu modala potwierdzenia, nie robimy nic więcej (pozostajemy w modalu Location)
    const locationModal = new bootstrap.Modal(document.getElementById('locationModal'));
    locationModal.show();
});

// Obsługa potwierdzenia usunięcia
document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
    const selectedLocation = document.getElementById('locationToDelete').value;
    if (selectedLocation) {
        // Wyślij zapytanie AJAX do serwera w celu usunięcia lokalizacji
        fetch('/delete-location', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ location: selectedLocation }) // Przesyłamy wybraną lokalizację
        })
        .then(response => {
            if (response.ok) {
                // Po pomyślnym usunięciu lokalizacji zamknij modale
                const locationModal = bootstrap.Modal.getInstance(document.getElementById('locationModal'));
                locationModal.hide();
                const confirmDeleteModal = bootstrap.Modal.getInstance(document.getElementById('confirmDeleteModal'));
                confirmDeleteModal.hide();

                // Odśwież stronę po usunięciu
                location.reload();  // lub window.location.reload();
            } else {
                alert('Error deleting location!');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error deleting location!');
        });
    } else {
        alert('Please select a location to delete!');
    }
});

// Zapobieganie kumulacji stylów Bootstrap
document.addEventListener('shown.bs.modal', function () {
    const scrollbarWidth = window.innerWidth - document.documentElement.clientWidth;
    if (scrollbarWidth > 0) {
        document.body.style.paddingRight = `${scrollbarWidth}px`;
    }
});
document.addEventListener('hidden.bs.modal', function () {
    document.body.style.paddingRight = ''; // Resetowanie stylu po zamknięciu modala
});
