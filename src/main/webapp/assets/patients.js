let patients = [];

function render(items) {
    const body = document.querySelector('#patientRows');
    body.replaceChildren();
    if (!items.length) {
        const row = document.createElement('tr');
        const cell = document.createElement('td');
        cell.colSpan = 4;
        cell.textContent = 'No patients found.';
        row.append(cell);
        body.append(row);
        return;
    }
    items.forEach(patient => {
        const row = document.createElement('tr');
        Clinic.cell(row, patient.fullName);
        Clinic.cell(row, patient.contactNumber);
        Clinic.cell(row, patient.address);
        const action = document.createElement('td');
        const button = document.createElement('button');
        button.className = 'btn btn-outline-primary btn-sm';
        button.type = 'button';
        button.textContent = 'Edit';
        button.addEventListener('click', () => edit(patient));
        action.append(button);
        row.append(action);
        body.append(row);
    });
}

function edit(patient) {
    const form = document.querySelector('#patientForm');
    form.elements.id.value = patient.id;
    form.elements.fullName.value = patient.fullName;
    form.elements.address.value = patient.address;
    form.elements.contactNumber.value = patient.contactNumber;
    document.querySelector('#patientEditorTitle').textContent = 'Edit patient';
    document.querySelector('#patientEditor').classList.remove('d-none');
}

async function load() {
    patients = await Clinic.api('api/patients');
    render(patients);
}

document.querySelector('#patientSearch').addEventListener('input', event => {
    const query = event.target.value.trim().toLowerCase();
    render(patients.filter(patient => patient.fullName.toLowerCase().includes(query) || patient.contactNumber.toLowerCase().includes(query)));
});
document.querySelector('#addPatient').addEventListener('click', () => {
    document.querySelector('#patientForm').reset();
    document.querySelector('#patientEditorTitle').textContent = 'Add patient';
    document.querySelector('#patientEditor').classList.remove('d-none');
});
document.querySelector('#closePatientEditor').addEventListener('click', () => document.querySelector('#patientEditor').classList.add('d-none'));
document.querySelector('#patientForm').addEventListener('submit', async event => {
    event.preventDefault();
    const button = event.submitter;
    const values = Object.fromEntries(new FormData(event.target));
    button.disabled = true;
    try {
        const method = values.id ? 'PUT' : 'POST';
        const url = values.id ? `api/patients/${encodeURIComponent(values.id)}` : 'api/patients';
        await Clinic.api(url, {method, body: JSON.stringify({fullName: values.fullName, address: values.address, contactNumber: values.contactNumber})});
        document.querySelector('#patientEditor').classList.add('d-none');
        Clinic.showMessage(values.id ? 'Patient updated.' : 'Patient added.');
        await load();
    } catch (exception) {
        Clinic.showMessage(exception.message, true);
    } finally {
        button.disabled = false;
    }
});
Clinic.ready().then(load).catch(exception => Clinic.showMessage(exception.message, true));
