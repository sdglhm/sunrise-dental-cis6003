package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.BillDao;
import lk.ac.icbt.sunrisedental.dao.ClinicSettingsDao;
import lk.ac.icbt.sunrisedental.dto.BillGeneration;
import lk.ac.icbt.sunrisedental.dto.BillReceipt;
import lk.ac.icbt.sunrisedental.dto.BillingPreview;
import lk.ac.icbt.sunrisedental.exception.NotFoundException;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BillingService {
    private final AppointmentService appointments;
    private final BillDao bills;
    private final ClinicSettingsDao settings;

    public BillingService(AppointmentService appointments, BillDao bills, ClinicSettingsDao settings) {
        this.appointments = appointments;
        this.bills = bills;
        this.settings = settings;
    }

    public BillingPreview preview(String number) {
        Appointment appointment = appointments.get(number);
        BigDecimal treatmentPrice = appointment.treatment().price();
        BigDecimal consultationFee = settings.consultationFee();
        if (treatmentPrice == null || consultationFee == null || treatmentPrice.signum() < 0 || consultationFee.signum() < 0) {
            throw new ValidationException("Billing amounts must be valid non-negative values");
        }
        return new BillingPreview(number, appointment.patient().fullName(), appointment.treatment().name(), treatmentPrice, consultationFee, treatmentPrice.add(consultationFee));
    }

    public BillGeneration generate(String number) {
        Appointment appointment = appointments.get(number);
        return bills.findByAppointmentId(appointment.id())
                .map(bill -> new BillGeneration(receipt(appointment, bill), false))
                .orElseGet(() -> create(appointment));
    }

    public BillReceipt get(String number) {
        Appointment appointment = appointments.get(number);
        Bill bill = bills.findByAppointmentId(appointment.id())
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        return receipt(appointment, bill);
    }

    private BillGeneration create(Appointment appointment) {
        if (appointment.status() != AppointmentStatus.ACTIVE) throw new ValidationException("Cannot bill a cancelled appointment");
        BillingPreview preview = preview(appointment.appointmentNumber());
        String billNumber = "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Bill bill = bills.save(new Bill(0, billNumber, appointment.id(), preview.treatmentPrice(), preview.consultationFee(), preview.totalAmount(), LocalDateTime.now()));
        return new BillGeneration(receipt(appointment, bill), true);
    }

    private BillReceipt receipt(Appointment appointment, Bill bill) {
        return new BillReceipt(bill.billNumber(), appointment.appointmentNumber(), appointment.patient().fullName(), appointment.dentist().fullName(), appointment.treatment().name(), bill.treatmentPrice(), bill.consultationFee(), bill.totalAmount(), bill.generatedAt());
    }
}
