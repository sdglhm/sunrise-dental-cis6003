package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.ReportDao;
import lk.ac.icbt.sunrisedental.dto.*;
import lk.ac.icbt.sunrisedental.exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReportService {
    private final ReportDao reports;

    public ReportService(ReportDao reports) { this.reports = reports; }

    public List<DailyReport> daily(String date) {
        LocalDate selected = LocalDate.now();
        if (date != null && !date.isBlank()) {
            try { selected = LocalDate.parse(date); }
            catch (DateTimeParseException exception) { throw new ValidationException("Enter a valid report date"); }
        }
        return reports.daily(selected);
    }

    public List<DentistReport> dentists() { return reports.dentists(); }
    public List<TreatmentReport> treatments() { return reports.treatments(); }
    public List<RevenueReport> revenue() { return reports.revenue(); }
    public DashboardSummary summary() { return reports.summary(LocalDate.now()); }
}
