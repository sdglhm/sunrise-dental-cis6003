package lk.ac.icbt.sunrisedental.model;

import java.math.BigDecimal;

public record Bill(long id, long appointmentId, BigDecimal treatmentPrice,
                   BigDecimal consultationFee, BigDecimal totalAmount) { }
