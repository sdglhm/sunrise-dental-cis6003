package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lk.ac.icbt.sunrisedental.dto.DentistRequest;
import lk.ac.icbt.sunrisedental.dto.TreatmentRequest;
import lk.ac.icbt.sunrisedental.util.AppServices;
import lk.ac.icbt.sunrisedental.util.ApiErrorHandler;
import lk.ac.icbt.sunrisedental.util.JsonResponse;

@WebServlet("/api/catalog/*")
public class CatalogServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, () -> {
            String path = request.getPathInfo();
            if ("/dentists".equals(path)) JsonResponse.write(response, 200, AppServices.catalog().dentists());
            else if ("/treatments".equals(path)) JsonResponse.write(response, 200, AppServices.catalog().treatments());
            else JsonResponse.error(response, 404, "Catalogue item not found");
        });
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, () -> {
            String path = request.getPathInfo();
            if ("/dentists".equals(path)) JsonResponse.write(response, 201, AppServices.catalog().addDentist(JsonResponse.body(request, DentistRequest.class).fullName()));
            else if ("/treatments".equals(path)) {
                TreatmentRequest treatment = JsonResponse.body(request, TreatmentRequest.class);
                JsonResponse.write(response, 201, AppServices.catalog().addTreatment(treatment.name(), treatment.price()));
            } else JsonResponse.error(response, 404, "Catalogue item not found");
        });
    }
}
