package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.model.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {
    Optional<Appointment> findByNumber(String appointmentNumber);
    boolean hasActiveSlot(long dentistId, LocalDate date, LocalTime time, String excludingNumber);
    Appointment save(Appointment appointment);
    Appointment update(Appointment appointment);
    void cancel(String appointmentNumber);
    List<Appointment> findAll(LocalDate date, Long dentistId);
}
