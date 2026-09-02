package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.util.DatabaseConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcClinicSettingsDao implements ClinicSettingsDao {
    @Override
    public BigDecimal consultationFee() {
        String sql = "SELECT decimal_value FROM clinic_settings WHERE setting_key = 'consultation_fee'";
        try (Connection connection = DatabaseConnectionFactory.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            if (!result.next()) throw new IllegalStateException("Consultation fee is not configured");
            return result.getBigDecimal(1);
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not read clinic settings", exception);
        }
    }
}
