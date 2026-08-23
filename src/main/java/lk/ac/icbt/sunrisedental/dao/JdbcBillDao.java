package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.model.Bill;
import lk.ac.icbt.sunrisedental.util.DatabaseConnectionFactory;
import java.sql.*; import java.util.Optional;

public class JdbcBillDao implements BillDao {
    public Optional<Bill> findByAppointmentId(long id){try(Connection c=DatabaseConnectionFactory.getInstance().getConnection();PreparedStatement s=c.prepareStatement("SELECT bill_id,appointment_id,treatment_price,consultation_fee,total_amount FROM bills WHERE appointment_id=?")){s.setLong(1,id);ResultSet r=s.executeQuery();return r.next()?Optional.of(map(r)):Optional.empty();}catch(SQLException e){throw new IllegalStateException("Could not read bill",e);}}
    public Bill save(Bill b){try(Connection c=DatabaseConnectionFactory.getInstance().getConnection();PreparedStatement s=c.prepareStatement("INSERT INTO bills (appointment_id,treatment_price,consultation_fee,total_amount) VALUES (?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){s.setLong(1,b.appointmentId());s.setBigDecimal(2,b.treatmentPrice());s.setBigDecimal(3,b.consultationFee());s.setBigDecimal(4,b.totalAmount());s.executeUpdate();ResultSet k=s.getGeneratedKeys();k.next();return new Bill(k.getLong(1),b.appointmentId(),b.treatmentPrice(),b.consultationFee(),b.totalAmount());}catch(SQLException e){throw new IllegalStateException("Could not save bill",e);}}
    private Bill map(ResultSet r)throws SQLException{return new Bill(r.getLong(1),r.getLong(2),r.getBigDecimal(3),r.getBigDecimal(4),r.getBigDecimal(5));}
}
