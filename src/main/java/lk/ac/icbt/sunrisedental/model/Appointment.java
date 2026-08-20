package lk.ac.icbt.sunrisedental.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Appointment(long id, String appointmentNumber, Patient patient, Dentist dentist,
                          Treatment treatment, LocalDate date, LocalTime time,
                          AppointmentStatus status) { }
