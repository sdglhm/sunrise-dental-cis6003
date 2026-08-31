package lk.ac.icbt.sunrisedental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import java.util.Optional;
import lk.ac.icbt.sunrisedental.dao.PatientDao;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.Patient;
import org.junit.jupiter.api.Test;

class PatientServiceTest {
    @Test
    void addsAPatientWithTrimmedValues() {
        PatientService service = new PatientService(new StubPatients());
        Patient patient = service.add("  Asha Silva ", " Kandy ", " 0711111111 ");
        assertEquals("Asha Silva", patient.fullName());
        assertEquals("Kandy", patient.address());
    }

    @Test
    void rejectsAnIncompletePatient() {
        PatientService service = new PatientService(new StubPatients());
        assertThrows(ValidationException.class, () -> service.add("Asha", "", "0711111111"));
    }

    private static class StubPatients implements PatientDao {
        public Optional<Patient> findById(long id) { return Optional.of(new Patient(id, "Asha", "Kandy", "071")); }
        public List<Patient> findAll(String search) { return List.of(); }
        public Patient save(Patient patient) { return new Patient(1, patient.fullName(), patient.address(), patient.contactNumber()); }
        public Patient update(Patient patient) { return patient; }
    }
}
