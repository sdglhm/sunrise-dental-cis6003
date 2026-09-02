package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportDao {
    List<DailyReport> daily(LocalDate date);
    List<DentistReport> dentists();
    List<TreatmentReport> treatments();
    List<RevenueReport> revenue();
    DashboardSummary summary(LocalDate today);
}
