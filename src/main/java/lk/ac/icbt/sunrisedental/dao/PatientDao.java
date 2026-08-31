package lk.ac.icbt.sunrisedental.dao;

import java.util.List;
import java.util.Optional;
import lk.ac.icbt.sunrisedental.model.Patient;

public interface PatientDao {
    Optional<Patient> findById(long id);
    List<Patient> findAll(String search);
    Patient save(Patient patient);
    Patient update(Patient patient);
}
