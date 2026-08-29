package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.model.*;
import lk.ac.icbt.sunrisedental.util.DatabaseConnectionFactory;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCatalogDao implements CatalogDao {
    public Optional<Dentist> findDentist(long id) { return findDentist("SELECT dentist_id, full_name, active FROM dentists WHERE dentist_id = ?", id); }
    public Optional<Treatment> findTreatment(long id) {
        try (Connection c = DatabaseConnectionFactory.getInstance().getConnection(); PreparedStatement s = c.prepareStatement("SELECT treatment_id, treatment_name, price, active FROM treatments WHERE treatment_id = ?")) {
            s.setLong(1, id); ResultSet r = s.executeQuery(); return r.next() ? Optional.of(new Treatment(r.getLong(1), r.getString(2), r.getBigDecimal(3), r.getBoolean(4))) : Optional.empty();
        } catch (SQLException e) { throw new IllegalStateException("Could not read treatment", e); }
    }
    private Optional<Dentist> findDentist(String sql, long id) {
        try (Connection c = DatabaseConnectionFactory.getInstance().getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, id); ResultSet r = s.executeQuery(); return r.next() ? Optional.of(new Dentist(r.getLong(1), r.getString(2), r.getBoolean(3))) : Optional.empty();
        } catch (SQLException e) { throw new IllegalStateException("Could not read dentist", e); }
    }
    public List<Dentist> findActiveDentists() {
        List<Dentist> dentists = new ArrayList<>();
        try (Connection c = DatabaseConnectionFactory.getInstance().getConnection(); PreparedStatement s = c.prepareStatement("SELECT dentist_id, full_name, active FROM dentists WHERE active=TRUE ORDER BY full_name"); ResultSet r = s.executeQuery()) {
            while (r.next()) dentists.add(new Dentist(r.getLong(1), r.getString(2), r.getBoolean(3)));
            return dentists;
        } catch (SQLException e) { throw new IllegalStateException("Could not list dentists", e); }
    }
    public List<Treatment> findActiveTreatments() {
        List<Treatment> treatments = new ArrayList<>();
        try (Connection c = DatabaseConnectionFactory.getInstance().getConnection(); PreparedStatement s = c.prepareStatement("SELECT treatment_id, treatment_name, price, active FROM treatments WHERE active=TRUE ORDER BY treatment_name"); ResultSet r = s.executeQuery()) {
            while (r.next()) treatments.add(new Treatment(r.getLong(1), r.getString(2), r.getBigDecimal(3), r.getBoolean(4)));
            return treatments;
        } catch (SQLException e) { throw new IllegalStateException("Could not list treatments", e); }
    }
    public Dentist saveDentist(String fullName) {
        try (Connection c = DatabaseConnectionFactory.getInstance().getConnection(); PreparedStatement s = c.prepareStatement("INSERT INTO dentists (full_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, fullName); s.executeUpdate(); ResultSet keys = s.getGeneratedKeys(); keys.next(); return new Dentist(keys.getLong(1), fullName, true);
        } catch (SQLException e) { throw new IllegalStateException("Could not add dentist", e); }
    }
    public Treatment saveTreatment(String name, BigDecimal price) {
        try (Connection c = DatabaseConnectionFactory.getInstance().getConnection(); PreparedStatement s = c.prepareStatement("INSERT INTO treatments (treatment_name, price) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, name); s.setBigDecimal(2, price); s.executeUpdate(); ResultSet keys = s.getGeneratedKeys(); keys.next(); return new Treatment(keys.getLong(1), name, price, true);
        } catch (SQLException e) { throw new IllegalStateException("Could not add treatment", e); }
    }
}
