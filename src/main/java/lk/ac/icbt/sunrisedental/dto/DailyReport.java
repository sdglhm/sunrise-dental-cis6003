package lk.ac.icbt.sunrisedental.dto;

import java.time.LocalDate;

public record DailyReport(LocalDate date, long appointments) { }
