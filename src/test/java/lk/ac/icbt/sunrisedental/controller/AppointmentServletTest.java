package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.http.HttpServletRequest;
import lk.ac.icbt.sunrisedental.dto.BillGeneration;
import lk.ac.icbt.sunrisedental.dto.BillReceipt;
import lk.ac.icbt.sunrisedental.exception.ConflictException;
import lk.ac.icbt.sunrisedental.model.*;
import lk.ac.icbt.sunrisedental.service.AppointmentService;
import lk.ac.icbt.sunrisedental.service.BillingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppointmentServletTest {
    private final AppointmentService appointments = mock(AppointmentService.class);
    private final BillingService billing = mock(BillingService.class);
    private final AppointmentServlet servlet = new AppointmentServlet(appointments, billing);

    @Test void returnsAppointmentDetails() throws Exception {
        when(appointments.get("APT-001")).thenReturn(appointment());
        var response = ServletTestSupport.response();
        servlet.doGet(ServletTestSupport.request("/APT-001"), response.response());
        assertEquals(200, response.status().get());
        assertTrue(response.body().toString().contains("APT-001"));
    }

    @Test void createsAppointment() throws Exception {
        when(appointments.create(any())).thenReturn(appointment());
        HttpServletRequest request = ServletTestSupport.request(null, requestJson());
        var response = ServletTestSupport.response();
        servlet.doPost(request, response.response());
        assertEquals(201, response.status().get());
        verify(appointments).create(any());
    }

    @Test void updatesAppointment() throws Exception {
        when(appointments.update(eq("APT-001"), any())).thenReturn(appointment());
        var response = ServletTestSupport.response();
        servlet.doPut(ServletTestSupport.request("/APT-001", requestJson()), response.response());
        assertEquals(200, response.status().get());
        verify(appointments).update(eq("APT-001"), any());
    }

    @Test void cancelsAppointment() throws Exception {
        var response = ServletTestSupport.response();
        servlet.doDelete(ServletTestSupport.request("/APT-001"), response.response());
        assertEquals(200, response.status().get());
        verify(appointments).cancel("APT-001");
    }

    @Test void returnsConflictForOccupiedSlot() throws Exception {
        when(appointments.create(any())).thenThrow(new ConflictException("Dentist already booked"));
        var response = ServletTestSupport.response();
        servlet.doPost(ServletTestSupport.request(null, requestJson()), response.response());
        assertEquals(409, response.status().get());
        assertTrue(response.body().toString().contains("Dentist already booked"));
    }

    @Test void generatesBillWithCreationStatus() throws Exception {
        BillReceipt receipt = new BillReceipt("BILL-12345678", "APT-001", "Asha", "Dr Lee", "Filling", new BigDecimal("3500.00"), new BigDecimal("1000.00"), new BigDecimal("4500.00"), LocalDateTime.now());
        when(billing.generate("APT-001")).thenReturn(new BillGeneration(receipt, true));
        var response = ServletTestSupport.response();
        servlet.doPost(ServletTestSupport.request("/APT-001/bill"), response.response());
        assertEquals(201, response.status().get());
        assertTrue(response.body().toString().contains("BILL-12345678"));
        assertTrue(response.body().toString().contains("\"generatedAt\":\""));
    }

    @Test void returnsControlledServerError() throws Exception {
        when(appointments.get("APT-001")).thenThrow(new IllegalStateException("database password leaked"));
        var response = ServletTestSupport.response();
        servlet.doGet(ServletTestSupport.request("/APT-001"), response.response());
        assertEquals(500, response.status().get());
        assertFalse(response.body().toString().contains("password"));
    }

    private Appointment appointment() {
        return new Appointment(1, "APT-001", new Patient(1, "Asha", "Kandy", "0711111111"), new Dentist(1, "Dr Lee", true), new Treatment(1, "Filling", new BigDecimal("3500.00"), true), LocalDate.now().plusDays(1), LocalTime.NOON, AppointmentStatus.ACTIVE);
    }

    private String requestJson() {
        return "{\"patientName\":\"Asha\",\"address\":\"Kandy\",\"contactNumber\":\"0711111111\",\"dentistId\":1,\"treatmentId\":1,\"appointmentDate\":\"" + LocalDate.now().plusDays(1) + "\",\"appointmentTime\":\"12:00\"}";
    }
}
