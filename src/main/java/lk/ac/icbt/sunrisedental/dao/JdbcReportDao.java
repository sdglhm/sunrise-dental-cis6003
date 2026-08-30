package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.util.DatabaseConnectionFactory;
import java.sql.*; import java.util.*;

public class JdbcReportDao {
    public List<Map<String,Object>> daily(String date) { return query("SELECT appointment_date AS date, COUNT(*) AS appointments FROM appointments WHERE appointment_date=COALESCE(CAST(? AS DATE),CURRENT_DATE) AND status='ACTIVE' GROUP BY appointment_date", date); }
    public List<Map<String,Object>> dentists() { return query("SELECT d.full_name AS dentist, COUNT(a.appointment_id) AS appointments FROM dentists d LEFT JOIN appointments a ON a.dentist_id=d.dentist_id AND a.status='ACTIVE' GROUP BY d.dentist_id,d.full_name ORDER BY appointments DESC", null); }
    public List<Map<String,Object>> treatments() { return query("SELECT t.treatment_name AS treatment, COUNT(a.appointment_id) AS appointments FROM treatments t LEFT JOIN appointments a ON a.treatment_id=t.treatment_id AND a.status='ACTIVE' GROUP BY t.treatment_id,t.treatment_name ORDER BY appointments DESC", null); }
    public List<Map<String,Object>> revenue() { return query("SELECT DATE(generated_at) AS date, COUNT(*) AS bills, COALESCE(SUM(total_amount),0) AS revenue FROM bills GROUP BY DATE(generated_at) ORDER BY date DESC", null); }
    private List<Map<String,Object>> query(String sql,String value) { try(Connection c=DatabaseConnectionFactory.getInstance().getConnection();PreparedStatement s=c.prepareStatement(sql)){if(sql.contains("?")){if(value==null)s.setNull(1,Types.VARCHAR);else s.setString(1,value);}ResultSet r=s.executeQuery();List<Map<String,Object>> rows=new ArrayList<>();ResultSetMetaData m=r.getMetaData();while(r.next()){Map<String,Object> row=new LinkedHashMap<>();for(int i=1;i<=m.getColumnCount();i++){Object column=r.getObject(i);row.put(m.getColumnLabel(i),column instanceof java.sql.Date date?date.toLocalDate().toString():column);}rows.add(row);}return rows;}catch(SQLException e){throw new IllegalStateException("Could not create report",e);} }
}
