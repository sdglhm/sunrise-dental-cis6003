package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lk.ac.icbt.sunrisedental.dto.PatientRequest;
import lk.ac.icbt.sunrisedental.exception.NotFoundException;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.util.AppServices;
import lk.ac.icbt.sunrisedental.util.JsonResponse;

@WebServlet("/api/patients/*")
public class PatientServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String path = request.getPathInfo();
            if (path == null || "/".equals(path)) JsonResponse.write(response, 200, AppServices.patients().list(request.getParameter("search")));
            else JsonResponse.write(response, 200, AppServices.patients().get(id(path)));
        } catch (NotFoundException exception) { JsonResponse.error(response, 404, exception.getMessage());
        } catch (Exception exception) { JsonResponse.error(response, 400, "Invalid request"); }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            PatientRequest patient = JsonResponse.body(request, PatientRequest.class);
            JsonResponse.write(response, 201, AppServices.patients().add(patient.fullName(), patient.address(), patient.contactNumber()));
        } catch (ValidationException exception) { JsonResponse.error(response, 422, exception.getMessage());
        } catch (Exception exception) { JsonResponse.error(response, 400, "Invalid request"); }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            PatientRequest patient = JsonResponse.body(request, PatientRequest.class);
            JsonResponse.write(response, 200, AppServices.patients().update(id(request.getPathInfo()), patient.fullName(), patient.address(), patient.contactNumber()));
        } catch (NotFoundException exception) { JsonResponse.error(response, 404, exception.getMessage());
        } catch (ValidationException exception) { JsonResponse.error(response, 422, exception.getMessage());
        } catch (Exception exception) { JsonResponse.error(response, 400, "Invalid request"); }
    }

    private long id(String path) { return Long.parseLong(path.substring(1)); }
}
