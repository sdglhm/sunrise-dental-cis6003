function renderRows(selector, items, values, emptyText) {
    const body = document.querySelector(selector);
    body.replaceChildren();
    if (!items.length) {
        const row = document.createElement('tr');
        const empty = document.createElement('td');
        empty.colSpan = values.length;
        empty.textContent = emptyText;
        row.append(empty);
        body.append(row);
        return;
    }
    items.forEach(item => {
        const row = document.createElement('tr');
        values.forEach(value => Clinic.cell(row, value(item)));
        body.append(row);
    });
}

async function load() {
    const [dentists, treatments] = await Promise.all([Clinic.api('api/catalog/dentists'), Clinic.api('api/catalog/treatments')]);
    renderRows('#dentistRows', dentists, [item => item.fullName, () => 'Active'], 'No active dentists found.');
    renderRows('#treatmentRows', treatments, [item => item.name, item => Clinic.currency(item.price)], 'No active treatments found.');
}

document.querySelector('#dentistForm').addEventListener('submit', async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;
    try {
        const dentist = await Clinic.api('api/catalog/dentists', {method: 'POST', body: JSON.stringify(Object.fromEntries(new FormData(event.target)))});
        Clinic.showMessage(`Added dentist: ${dentist.fullName}`);
        event.target.reset();
        await load();
    } catch (exception) { Clinic.showMessage(exception.message, true); }
    finally { button.disabled = false; }
});

document.querySelector('#treatmentForm').addEventListener('submit', async event => {
    event.preventDefault();
    const button = event.submitter;
    button.disabled = true;
    try {
        const request = Object.fromEntries(new FormData(event.target));
        request.price = Number(request.price);
        const treatment = await Clinic.api('api/catalog/treatments', {method: 'POST', body: JSON.stringify(request)});
        Clinic.showMessage(`Added treatment: ${treatment.name}`);
        event.target.reset();
        await load();
    } catch (exception) { Clinic.showMessage(exception.message, true); }
    finally { button.disabled = false; }
});

Clinic.ready().then(load).catch(exception => Clinic.showMessage(exception.message, true));
