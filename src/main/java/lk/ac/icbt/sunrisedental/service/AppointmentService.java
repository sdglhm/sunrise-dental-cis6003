package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.AppointmentDao;
import lk.ac.icbt.sunrisedental.dao.CatalogDao;
import lk.ac.icbt.sunrisedental.dto.AppointmentRequest;
import lk.ac.icbt.sunrisedental.exception.NotFoundException;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class AppointmentService {
    private final AppointmentDao appointmentDao;
    private final CatalogDao catalogDao;

    public AppointmentService(AppointmentDao appointmentDao, CatalogDao catalogDao) {
        this.appointmentDao = appointmentDao; this.catalogDao = catalogDao;
    }
    public Appointment create(AppointmentRequest request) {
        validate(request);
        Dentist dentist = dentist(request.dentistId()); Treatment treatment = treatment(request.treatmentId());
        if (appointmentDao.hasActiveSlot(dentist.id(), request.appointmentDate(), request.appointmentTime(), null))
            throw new ValidationException("Dentist already has an active appointment at this date and time");
        Patient patient = new Patient(0, request.patientName().trim(), request.address().trim(), request.contactNumber().trim());
        return appointmentDao.save(new Appointment(0, "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), patient, dentist, treatment, request.appointmentDate(), request.appointmentTime(), AppointmentStatus.ACTIVE));
    }
    public Appointment update(String number, AppointmentRequest request) {
        Appointment old = get(number); validate(request);
        Dentist dentist = dentist(request.dentistId()); Treatment treatment = treatment(request.treatmentId());
        if (old.status() == AppointmentStatus.ACTIVE && appointmentDao.hasActiveSlot(dentist.id(), request.appointmentDate(), request.appointmentTime(), number))
            throw new ValidationException("Dentist already has an active appointment at this date and time");
        return appointmentDao.update(new Appointment(old.id(), number, new Patient(old.patient().id(), request.patientName().trim(), request.address().trim(), request.contactNumber().trim()), dentist, treatment, request.appointmentDate(), request.appointmentTime(), old.status()));
    }
    public Appointment get(String number) { return appointmentDao.findByNumber(number).orElseThrow(() -> new NotFoundException("Appointment not found")); }
    public List<Appointment> list(LocalDate date, Long dentistId) { return appointmentDao.findAll(date, dentistId); }
    public void cancel(String number) { get(number); appointmentDao.cancel(number); }
    private Dentist dentist(long id) { Dentist d = catalogDao.findDentist(id).orElseThrow(() -> new ValidationException("Dentist not found")); if (!d.active()) throw new ValidationException("Dentist is inactive"); return d; }
    private Treatment treatment(long id) { Treatment t = catalogDao.findTreatment(id).orElseThrow(() -> new ValidationException("Treatment not found")); if (!t.active()) throw new ValidationException("Treatment is inactive"); return t; }
    private void validate(AppointmentRequest r) {
        if (r == null || blank(r.patientName()) || blank(r.address()) || blank(r.contactNumber())) throw new ValidationException("Patient name, address and contact number are required");
        if (r.dentistId() <= 0 || r.treatmentId() <= 0 || r.appointmentDate() == null || r.appointmentTime() == null) throw new ValidationException("Dentist, treatment, date and time are required");
        if (r.appointmentDate().isBefore(LocalDate.now())) throw new ValidationException("Appointment date cannot be in the past");
    }
    private boolean blank(String value) { return value == null || value.isBlank(); }
}
