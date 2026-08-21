package lk.ac.icbt.sunrisedental.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequest(String patientName, String address, String contactNumber,
                                 long dentistId, long treatmentId, LocalDate appointmentDate,
                                 LocalTime appointmentTime) { }
