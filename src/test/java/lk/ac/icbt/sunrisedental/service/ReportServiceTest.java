package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.ReportDao;
import lk.ac.icbt.sunrisedental.dto.*;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {
    @Test void usesSelectedDateForDailyReport() {
        MemoryReports reports = new MemoryReports();
        ReportService service = new ReportService(reports);
        assertEquals(1, service.daily("2026-09-04").size());
        assertEquals(LocalDate.of(2026, 9, 4), reports.selectedDate);
    }

    @Test void rejectsInvalidReportDate() {
        ReportService service = new ReportService(new MemoryReports());
        assertThrows(ValidationException.class, () -> service.daily("04/09/2026"));
    }

    @Test void returnsEmptyReportResults() {
        MemoryReports reports = new MemoryReports();
        reports.empty = true;
        ReportService service = new ReportService(reports);
        assertTrue(service.dentists().isEmpty());
        assertTrue(service.treatments().isEmpty());
        assertTrue(service.revenue().isEmpty());
    }

    @Test void returnsDashboardSummary() {
        DashboardSummary summary = new ReportService(new MemoryReports()).summary();
        assertEquals(4, summary.todayAppointments());
        assertEquals(new BigDecimal("8250.00"), summary.revenueToday());
    }

    private static class MemoryReports implements ReportDao {
        LocalDate selectedDate;
        boolean empty;

        public List<DailyReport> daily(LocalDate date) {
            selectedDate = date;
            return empty ? List.of() : List.of(new DailyReport(date, 4));
        }
        public List<DentistReport> dentists() { return empty ? List.of() : List.of(new DentistReport(1, "Dr Perera", 3)); }
        public List<TreatmentReport> treatments() { return empty ? List.of() : List.of(new TreatmentReport(1, "Cleaning", 2)); }
        public List<RevenueReport> revenue() { return empty ? List.of() : List.of(new RevenueReport(LocalDate.now(), 2, new BigDecimal("8250.00"))); }
        public DashboardSummary summary(LocalDate today) { return new DashboardSummary(4, 7, 1, 2, new BigDecimal("8250.00")); }
    }
}
