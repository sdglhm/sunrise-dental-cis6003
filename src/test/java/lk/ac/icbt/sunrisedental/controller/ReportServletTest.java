package lk.ac.icbt.sunrisedental.controller;

import lk.ac.icbt.sunrisedental.dto.DashboardSummary;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.service.ReportService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServletTest {
    @Test void returnsDashboardSummary() throws Exception {
        ReportService reports = mock(ReportService.class);
        when(reports.summary()).thenReturn(new DashboardSummary(4, 7, 1, 2, new BigDecimal("8250.00")));
        var response = ServletTestSupport.response();
        new ReportServlet(reports).doGet(ServletTestSupport.request("/summary"), response.response());
        assertEquals(200, response.status().get());
        assertTrue(response.body().toString().contains("8250.00"));
    }

    @Test void returnsBadRequestForInvalidDate() throws Exception {
        ReportService reports = mock(ReportService.class);
        when(reports.daily(any())).thenThrow(new ValidationException("Enter a valid report date"));
        var request = ServletTestSupport.request("/daily");
        when(request.getParameter("date")).thenReturn("invalid");
        var response = ServletTestSupport.response();
        new ReportServlet(reports).doGet(request, response.response());
        assertEquals(400, response.status().get());
    }
}
