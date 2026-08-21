package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.model.Bill;
import java.util.Optional;

public interface BillDao { Optional<Bill> findByAppointmentId(long appointmentId); Bill save(Bill bill); }
