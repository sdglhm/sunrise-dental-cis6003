package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ac.icbt.sunrisedental.dto.AppointmentRequest;
import lk.ac.icbt.sunrisedental.util.ApiErrorHandler;
import lk.ac.icbt.sunrisedental.util.AppServices;
import lk.ac.icbt.sunrisedental.util.JsonResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@WebServlet("/api/appointments/*")
public class AppointmentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, () -> {
            String path = request.getPathInfo();
            String[] parts = path == null ? new String[0] : path.split("/");
            if (path == null || "/".equals(path)) {
                JsonResponse.write(response, 200, AppServices.appointments().list(date(request.getParameter("date")), number(request.getParameter("dentistId"))));
            } else if (parts.length == 3 && "bill-preview".equals(parts[2])) {
                JsonResponse.write(response, 200, AppServices.billing().preview(parts[1]));
            } else if (parts.length == 3 && "bill".equals(parts[2])) {
                JsonResponse.write(response, 200, AppServices.billing().get(parts[1]));
            } else {
                JsonResponse.write(response, 200, AppServices.appointments().get(part(path)));
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, () -> {
            String[] parts = request.getPathInfo() == null ? new String[0] : request.getPathInfo().split("/");
            if (parts.length == 3 && "bill".equals(parts[2])) {
                var result = AppServices.billing().generate(parts[1]);
                JsonResponse.write(response, result.created() ? 201 : 200, result.receipt());
            } else {
                JsonResponse.write(response, 201, AppServices.appointments().create(JsonResponse.body(request, AppointmentRequest.class)));
            }
        });
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, () -> JsonResponse.write(response, 200, AppServices.appointments().update(part(request.getPathInfo()), JsonResponse.body(request, AppointmentRequest.class))));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, () -> {
            AppServices.appointments().cancel(part(request.getPathInfo()));
            JsonResponse.write(response, 200, Map.of("message", "Appointment cancelled"));
        });
    }

    private String part(String path) {
        if (path == null || path.length() < 2) throw new IllegalArgumentException("Appointment number is required");
        return path.substring(1).split("/")[0];
    }

    private LocalDate date(String value) { return value == null || value.isBlank() ? null : LocalDate.parse(value); }
    private Long number(String value) { return value == null || value.isBlank() ? null : Long.parseLong(value); }
}
