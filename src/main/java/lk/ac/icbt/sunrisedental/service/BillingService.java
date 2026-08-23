package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.BillDao;
import lk.ac.icbt.sunrisedental.dto.BillingPreview;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.*;
import java.math.BigDecimal;

public class BillingService {
    private final AppointmentService appointments; private final BillDao bills; private final BigDecimal consultationFee;
    public BillingService(AppointmentService appointments, BillDao bills, BigDecimal consultationFee) { this.appointments = appointments; this.bills = bills; this.consultationFee = consultationFee; }
    public BillingPreview preview(String number) { Appointment a = appointments.get(number); BigDecimal total = a.treatment().price().add(consultationFee); return new BillingPreview(number, a.patient().fullName(), a.treatment().name(), a.treatment().price(), consultationFee, total); }
    public Bill generate(String number) { Appointment a = appointments.get(number); if (a.status() != AppointmentStatus.ACTIVE) throw new ValidationException("Cannot bill a cancelled appointment"); return bills.findByAppointmentId(a.id()).orElseGet(() -> { BillingPreview p = preview(number); return bills.save(new Bill(0, a.id(), p.treatmentPrice(), p.consultationFee(), p.totalAmount())); }); }
}
