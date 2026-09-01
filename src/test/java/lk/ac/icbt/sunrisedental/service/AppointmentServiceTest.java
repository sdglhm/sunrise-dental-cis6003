package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.*;
import lk.ac.icbt.sunrisedental.dto.AppointmentRequest;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentServiceTest {
    private final Dentist dentist = new Dentist(1, "Dr Silva", true);
    private final Treatment treatment = new Treatment(1, "Cleaning", new BigDecimal("2500.00"), true);
    @Test void rejectsDuplicateActiveDentistSlot() {
        AppointmentDao appointments = new StubAppointments(true);
        AppointmentService service = new AppointmentService(appointments, catalog(), patients());
        AppointmentRequest request = request();
        assertThrows(lk.ac.icbt.sunrisedental.exception.ConflictException.class, () -> service.create(request));
    }
    @Test void createsAppointmentWithGeneratedNumber() {
        AppointmentService service = new AppointmentService(new StubAppointments(false), catalog(), patients());
        Appointment result = service.create(request());
        assertTrue(result.appointmentNumber().startsWith("APT-"));
        assertEquals("Nimal Perera", result.patient().fullName());
    }
    @Test void rejectsPastAppointmentDate() {
        AppointmentService service = new AppointmentService(new StubAppointments(false), catalog(), patients());
        AppointmentRequest request = new AppointmentRequest("Nimal", "Colombo", "0711111111", 1, 1, LocalDate.now().minusDays(1), LocalTime.NOON, null);
        assertThrows(ValidationException.class, () -> service.create(request));
    }
    @Test void rejectsInvalidContactNumber() {
        AppointmentService service = new AppointmentService(new StubAppointments(false), catalog(), patients());
        AppointmentRequest request = new AppointmentRequest("Nimal", "Colombo", "invalid", 1, 1, LocalDate.now().plusDays(1), LocalTime.NOON, null);
        assertThrows(ValidationException.class, () -> service.create(request));
    }
    @Test void cancelsAnExistingAppointment() {
        Appointment appointment = new Appointment(1, "APT-001", new Patient(1, "Nimal", "Colombo", "0711111111"), dentist, treatment, LocalDate.now().plusDays(1), LocalTime.NOON, AppointmentStatus.ACTIVE);
        class CancellableAppointments extends StubAppointments {
            boolean cancelled;
            CancellableAppointments() { super(false); }
            public Optional<Appointment> findByNumber(String number) { return Optional.of(appointment); }
            public void cancel(String number) { cancelled = true; }
        }
        CancellableAppointments appointments = new CancellableAppointments();
        new AppointmentService(appointments, catalog(), patients()).cancel("APT-001");
        assertTrue(appointments.cancelled);
    }
    @Test void usesAnExistingPatientWhenSelected() {
        AppointmentService service = new AppointmentService(new StubAppointments(false), catalog(), patients());
        Appointment appointment = service.create(new AppointmentRequest(null, null, null, 1, 1, LocalDate.now().plusDays(1), LocalTime.of(11, 0), 8L));
        assertEquals(8L, appointment.patient().id());
    }
    private AppointmentRequest request() { return new AppointmentRequest("Nimal Perera", "Colombo", "0711111111", 1, 1, LocalDate.now().plusDays(1), LocalTime.of(10, 0), null); }
    private CatalogDao catalog() { return new CatalogDao() { public Optional<Dentist> findDentist(long id) { return Optional.of(dentist); } public Optional<Treatment> findTreatment(long id) { return Optional.of(treatment); } public List<Dentist> findActiveDentists() { return List.of(dentist); } public List<Treatment> findActiveTreatments() { return List.of(treatment); } public Dentist saveDentist(String name) { return dentist; } public Treatment saveTreatment(String name, BigDecimal price) { return treatment; } }; }
    private PatientDao patients() { return new PatientDao() { public Optional<Patient> findById(long id) { return Optional.of(new Patient(id, "Asha", "Kandy", "071")); } public List<Patient> findAll(String search) { return List.of(); } public Patient save(Patient patient) { return patient; } public Patient update(Patient patient) { return patient; } }; }
    private static class StubAppointments implements AppointmentDao {
        private final boolean occupied; StubAppointments(boolean occupied) { this.occupied = occupied; }
        public Optional<Appointment> findByNumber(String n) { return Optional.empty(); }
        public boolean hasActiveSlot(long d, LocalDate date, LocalTime time, String excluding) { return occupied; }
        public Appointment save(Appointment a) { return a; } public Appointment update(Appointment a) { return a; }
        public void cancel(String n) { } public List<Appointment> findAll(LocalDate d, Long dentist) { return List.of(); }
    }
}
