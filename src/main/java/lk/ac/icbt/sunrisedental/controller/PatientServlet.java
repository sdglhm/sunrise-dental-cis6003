package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lk.ac.icbt.sunrisedental.dto.PatientRequest;
import lk.ac.icbt.sunrisedental.service.PatientService;
import lk.ac.icbt.sunrisedental.util.ApiErrorHandler;
import lk.ac.icbt.sunrisedental.util.AppServices;
import lk.ac.icbt.sunrisedental.util.JsonResponse;

@WebServlet("/api/patients/*")
public class PatientServlet extends HttpServlet {
    private final PatientService patients;

    public PatientServlet() { this(AppServices.patients()); }
    PatientServlet(PatientService patients) { this.patients = patients; }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, () -> {
            String path = request.getPathInfo();
            if (path == null || "/".equals(path)) JsonResponse.write(response, 200, patients.list(request.getParameter("search")));
            else JsonResponse.write(response, 200, patients.get(id(path)));
        });
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, () -> {
            PatientRequest patient = JsonResponse.body(request, PatientRequest.class);
            JsonResponse.write(response, 201, patients.add(patient.fullName(), patient.address(), patient.contactNumber()));
        });
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, () -> {
            PatientRequest patient = JsonResponse.body(request, PatientRequest.class);
            JsonResponse.write(response, 200, patients.update(id(request.getPathInfo()), patient.fullName(), patient.address(), patient.contactNumber()));
        });
    }

    private long id(String path) {
        if (path == null || path.length() < 2) throw new IllegalArgumentException("Patient ID is required");
        return Long.parseLong(path.substring(1).split("/")[0]);
    }
}
