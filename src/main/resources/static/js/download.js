function downloadFile() {
    const userConfirmed = confirm('Download "We are CarGo!" file to read more about us. Do you agree to download the file?');

    if (userConfirmed) {
        window.location.href = '/downloadFile';
    }
}
