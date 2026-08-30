package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.model.*;
import lk.ac.icbt.sunrisedental.util.DatabaseConnectionFactory;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAppointmentDao implements AppointmentDao {
    private static final String BASE = "SELECT a.appointment_id, a.appointment_number, a.appointment_date, a.appointment_time, a.status, p.patient_id, p.full_name, p.address, p.contact_number, d.dentist_id, d.full_name, d.active, t.treatment_id, t.treatment_name, t.price, t.active FROM appointments a JOIN patients p ON a.patient_id=p.patient_id JOIN dentists d ON a.dentist_id=d.dentist_id JOIN treatments t ON a.treatment_id=t.treatment_id ";
    public Optional<Appointment> findByNumber(String number) {
        try (Connection c = connection(); PreparedStatement s = c.prepareStatement(BASE + "WHERE a.appointment_number=?")) { s.setString(1, number); ResultSet r=s.executeQuery(); return r.next()?Optional.of(map(r)):Optional.empty(); } catch(SQLException e){throw error(e);}
    }
    public boolean hasActiveSlot(long dentistId, LocalDate date, LocalTime time, String excluding) {
        String sql="SELECT 1 FROM appointments WHERE dentist_id=? AND appointment_date=? AND appointment_time=? AND status='ACTIVE'" + (excluding == null ? "" : " AND appointment_number<>?");
        try(Connection c=connection(); PreparedStatement s=c.prepareStatement(sql)){s.setLong(1,dentistId);s.setDate(2,Date.valueOf(date));s.setTime(3,Time.valueOf(time));if(excluding!=null)s.setString(4,excluding);return s.executeQuery().next();}catch(SQLException e){throw error(e);}
    }
    public Appointment save(Appointment a) {
        try(Connection c=connection()){c.setAutoCommit(false); long patientId=insertPatient(c,a.patient()); try(PreparedStatement s=c.prepareStatement("INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS)){s.setString(1,a.appointmentNumber());s.setLong(2,patientId);s.setLong(3,a.dentist().id());s.setLong(4,a.treatment().id());s.setDate(5,Date.valueOf(a.date()));s.setTime(6,Time.valueOf(a.time()));s.setString(7,a.status().name());s.executeUpdate();ResultSet keys=s.getGeneratedKeys();keys.next();c.commit();return new Appointment(keys.getLong(1),a.appointmentNumber(),new Patient(patientId,a.patient().fullName(),a.patient().address(),a.patient().contactNumber()),a.dentist(),a.treatment(),a.date(),a.time(),a.status());}catch(SQLException e){c.rollback();throw e;}}catch(SQLException e){throw error(e);}
    }
    public Appointment update(Appointment a) {
        try(Connection c=connection()){c.setAutoCommit(false);try(PreparedStatement p=c.prepareStatement("UPDATE patients SET full_name=?, address=?, contact_number=? WHERE patient_id=?");PreparedStatement s=c.prepareStatement("UPDATE appointments SET dentist_id=?, treatment_id=?, appointment_date=?, appointment_time=? WHERE appointment_number=?")){p.setString(1,a.patient().fullName());p.setString(2,a.patient().address());p.setString(3,a.patient().contactNumber());p.setLong(4,a.patient().id());p.executeUpdate();s.setLong(1,a.dentist().id());s.setLong(2,a.treatment().id());s.setDate(3,Date.valueOf(a.date()));s.setTime(4,Time.valueOf(a.time()));s.setString(5,a.appointmentNumber());s.executeUpdate();c.commit();return a;}catch(SQLException e){c.rollback();throw e;}}catch(SQLException e){throw error(e);}
    }
    public void cancel(String number) { try(Connection c=connection();PreparedStatement s=c.prepareStatement("UPDATE appointments SET status='CANCELLED' WHERE appointment_number=?")){s.setString(1,number);s.executeUpdate();}catch(SQLException e){throw error(e);} }
    public List<Appointment> findAll(LocalDate date, Long dentistId) {
        String sql=BASE+"WHERE 1=1"+(date==null?"":" AND a.appointment_date=?")+(dentistId==null?"":" AND a.dentist_id=?")+" ORDER BY a.appointment_date, a.appointment_time";
        try(Connection c=connection();PreparedStatement s=c.prepareStatement(sql)){int parameter=1;if(date!=null)s.setDate(parameter++,Date.valueOf(date));if(dentistId!=null)s.setLong(parameter,dentistId);ResultSet r=s.executeQuery();List<Appointment> list=new ArrayList<>();while(r.next())list.add(map(r));return list;}catch(SQLException e){throw error(e);}
    }
    private long insertPatient(Connection c,Patient p)throws SQLException{try(PreparedStatement s=c.prepareStatement("INSERT INTO patients (full_name,address,contact_number) VALUES (?,?,?)",Statement.RETURN_GENERATED_KEYS)){s.setString(1,p.fullName());s.setString(2,p.address());s.setString(3,p.contactNumber());s.executeUpdate();ResultSet k=s.getGeneratedKeys();k.next();return k.getLong(1);}}
    private Appointment map(ResultSet r)throws SQLException{return new Appointment(r.getLong(1),r.getString(2),new Patient(r.getLong(6),r.getString(7),r.getString(8),r.getString(9)),new Dentist(r.getLong(10),r.getString(11),r.getBoolean(12)),new Treatment(r.getLong(13),r.getString(14),r.getBigDecimal(15),r.getBoolean(16)),r.getDate(3).toLocalDate(),r.getTime(4).toLocalTime(),AppointmentStatus.valueOf(r.getString(5)));}
    private Connection connection()throws SQLException{return DatabaseConnectionFactory.getInstance().getConnection();} private IllegalStateException error(SQLException e){return new IllegalStateException("Database operation failed",e);}
}
