package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.model.*;
import lk.ac.icbt.sunrisedental.util.DatabaseConnectionFactory;
import java.sql.*;
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
}
