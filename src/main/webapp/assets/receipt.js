const number = new URLSearchParams(location.search).get('appointmentNumber');
const receipt = document.querySelector('#receipt');
const error = document.querySelector('#receiptError');
const printButton = document.querySelector('#printReceipt');

function item(label, value) {
    const term = document.createElement('dt');
    term.className = 'col-sm-4';
    const description = document.createElement('dd');
    description.className = 'col-sm-8';
    term.textContent = label;
    description.textContent = value;
    return [term, description];
}

function currency(value) {
    return `Rs. ${Number(value).toLocaleString('en-LK', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
}

async function generateBill() {
    if (!number) throw Error('Appointment number is required.');
    const response = await fetch(`api/appointments/${encodeURIComponent(number)}/bill`, {method: 'POST'});
    const bill = await response.json();
    if (!response.ok) throw Error(bill.error || 'Could not generate bill.');
    receipt.replaceChildren(...[
        item('Bill number', bill.billNumber),
        item('Appointment', bill.appointmentNumber),
        item('Patient', bill.patientName),
        item('Dentist', bill.dentistName),
        item('Treatment', bill.treatmentName),
        item('Treatment price', currency(bill.treatmentPrice)),
        item('Consultation fee', currency(bill.consultationFee)),
        item('Total', currency(bill.totalAmount)),
        item('Generated', Clinic.dateTime(bill.generatedAt))
    ].flat());
    printButton.classList.remove('d-none');
}

printButton.onclick = () => window.print();
generateBill().catch(exception => {
    receipt.replaceChildren();
    error.textContent = exception.message;
    error.classList.remove('d-none');
});
