async function session() {
    const response = await fetch('api/auth/session');
    if (response.ok) location.replace('appointments.html');
}

document.querySelector('#loginForm').addEventListener('submit', async event => {
    event.preventDefault();
    const button = event.submitter;
    const error = document.querySelector('#loginError');
    button.disabled = true;
    error.classList.add('d-none');
    try {
        const response = await fetch('api/auth/login', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(Object.fromEntries(new FormData(event.target)))
        });
        const body = await response.json();
        if (!response.ok) throw Error(body.error || 'Could not sign in');
        location.replace('appointments.html');
    } catch (exception) {
        error.textContent = exception.message;
        error.classList.remove('d-none');
    } finally {
        button.disabled = false;
    }
});

session();
