package lk.ac.icbt.sunrisedental.dto;

import java.math.BigDecimal;

public record BillingPreview(String appointmentNumber, String patientName, String treatmentName,
                             BigDecimal treatmentPrice, BigDecimal consultationFee,
                             BigDecimal totalAmount) { }
