package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.dto.*;
import lk.ac.icbt.sunrisedental.util.DatabaseConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcReportDao implements ReportDao {
    @Override
    public List<DailyReport> daily(LocalDate date) {
        String sql = "SELECT appointment_date, COUNT(*) FROM appointments WHERE appointment_date=? AND status='ACTIVE' GROUP BY appointment_date";
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet result = statement.executeQuery()) {
                List<DailyReport> rows = new ArrayList<>();
                while (result.next()) rows.add(new DailyReport(result.getDate(1).toLocalDate(), result.getLong(2)));
                return rows;
            }
        } catch (SQLException exception) { throw error(exception); }
    }

    @Override
    public List<DentistReport> dentists() {
        String sql = "SELECT d.dentist_id,d.full_name,COUNT(a.appointment_id) FROM dentists d LEFT JOIN appointments a ON a.dentist_id=d.dentist_id AND a.status='ACTIVE' GROUP BY d.dentist_id,d.full_name ORDER BY COUNT(a.appointment_id) DESC,d.full_name";
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            List<DentistReport> rows = new ArrayList<>();
            while (result.next()) rows.add(new DentistReport(result.getLong(1), result.getString(2), result.getLong(3)));
            return rows;
        } catch (SQLException exception) { throw error(exception); }
    }

    @Override
    public List<TreatmentReport> treatments() {
        String sql = "SELECT t.treatment_id,t.treatment_name,COUNT(a.appointment_id) FROM treatments t LEFT JOIN appointments a ON a.treatment_id=t.treatment_id AND a.status='ACTIVE' GROUP BY t.treatment_id,t.treatment_name ORDER BY COUNT(a.appointment_id) DESC,t.treatment_name";
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            List<TreatmentReport> rows = new ArrayList<>();
            while (result.next()) rows.add(new TreatmentReport(result.getLong(1), result.getString(2), result.getLong(3)));
            return rows;
        } catch (SQLException exception) { throw error(exception); }
    }

    @Override
    public List<RevenueReport> revenue() {
        String sql = "SELECT DATE(generated_at),COUNT(*),COALESCE(SUM(total_amount),0) FROM bills GROUP BY DATE(generated_at) ORDER BY DATE(generated_at) DESC";
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            List<RevenueReport> rows = new ArrayList<>();
            while (result.next()) rows.add(new RevenueReport(result.getDate(1).toLocalDate(), result.getLong(2), result.getBigDecimal(3)));
            return rows;
        } catch (SQLException exception) { throw error(exception); }
    }

    @Override
    public DashboardSummary summary(LocalDate today) {
        String sql = "SELECT " +
                "SUM(CASE WHEN appointment_date=? THEN 1 ELSE 0 END)," +
                "SUM(CASE WHEN status='ACTIVE' THEN 1 ELSE 0 END)," +
                "SUM(CASE WHEN status='CANCELLED' THEN 1 ELSE 0 END)," +
                "(SELECT COUNT(*) FROM bills WHERE DATE(generated_at)=?)," +
                "(SELECT COALESCE(SUM(total_amount),0) FROM bills WHERE DATE(generated_at)=?) FROM appointments";
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(today));
            statement.setDate(2, Date.valueOf(today));
            statement.setDate(3, Date.valueOf(today));
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                BigDecimal revenue = result.getBigDecimal(5);
                return new DashboardSummary(result.getLong(1), result.getLong(2), result.getLong(3), result.getLong(4), revenue == null ? BigDecimal.ZERO : revenue);
            }
        } catch (SQLException exception) { throw error(exception); }
    }

    private Connection connection() throws SQLException { return DatabaseConnectionFactory.getInstance().getConnection(); }
    private IllegalStateException error(SQLException exception) { return new IllegalStateException("Could not create report", exception); }
}
