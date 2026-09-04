function reportRows(selector, rows, fields) {
    const body = document.querySelector(selector);
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
        fields.forEach(field => Clinic.cell(row, field.format ? field.format(item[field.name]) : item[field.name]));
        body.append(row);
    });
}

async function loadSummary() {
    const summary = await Clinic.api('api/reports/summary');
    document.querySelector('#todayAppointments').textContent = summary.todayAppointments;
    document.querySelector('#activeAppointments').textContent = summary.activeAppointments;
    document.querySelector('#cancelledAppointments').textContent = summary.cancelledAppointments;
    document.querySelector('#billsToday').textContent = summary.billsToday;
    document.querySelector('#revenueToday').textContent = Clinic.currency(summary.revenueToday);
}

async function loadReports(date = '') {
    const query = date ? `?date=${encodeURIComponent(date)}` : '';
    const [daily, dentists, treatments, revenue] = await Promise.all([Clinic.api(`api/reports/daily${query}`), Clinic.api('api/reports/dentists'), Clinic.api('api/reports/treatments'), Clinic.api('api/reports/revenue')]);
    reportRows('#dailyReportRows', daily, [{name: 'date', format: Clinic.date}, {name: 'appointments'}]);
    reportRows('#dentistReportRows', dentists, [{name: 'dentist'}, {name: 'appointments'}]);
    reportRows('#treatmentReportRows', treatments, [{name: 'treatment'}, {name: 'appointments'}]);
    reportRows('#revenueReportRows', revenue, [{name: 'date', format: Clinic.date}, {name: 'bills'}, {name: 'revenue', format: Clinic.currency}]);
}

document.querySelector('#dailyReportForm').addEventListener('submit', async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;
    try { await loadReports(new FormData(event.target).get('date')); Clinic.showMessage('Reports updated.'); }
    catch (exception) { Clinic.showMessage(exception.message, true); }
    finally { button.disabled = false; }
});
document.querySelector('#reportDate').value = new Date().toLocaleDateString('en-CA');
Clinic.ready().then(() => Promise.all([loadSummary(), loadReports()])).catch(exception => Clinic.showMessage(exception.message, true));
