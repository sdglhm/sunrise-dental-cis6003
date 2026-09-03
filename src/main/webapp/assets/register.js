async function loadCatalog() {
    const [dentists, treatments, patients] = await Promise.all([Clinic.api('api/catalog/dentists'), Clinic.api('api/catalog/treatments'), Clinic.api('api/patients')]);
    dentists.forEach(dentist => Clinic.option(document.querySelector('#dentistId'), dentist.id, dentist.fullName));
    treatments.forEach(treatment => Clinic.option(document.querySelector('#treatmentId'), treatment.id, `${treatment.name} - ${Clinic.currency(treatment.price)}`));
    patients.forEach(patient => Clinic.option(document.querySelector('#patientId'), patient.id, `${patient.fullName} (${patient.contactNumber})`));
    document.querySelector('#patientId').addEventListener('change', event => {
        const patient = patients.find(item => item.id === Number(event.target.value));
        if (!patient) return;
        document.querySelector('#patientName').value = patient.fullName;
        document.querySelector('#address').value = patient.address;
        document.querySelector('#contactNumber').value = patient.contactNumber;
    });
}

document.querySelector('#appointmentForm').addEventListener('submit', async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;
    try {
        const request = Object.fromEntries(new FormData(event.target));
        request.patientId = request.patientId ? Number(request.patientId) : null;
        request.dentistId = Number(request.dentistId);
        request.treatmentId = Number(request.treatmentId);
        const appointment = await Clinic.api('api/appointments', {method: 'POST', body: JSON.stringify(request)});
        location.assign(`appointments.html?appointmentNumber=${encodeURIComponent(appointment.appointmentNumber)}`);
    } catch (exception) {
        Clinic.showMessage(exception.message, true);
    } finally {
        button.disabled = false;
    }
});

document.querySelector('#appointmentDate').min = new Date().toLocaleDateString('en-CA');
Clinic.ready().then(loadCatalog).catch(exception => Clinic.showMessage(exception.message, true));
