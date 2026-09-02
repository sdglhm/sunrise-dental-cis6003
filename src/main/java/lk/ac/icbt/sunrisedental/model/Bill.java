package lk.ac.icbt.sunrisedental.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Bill(long id, String billNumber, long appointmentId, BigDecimal treatmentPrice,
                   BigDecimal consultationFee, BigDecimal totalAmount,
                   LocalDateTime generatedAt) { }
