package lk.ac.icbt.sunrisedental.dto;

import java.math.BigDecimal;

public record DashboardSummary(long todayAppointments, long activeAppointments,
                               long cancelledAppointments, long billsToday,
                               BigDecimal revenueToday) { }
