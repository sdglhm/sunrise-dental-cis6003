package lk.ac.icbt.sunrisedental.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillReceipt(String billNumber, String appointmentNumber, String patientName,
                          String dentistName, String treatmentName, BigDecimal treatmentPrice,
                          BigDecimal consultationFee, BigDecimal totalAmount,
                          LocalDateTime generatedAt) { }
