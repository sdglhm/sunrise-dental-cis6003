package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.*;
import lk.ac.icbt.sunrisedental.dto.BillingPreview;
import lk.ac.icbt.sunrisedental.model.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {
    @Test void addsTreatmentAndConsultationFeesUsingBigDecimal() {
        Treatment treatment = new Treatment(1, "Filling", new BigDecimal("3500.50"), true);
        Appointment appointment = new Appointment(4, "APT-001", new Patient(1, "Asha", "Kandy", "071"), new Dentist(1, "Dr Lee", true), treatment, LocalDate.now(), LocalTime.NOON, AppointmentStatus.ACTIVE);
        AppointmentService appointments = new AppointmentService(new AppointmentDao() {
            public Optional<Appointment> findByNumber(String n) { return Optional.of(appointment); } public boolean hasActiveSlot(long d, LocalDate x, LocalTime y, String e) { return false; }
            public Appointment save(Appointment a) { return a; } public Appointment update(Appointment a) { return a; } public void cancel(String n) { } public List<Appointment> findAll(LocalDate d, Long x) { return List.of(); }
        }, new CatalogDao() { public Optional<Dentist> findDentist(long i) { return Optional.empty(); } public Optional<Treatment> findTreatment(long i) { return Optional.empty(); } public List<Dentist> findActiveDentists() { return List.of(); } public List<Treatment> findActiveTreatments() { return List.of(); } public Dentist saveDentist(String name) { return null; } public Treatment saveTreatment(String name, BigDecimal price) { return null; } }, new PatientDao() { public Optional<Patient> findById(long id) { return Optional.empty(); } public List<Patient> findAll(String search) { return List.of(); } public Patient save(Patient patient) { return patient; } public Patient update(Patient patient) { return patient; } });
        BillingService service = new BillingService(appointments, new BillDao() { public Optional<Bill> findByAppointmentId(long id) { return Optional.empty(); } public Bill save(Bill b) { return b; } }, new BigDecimal("1000.00"));
        BillingPreview preview = service.preview("APT-001");
        assertEquals(new BigDecimal("4500.50"), preview.totalAmount());
    }
}
