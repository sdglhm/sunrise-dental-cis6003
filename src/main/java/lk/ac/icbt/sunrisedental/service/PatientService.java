package lk.ac.icbt.sunrisedental.service;

import java.util.List;
import lk.ac.icbt.sunrisedental.dao.PatientDao;
import lk.ac.icbt.sunrisedental.exception.NotFoundException;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.Patient;

public class PatientService {
    private final PatientDao patients;

    public PatientService(PatientDao patients) { this.patients = patients; }
    public List<Patient> list(String search) { return patients.findAll(search); }
    public Patient get(long id) { return patients.findById(id).orElseThrow(() -> new NotFoundException("Patient not found")); }
    public Patient add(String fullName, String address, String contactNumber) { return patients.save(validPatient(0, fullName, address, contactNumber)); }
    public Patient update(long id, String fullName, String address, String contactNumber) { get(id); return patients.update(validPatient(id, fullName, address, contactNumber)); }

    private Patient validPatient(long id, String fullName, String address, String contactNumber) {
        if (blank(fullName) || blank(address) || blank(contactNumber)) throw new ValidationException("Patient name, address and contact number are required");
        return new Patient(id, fullName.trim(), address.trim(), contactNumber.trim());
    }
    private boolean blank(String value) { return value == null || value.isBlank(); }
}
