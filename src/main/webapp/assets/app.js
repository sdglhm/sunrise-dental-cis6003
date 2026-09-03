let currentAppointment = null;

function detail(label, value) {
    const term = document.createElement('dt');
    term.className = 'col-sm-4 text-body-secondary';
    term.textContent = label;
    const description = document.createElement('dd');
    description.className = 'col-sm-8';
    description.textContent = value;
    return [term, description];
}

async function loadCatalog() {
    const [dentists, treatments] = await Promise.all([Clinic.api('api/catalog/dentists'), Clinic.api('api/catalog/treatments')]);
    dentists.forEach(dentist => Clinic.option(document.querySelector('#editDentistId'), dentist.id, dentist.fullName));
    treatments.forEach(treatment => Clinic.option(document.querySelector('#editTreatmentId'), treatment.id, `${treatment.name} - ${Clinic.currency(treatment.price)}`));
}

function renderAppointment(appointment) {
    currentAppointment = appointment;
    const details = [
        ['Appointment number', appointment.appointmentNumber], ['Patient', appointment.patient.fullName],
        ['Address', appointment.patient.address], ['Contact number', appointment.patient.contactNumber],
        ['Dentist', appointment.dentist.fullName], ['Treatment', appointment.treatment.name],
        ['Date', new Date(`${appointment.date}T00:00:00`).toLocaleDateString('en-LK')],
        ['Time', appointment.time.slice(0, 5)], ['Status', appointment.status]
    ];
    document.querySelector('#appointmentDetails').replaceChildren(...details.flatMap(item => detail(item[0], item[1])));
    document.querySelector('#appointmentDetail').classList.remove('d-none');
    document.querySelector('#editAppointment').classList.toggle('d-none', appointment.status === 'CANCELLED');
    document.querySelector('#cancelAppointment').classList.toggle('d-none', appointment.status === 'CANCELLED');
    let billLink = document.querySelector('#viewBill');
    if (!billLink) {
        billLink = document.createElement('a');
        billLink.id = 'viewBill';
        billLink.className = 'btn btn-outline-secondary btn-sm';
        billLink.textContent = 'Open bill';
        document.querySelector('#appointmentActions').append(billLink);
    }
    billLink.href = `receipt.html?appointmentNumber=${encodeURIComponent(appointment.appointmentNumber)}`;
}

function fillEditForm() {
    if (!currentAppointment) return;
    const form = document.querySelector('#editForm');
    form.elements.patientName.value = currentAppointment.patient.fullName;
    form.elements.address.value = currentAppointment.patient.address;
    form.elements.contactNumber.value = currentAppointment.patient.contactNumber;
    form.elements.dentistId.value = currentAppointment.dentist.id;
    form.elements.treatmentId.value = currentAppointment.treatment.id;
    form.elements.appointmentDate.value = currentAppointment.date;
    form.elements.appointmentTime.value = currentAppointment.time.slice(0, 5);
    document.querySelector('#editPanel').classList.remove('d-none');
}

async function findAppointment(number) {
    const value = number.trim();
    if (!value) throw Error('Enter an appointment number');
    const appointment = await Clinic.api(`api/appointments/${encodeURIComponent(value)}`);
    renderAppointment(appointment);
}

async function load() {
    const body = document.querySelector('#rows');
    body.replaceChildren();
    const appointments = await Clinic.api('api/appointments');
    if (!appointments.length) {
        const row = document.createElement('tr');
        const empty = document.createElement('td');
        empty.colSpan = 5;
        empty.textContent = 'No appointments found.';
        row.append(empty);
        body.append(row);
        return;
    }
    appointments.forEach(appointment => {
        const row = document.createElement('tr');
        row.tabIndex = 0;
        row.addEventListener('click', () => renderAppointment(appointment));
        row.addEventListener('keydown', event => {
            if (event.key === 'Enter' || event.key === ' ') renderAppointment(appointment);
        });
        Clinic.cell(row, appointment.appointmentNumber);
        Clinic.cell(row, appointment.patient.fullName);
        Clinic.cell(row, appointment.dentist.fullName);
        Clinic.cell(row, `${appointment.date} ${appointment.time.slice(0, 5)}`);
        Clinic.cell(row, appointment.status);
        body.append(row);
    });
}

document.querySelector('#searchForm').addEventListener('submit', async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;
    try {
        await findAppointment(new FormData(event.target).get('appointmentNumber'));
        Clinic.showMessage('Appointment found.');
    } catch (exception) {
        document.querySelector('#appointmentDetail').classList.add('d-none');
        Clinic.showMessage(exception.message, true);
    } finally {
        button.disabled = false;
    }
});

document.querySelector('#editAppointment').addEventListener('click', fillEditForm);
document.querySelector('#closeEdit').addEventListener('click', () => document.querySelector('#editPanel').classList.add('d-none'));
document.querySelector('#editForm').addEventListener('submit', async event => {
    event.preventDefault();
    if (!currentAppointment) return;
    const button = event.submitter;
    button.disabled = true;
    try {
        const request = Object.fromEntries(new FormData(event.target));
        request.dentistId = Number(request.dentistId);
        request.treatmentId = Number(request.treatmentId);
        const appointment = await Clinic.api(`api/appointments/${encodeURIComponent(currentAppointment.appointmentNumber)}`, {method: 'PUT', body: JSON.stringify(request)});
        renderAppointment(appointment);
        document.querySelector('#editPanel').classList.add('d-none');
        Clinic.showMessage('Appointment updated.');
        await load();
    } catch (exception) {
        Clinic.showMessage(exception.message, true);
    } finally {
        button.disabled = false;
    }
});

document.querySelector('#cancelAppointment').addEventListener('click', async event => {
    if (!currentAppointment || !confirm(`Cancel ${currentAppointment.appointmentNumber}?`)) return;
    event.currentTarget.disabled = true;
    try {
        await Clinic.api(`api/appointments/${encodeURIComponent(currentAppointment.appointmentNumber)}`, {method: 'DELETE'});
        renderAppointment(await Clinic.api(`api/appointments/${encodeURIComponent(currentAppointment.appointmentNumber)}`));
        Clinic.showMessage('Appointment cancelled.');
        await load();
    } catch (exception) {
        Clinic.showMessage(exception.message, true);
    } finally {
        event.currentTarget.disabled = false;
    }
});

document.querySelector('#load').addEventListener('click', async event => {
    event.currentTarget.disabled = true;
    try { await load(); } catch (exception) { Clinic.showMessage(exception.message, true); }
    finally { event.currentTarget.disabled = false; }
});

document.querySelector('#editAppointmentDate').min = new Date().toLocaleDateString('en-CA');
Clinic.ready().then(async () => {
    await Promise.all([loadCatalog(), load()]);
    const number = new URLSearchParams(location.search).get('appointmentNumber');
    if (number) {
        document.querySelector('#appointmentNumber').value = number;
        await findAppointment(number);
        Clinic.showMessage('Appointment saved.');
    }
}).catch(exception => Clinic.showMessage(exception.message, true));
