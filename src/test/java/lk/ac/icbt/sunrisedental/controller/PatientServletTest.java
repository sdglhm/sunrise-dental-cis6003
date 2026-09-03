package lk.ac.icbt.sunrisedental.controller;

import lk.ac.icbt.sunrisedental.model.Patient;
import lk.ac.icbt.sunrisedental.service.PatientService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServletTest {
    private final PatientService patients = mock(PatientService.class);
    private final PatientServlet servlet = new PatientServlet(patients);

    @Test void returnsPatientList() throws Exception {
        when(patients.list(null)).thenReturn(List.of(patient()));
        var response = ServletTestSupport.response();
        servlet.doGet(ServletTestSupport.request(null), response.response());
        assertEquals(200, response.status().get());
        assertTrue(response.body().toString().contains("Asha"));
    }

    @Test void updatesPatient() throws Exception {
        when(patients.update(eq(1L), eq("Asha"), eq("Colombo"), eq("0711111111"))).thenReturn(patient());
        var response = ServletTestSupport.response();
        servlet.doPut(ServletTestSupport.request("/1", "{\"fullName\":\"Asha\",\"address\":\"Colombo\",\"contactNumber\":\"0711111111\"}"), response.response());
        assertEquals(200, response.status().get());
        verify(patients).update(1L, "Asha", "Colombo", "0711111111");
    }

    private Patient patient() {
        return new Patient(1, "Asha", "Colombo", "0711111111");
    }
}
