package lk.ac.icbt.sunrisedental.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lk.ac.icbt.sunrisedental.model.Patient;
import lk.ac.icbt.sunrisedental.util.DatabaseConnectionFactory;

public class JdbcPatientDao implements PatientDao {
    public Optional<Patient> findById(long id) {
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement("SELECT patient_id, full_name, address, contact_number FROM patients WHERE patient_id=?")) {
            statement.setLong(1, id); ResultSet result = statement.executeQuery();
            return result.next() ? Optional.of(map(result)) : Optional.empty();
        } catch (SQLException exception) { throw error(exception); }
    }

    public List<Patient> findAll(String search) {
        String value = search == null || search.isBlank() ? null : "%" + search.trim().toLowerCase() + "%";
        String sql = "SELECT patient_id, full_name, address, contact_number FROM patients" + (value == null ? "" : " WHERE LOWER(full_name) LIKE ? OR contact_number LIKE ?") + " ORDER BY full_name";
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (value != null) { statement.setString(1, value); statement.setString(2, value); }
            ResultSet result = statement.executeQuery(); List<Patient> patients = new ArrayList<>();
            while (result.next()) patients.add(map(result));
            return patients;
        } catch (SQLException exception) { throw error(exception); }
    }

    public Patient save(Patient patient) {
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO patients (full_name, address, contact_number) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, patient.fullName()); statement.setString(2, patient.address()); statement.setString(3, patient.contactNumber()); statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys(); keys.next();
            return new Patient(keys.getLong(1), patient.fullName(), patient.address(), patient.contactNumber());
        } catch (SQLException exception) { throw error(exception); }
    }

    public Patient update(Patient patient) {
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement("UPDATE patients SET full_name=?, address=?, contact_number=? WHERE patient_id=?")) {
            statement.setString(1, patient.fullName()); statement.setString(2, patient.address()); statement.setString(3, patient.contactNumber()); statement.setLong(4, patient.id());
            statement.executeUpdate(); return patient;
        } catch (SQLException exception) { throw error(exception); }
    }

    private Patient map(ResultSet result) throws SQLException { return new Patient(result.getLong(1), result.getString(2), result.getString(3), result.getString(4)); }
    private Connection connection() throws SQLException { return DatabaseConnectionFactory.getInstance().getConnection(); }
    private IllegalStateException error(SQLException exception) { return new IllegalStateException("Could not manage patients", exception); }
}
