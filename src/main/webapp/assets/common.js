const Clinic = (() => {
    async function api(url, options = {}) {
        const response = await fetch(url, {headers: {'Content-Type': 'application/json'}, ...options});
        if (response.status === 401) {
            location.replace('index.html');
            throw Error('Authentication required');
        }
        const body = await response.json();
        if (!response.ok) throw Error(body.error || 'Request failed');
        return body;
    }

    function showMessage(message, error = false) {
        const element = document.querySelector('#message');
        if (!element) return;
        element.textContent = message;
        element.className = `alert ${error ? 'alert-danger' : 'alert-success'}`;
        element.classList.toggle('d-none', !message);
    }

    function cell(row, value) {
        const item = document.createElement('td');
        item.textContent = value;
        row.append(item);
    }

    function option(select, value, label) {
        const item = document.createElement('option');
        item.value = value;
        item.textContent = label;
        select.append(item);
    }

    function currency(value) {
        return `Rs. ${Number(value).toLocaleString('en-LK', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
    }

    async function ready() {
        const user = await api('api/auth/session');
        document.querySelectorAll('[data-staff-name]').forEach(element => element.textContent = user.fullName);
        document.querySelectorAll('[data-logout]').forEach(button => button.addEventListener('click', async () => {
            await api('api/auth/logout', {method: 'POST'});
            location.replace('index.html');
        }));
        return user;
    }

    return {api, cell, currency, option, ready, showMessage};
})();
