const $ = selector => document.querySelector(selector);
let currentAppointment = null;

const api = (url, options = {}) => fetch(url, {
    headers: {'Content-Type': 'application/json'},
    ...options
}).then(async response => {
    const body = await response.json();
    if (!response.ok) throw Error(body.error || 'Request failed');
    return body;
});

function option(select, value, label) {
    const item = document.createElement('option');
    item.value = value;
    item.textContent = label;
    select.append(item);
}

function cell(row, value) {
    const item = document.createElement('td');
    item.textContent = value;
    row.append(item);
}

function detail(label, value) {
    const wrapper = document.createElement('div');
    const term = document.createElement('dt');
    const description = document.createElement('dd');
    term.textContent = label;
    description.textContent = value;
    wrapper.append(term, description);
    return wrapper;
}

function showMessage(message, error = false) {
    const element = $('#message');
    element.textContent = message;
    element.classList.toggle('error', error);
    element.classList.toggle('success', !error && Boolean(message));
}

function currency(value) {
    return `Rs. ${Number(value).toLocaleString('en-LK', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
}

function reportRows(selector, rows, fields) {
    const body = $(selector);
    body.replaceChildren();
    if (!rows.length) {
        const row = document.createElement('tr');
        const empty = document.createElement('td');
        empty.colSpan = fields.length;
        empty.textContent = 'No report data for this selection.';
        row.append(empty);
        body.append(row);
        return;
    }
    rows.forEach(item => {
        const row = document.createElement('tr');
        fields.forEach(field => cell(row, field.format ? field.format(item[field.name]) : item[field.name]));
        body.append(row);
    });
}

async function loadSummary() {
    const summary = await api('api/reports/summary');
    $('#todayAppointments').textContent = summary.todayAppointments;
    $('#activeAppointments').textContent = summary.activeAppointments;
    $('#cancelledAppointments').textContent = summary.cancelledAppointments;
    $('#billsToday').textContent = summary.billsToday;
    $('#revenueToday').textContent = currency(summary.revenueToday);
}

async function loadReports(date = '') {
    const query = date ? `?date=${encodeURIComponent(date)}` : '';
    const [daily, dentists, treatments, revenue] = await Promise.all([
        api(`api/reports/daily${query}`),
        api('api/reports/dentists'),
        api('api/reports/treatments'),
        api('api/reports/revenue')
    ]);
    reportRows('#dailyReportRows', daily, [{name: 'date'}, {name: 'appointments'}]);
    reportRows('#dentistReportRows', dentists, [{name: 'dentist'}, {name: 'appointments'}]);
    reportRows('#treatmentReportRows', treatments, [{name: 'treatment'}, {name: 'appointments'}]);
    reportRows('#revenueReportRows', revenue, [{name: 'date'}, {name: 'bills'}, {name: 'revenue', format: currency}]);
}

async function loadCatalog() {
    const [dentists, treatments] = await Promise.all([
        api('api/catalog/dentists'),
        api('api/catalog/treatments')
    ]);
    dentists.forEach(dentist => {
        option($('#dentistId'), dentist.id, dentist.fullName);
        option($('#editDentistId'), dentist.id, dentist.fullName);
    });
    treatments.forEach(treatment => {
        const label = `${treatment.name} - Rs. ${treatment.price}`;
        option($('#treatmentId'), treatment.id, label);
        option($('#editTreatmentId'), treatment.id, label);
    });
}

function renderAppointment(appointment) {
    currentAppointment = appointment;
    $('#appointmentDetails').replaceChildren(
        detail('Appointment number', appointment.appointmentNumber),
        detail('Patient', appointment.patient.fullName),
        detail('Address', appointment.patient.address),
        detail('Contact number', appointment.patient.contactNumber),
        detail('Dentist', appointment.dentist.fullName),
        detail('Treatment', appointment.treatment.name),
        detail('Date', new Date(`${appointment.date}T00:00:00`).toLocaleDateString('en-LK')),
        detail('Time', appointment.time.slice(0, 5)),
        detail('Status', appointment.status)
    );
    $('#appointmentDetail').classList.remove('hidden');
    $('#editAppointment').classList.toggle('hidden', appointment.status === 'CANCELLED');
    $('#cancelAppointment').classList.toggle('hidden', appointment.status === 'CANCELLED');
    let billLink = $('#viewBill');
    if (!billLink) {
        billLink = document.createElement('a');
        billLink.id = 'viewBill';
        billLink.textContent = 'Open bill';
        billLink.className = 'button-link button-secondary';
        $('#appointmentActions').append(billLink);
    }
    billLink.href = `receipt.html?appointmentNumber=${encodeURIComponent(appointment.appointmentNumber)}`;
}

function fillEditForm() {
    if (!currentAppointment) return;
    const form = $('#editForm');
    form.elements.patientName.value = currentAppointment.patient.fullName;
    form.elements.address.value = currentAppointment.patient.address;
    form.elements.contactNumber.value = currentAppointment.patient.contactNumber;
    form.elements.dentistId.value = currentAppointment.dentist.id;
    form.elements.treatmentId.value = currentAppointment.treatment.id;
    form.elements.appointmentDate.value = currentAppointment.date;
    form.elements.appointmentTime.value = currentAppointment.time;
    $('#editPanel').classList.remove('hidden');
}

async function findAppointment(number) {
    const value = number.trim();
    if (!value) throw Error('Enter an appointment number');
    const appointment = await api(`api/appointments/${encodeURIComponent(value)}`);
    renderAppointment(appointment);
    return appointment;
}

async function load() {
    const rows = $('#rows');
    rows.replaceChildren();
    try {
        const appointments = await api('api/appointments');
        if (!appointments.length) {
            const row = document.createElement('tr');
            const empty = document.createElement('td');
            empty.colSpan = 5;
            empty.textContent = 'No appointments found.';
            row.append(empty);
            rows.append(row);
            return;
        }
        appointments.forEach(appointment => {
            const row = document.createElement('tr');
            row.tabIndex = 0;
            row.addEventListener('click', () => renderAppointment(appointment));
            row.addEventListener('keydown', event => {
                if (event.key === 'Enter' || event.key === ' ') renderAppointment(appointment);
            });
            cell(row, appointment.appointmentNumber);
            cell(row, appointment.patient.fullName);
            cell(row, appointment.dentist.fullName);
            cell(row, `${appointment.date} ${appointment.time}`);
            cell(row, appointment.status);
            rows.append(row);
        });
    } catch (error) {
        showMessage(error.message, true);
    }
}

function showApp(user) {
    $('#login').classList.add('hidden');
    $('#app').classList.remove('hidden');
    $('#staff').textContent = user.fullName;
    Promise.all([loadCatalog(), load(), loadSummary(), loadReports()]).catch(error => showMessage(error.message, true));
}

api('api/auth/session').then(showApp).catch(() => {});

$('#loginForm').onsubmit = async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;
    try {
        showApp(await api('api/auth/login', {
            method: 'POST',
            body: JSON.stringify(Object.fromEntries(new FormData(event.target)))
        }));
    } catch (error) {
        $('#loginError').textContent = error.message;
    } finally {
        button.disabled = false;
    }
};

$('#appointmentForm').onsubmit = async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;
    try {
        const request = Object.fromEntries(new FormData(event.target));
        request.dentistId = Number(request.dentistId);
        request.treatmentId = Number(request.treatmentId);
        const appointment = await api('api/appointments', {method: 'POST', body: JSON.stringify(request)});
        showMessage(`Saved: ${appointment.appointmentNumber}`);
        event.target.reset();
        renderAppointment(appointment);
        await Promise.all([load(), loadSummary()]);
    } catch (error) {
        showMessage(error.message, true);
    } finally {
        button.disabled = false;
    }
};

$('#searchForm').onsubmit = async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;
    try {
        await findAppointment(new FormData(event.target).get('appointmentNumber'));
        showMessage('Appointment found.');
    } catch (error) {
        $('#appointmentDetail').classList.add('hidden');
        showMessage(error.message, true);
    } finally {
        button.disabled = false;
    }
};

$('#editAppointment').onclick = fillEditForm;
$('#closeEdit').onclick = () => $('#editPanel').classList.add('hidden');

$('#editForm').onsubmit = async event => {
    event.preventDefault();
    if (!currentAppointment) return;
    const button = event.submitter;
    button.disabled = true;
    try {
        const request = Object.fromEntries(new FormData(event.target));
        request.dentistId = Number(request.dentistId);
        request.treatmentId = Number(request.treatmentId);
        const appointment = await api(`api/appointments/${encodeURIComponent(currentAppointment.appointmentNumber)}`, {
            method: 'PUT',
            body: JSON.stringify(request)
        });
        renderAppointment(appointment);
        $('#editPanel').classList.add('hidden');
        showMessage('Appointment updated.');
        await Promise.all([load(), loadSummary()]);
    } catch (error) {
        showMessage(error.message, true);
    } finally {
        button.disabled = false;
    }
};

$('#dailyReportForm').onsubmit = async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;
    try {
        await loadReports(new FormData(event.target).get('date'));
        showMessage('Reports updated.');
    } catch (error) {
        showMessage(error.message, true);
    } finally {
        button.disabled = false;
    }
};

$('#cancelAppointment').onclick = async () => {
    if (!currentAppointment || !confirm(`Cancel ${currentAppointment.appointmentNumber}?`)) return;
    $('#cancelAppointment').disabled = true;
    try {
        await api(`api/appointments/${encodeURIComponent(currentAppointment.appointmentNumber)}`, {method: 'DELETE'});
        const appointment = await api(`api/appointments/${encodeURIComponent(currentAppointment.appointmentNumber)}`);
        renderAppointment(appointment);
        showMessage('Appointment cancelled.');
        await Promise.all([load(), loadSummary()]);
    } catch (error) {
        showMessage(error.message, true);
    } finally {
        $('#cancelAppointment').disabled = false;
    }
};

$('#load').onclick = async event => {
    event.currentTarget.disabled = true;
    await load();
    event.currentTarget.disabled = false;
};
$('#logout').onclick = async () => {
    await api('api/auth/logout', {method: 'POST'});
    location.reload();
};

const minimumDate = new Date().toLocaleDateString('en-CA');
$('#appointmentDate').min = minimumDate;
$('#editAppointmentDate').min = minimumDate;
$('#reportDate').value = minimumDate;
