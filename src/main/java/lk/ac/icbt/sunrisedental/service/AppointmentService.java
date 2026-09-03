package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.AppointmentDao;
import lk.ac.icbt.sunrisedental.dao.CatalogDao;
import lk.ac.icbt.sunrisedental.dao.PatientDao;
import lk.ac.icbt.sunrisedental.dto.AppointmentRequest;
import lk.ac.icbt.sunrisedental.exception.NotFoundException;
import lk.ac.icbt.sunrisedental.exception.ConflictException;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class AppointmentService {
    private static final Pattern CONTACT_NUMBER = Pattern.compile("^[0-9+ -]{7,20}$");
    private final AppointmentDao appointmentDao;
    private final CatalogDao catalogDao;
    private final PatientDao patientDao;

    public AppointmentService(AppointmentDao appointmentDao, CatalogDao catalogDao, PatientDao patientDao) {
        this.appointmentDao = appointmentDao; this.catalogDao = catalogDao; this.patientDao = patientDao;
    }
    public Appointment create(AppointmentRequest request) {
        validate(request);
        Dentist dentist = dentist(request.dentistId()); Treatment treatment = treatment(request.treatmentId());
        if (appointmentDao.hasActiveSlot(dentist.id(), request.appointmentDate(), request.appointmentTime(), null))
            throw new ConflictException("Dentist already has an active appointment at this date and time");
        Patient patient = patient(request);
        return appointmentDao.save(new Appointment(0, "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), patient, dentist, treatment, request.appointmentDate(), request.appointmentTime(), AppointmentStatus.ACTIVE));
    }
    public Appointment update(String number, AppointmentRequest request) {
        Appointment old = get(number); validate(request);
        Dentist dentist = dentist(request.dentistId()); Treatment treatment = treatment(request.treatmentId());
        if (old.status() == AppointmentStatus.ACTIVE && appointmentDao.hasActiveSlot(dentist.id(), request.appointmentDate(), request.appointmentTime(), number))
            throw new ConflictException("Dentist already has an active appointment at this date and time");
        Patient patient = request.patientId() == null
                ? new Patient(old.patient().id(), request.patientName().trim(), request.address().trim(), request.contactNumber().trim())
                : patient(request);
        return appointmentDao.update(new Appointment(old.id(), number, patient, dentist, treatment, request.appointmentDate(), request.appointmentTime(), old.status()));
    }
    public Appointment get(String number) { return appointmentDao.findByNumber(number).orElseThrow(() -> new NotFoundException("Appointment not found")); }
    public List<Appointment> list(LocalDate date, Long dentistId) { return appointmentDao.findAll(date, dentistId); }
    public void cancel(String number) { get(number); appointmentDao.cancel(number); }
    private Dentist dentist(long id) { Dentist d = catalogDao.findDentist(id).orElseThrow(() -> new ValidationException("Dentist not found")); if (!d.active()) throw new ValidationException("Dentist is inactive"); return d; }
    private Treatment treatment(long id) { Treatment t = catalogDao.findTreatment(id).orElseThrow(() -> new ValidationException("Treatment not found")); if (!t.active()) throw new ValidationException("Treatment is inactive"); return t; }
    private Patient patient(AppointmentRequest request) { return request.patientId() == null ? new Patient(0, request.patientName().trim(), request.address().trim(), request.contactNumber().trim()) : patientDao.findById(request.patientId()).orElseThrow(() -> new ValidationException("Patient not found")); }
    private void validate(AppointmentRequest r) {
        if (r == null || (r.patientId() == null && (blank(r.patientName()) || blank(r.address()) || blank(r.contactNumber())))) throw new ValidationException("Patient name, address and contact number are required");
        if (r.patientId() == null && !CONTACT_NUMBER.matcher(r.contactNumber().trim()).matches()) throw new ValidationException("Enter a valid contact number");
        if (r.dentistId() <= 0 || r.treatmentId() <= 0 || r.appointmentDate() == null || r.appointmentTime() == null) throw new ValidationException("Dentist, treatment, date and time are required");
        if (r.appointmentDate().isBefore(LocalDate.now())) throw new ValidationException("Appointment date cannot be in the past");
    }
    private boolean blank(String value) { return value == null || value.isBlank(); }
}
