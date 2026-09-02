package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.AppointmentDao;
import lk.ac.icbt.sunrisedental.dao.BillDao;
import lk.ac.icbt.sunrisedental.dao.CatalogDao;
import lk.ac.icbt.sunrisedental.dao.PatientDao;
import lk.ac.icbt.sunrisedental.dto.BillGeneration;
import lk.ac.icbt.sunrisedental.dto.BillingPreview;
import lk.ac.icbt.sunrisedental.exception.NotFoundException;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.Appointment;
import lk.ac.icbt.sunrisedental.model.AppointmentStatus;
import lk.ac.icbt.sunrisedental.model.Bill;
import lk.ac.icbt.sunrisedental.model.Dentist;
import lk.ac.icbt.sunrisedental.model.Patient;
import lk.ac.icbt.sunrisedental.model.Treatment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {
    @Test void addsTreatmentAndConsultationFeesUsingBigDecimal() {
        BillingPreview preview = service(appointment(AppointmentStatus.ACTIVE), new MemoryBills(), new BigDecimal("1000.00")).preview("APT-001");
        assertEquals(new BigDecimal("4500.50"), preview.totalAmount());
    }

    @Test void createsACompleteReceipt() {
        BillGeneration result = service(appointment(AppointmentStatus.ACTIVE), new MemoryBills(), new BigDecimal("1000.00")).generate("APT-001");
        assertTrue(result.created());
        assertTrue(result.receipt().billNumber().startsWith("BILL-"));
        assertEquals("Dr Lee", result.receipt().dentistName());
        assertEquals(new BigDecimal("4500.50"), result.receipt().totalAmount());
    }

    @Test void returnsExistingBillWithoutCreatingADuplicate() {
        MemoryBills bills = new MemoryBills();
        BillingService service = service(appointment(AppointmentStatus.ACTIVE), bills, new BigDecimal("1000.00"));
        assertTrue(service.generate("APT-001").created());
        assertFalse(service.generate("APT-001").created());
        assertEquals(1, bills.saveCount);
    }

    @Test void rejectsBillingForCancelledAppointment() {
        BillingService service = service(appointment(AppointmentStatus.CANCELLED), new MemoryBills(), new BigDecimal("1000.00"));
        assertThrows(ValidationException.class, () -> service.generate("APT-001"));
    }

    @Test void rejectsNegativeBillingAmounts() {
        BillingService service = service(appointment(AppointmentStatus.ACTIVE), new MemoryBills(), new BigDecimal("-1.00"));
        assertThrows(ValidationException.class, () -> service.preview("APT-001"));
    }

    @Test void reportsWhenNoBillHasBeenGenerated() {
        BillingService service = service(appointment(AppointmentStatus.ACTIVE), new MemoryBills(), new BigDecimal("1000.00"));
        assertThrows(NotFoundException.class, () -> service.get("APT-001"));
    }

    private BillingService service(Appointment appointment, BillDao bills, BigDecimal fee) {
        AppointmentDao appointments = new AppointmentDao() {
            public Optional<Appointment> findByNumber(String number) { return Optional.of(appointment); }
            public boolean hasActiveSlot(long dentistId, LocalDate date, LocalTime time, String excluding) { return false; }
            public Appointment save(Appointment value) { return value; }
            public Appointment update(Appointment value) { return value; }
            public void cancel(String number) { }
            public List<Appointment> findAll(LocalDate date, Long dentistId) { return List.of(); }
        };
        CatalogDao catalog = new CatalogDao() {
            public Optional<Dentist> findDentist(long id) { return Optional.empty(); }
            public Optional<Treatment> findTreatment(long id) { return Optional.empty(); }
            public List<Dentist> findActiveDentists() { return List.of(); }
            public List<Treatment> findActiveTreatments() { return List.of(); }
            public Dentist saveDentist(String name) { return null; }
            public Treatment saveTreatment(String name, BigDecimal price) { return null; }
        };
        PatientDao patients = new PatientDao() {
            public Optional<Patient> findById(long id) { return Optional.empty(); }
            public List<Patient> findAll(String search) { return List.of(); }
            public Patient save(Patient patient) { return patient; }
            public Patient update(Patient patient) { return patient; }
        };
        return new BillingService(new AppointmentService(appointments, catalog, patients), bills, () -> fee);
    }

    private Appointment appointment(AppointmentStatus status) {
        Treatment treatment = new Treatment(1, "Filling", new BigDecimal("3500.50"), true);
        return new Appointment(4, "APT-001", new Patient(1, "Asha", "Kandy", "0711111111"), new Dentist(1, "Dr Lee", true), treatment, LocalDate.now(), LocalTime.NOON, status);
    }

    private static class MemoryBills implements BillDao {
        private Bill stored;
        private int saveCount;

        public Optional<Bill> findByAppointmentId(long appointmentId) { return Optional.ofNullable(stored); }
        public Bill save(Bill bill) {
            saveCount++;
            stored = new Bill(1, bill.billNumber(), bill.appointmentId(), bill.treatmentPrice(), bill.consultationFee(), bill.totalAmount(), LocalDateTime.now());
            return stored;
        }
    }
}
