package lk.ac.icbt.sunrisedental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueReport(LocalDate date, long bills, BigDecimal revenue) { }
