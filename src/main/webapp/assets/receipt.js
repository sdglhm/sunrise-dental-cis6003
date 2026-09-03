const number = new URLSearchParams(location.search).get('appointmentNumber');
const receipt = document.querySelector('#receipt');
const error = document.querySelector('#receiptError');
const printButton = document.querySelector('#printReceipt');

function item(label, value) {
    const wrapper = document.createElement('div');
    const term = document.createElement('dt');
    const description = document.createElement('dd');
    term.textContent = label;
    description.textContent = value;
    wrapper.append(term, description);
    return wrapper;
}

function currency(value) {
    return `Rs. ${Number(value).toLocaleString('en-LK', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
}

async function generateBill() {
    if (!number) throw Error('Appointment number is required.');
    const response = await fetch(`api/appointments/${encodeURIComponent(number)}/bill`, {method: 'POST'});
    const bill = await response.json();
    if (!response.ok) throw Error(bill.error || 'Could not generate bill.');
    receipt.replaceChildren(
        item('Bill number', bill.billNumber),
        item('Appointment', bill.appointmentNumber),
        item('Patient', bill.patientName),
        item('Dentist', bill.dentistName),
        item('Treatment', bill.treatmentName),
        item('Treatment price', currency(bill.treatmentPrice)),
        item('Consultation fee', currency(bill.consultationFee)),
        item('Total', currency(bill.totalAmount)),
        item('Generated', new Date(bill.generatedAt).toLocaleString('en-LK'))
    );
    printButton.classList.remove('hidden');
}

printButton.onclick = () => window.print();
generateBill().catch(exception => {
    receipt.replaceChildren();
    error.textContent = exception.message;
});
