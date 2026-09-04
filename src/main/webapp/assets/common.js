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

    function isoDate(value) {
        if (Array.isArray(value)) {
            return `${value[0]}-${String(value[1]).padStart(2, '0')}-${String(value[2]).padStart(2, '0')}`;
        }
        return String(value).slice(0, 10);
    }

    function date(value) {
        return new Date(`${isoDate(value)}T00:00:00`).toLocaleDateString('en-LK');
    }

    function dateTime(value) {
        if (Array.isArray(value)) {
            const [year, month, day, hour = 0, minute = 0, second = 0] = value;
            return new Date(year, month - 1, day, hour, minute, second).toLocaleString('en-LK');
        }
        return new Date(value).toLocaleString('en-LK');
    }

    function time(value) {
        if (Array.isArray(value)) {
            return `${String(value[0]).padStart(2, '0')}:${String(value[1] || 0).padStart(2, '0')}`;
        }
        return String(value).slice(0, 5);
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

    return {api, cell, currency, date, dateTime, isoDate, option, ready, showMessage, time};
})();
