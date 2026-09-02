package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ac.icbt.sunrisedental.util.ApiErrorHandler;
import lk.ac.icbt.sunrisedental.util.AppServices;
import lk.ac.icbt.sunrisedental.util.JsonResponse;

import java.io.IOException;

@WebServlet("/api/reports/*")
public class ReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, 400, () -> {
            String path = request.getPathInfo();
            if ("/daily".equals(path)) JsonResponse.write(response, 200, AppServices.reports().daily(request.getParameter("date")));
            else if ("/dentists".equals(path)) JsonResponse.write(response, 200, AppServices.reports().dentists());
            else if ("/treatments".equals(path)) JsonResponse.write(response, 200, AppServices.reports().treatments());
            else if ("/revenue".equals(path)) JsonResponse.write(response, 200, AppServices.reports().revenue());
            else if ("/summary".equals(path)) JsonResponse.write(response, 200, AppServices.reports().summary());
            else JsonResponse.error(response, 404, "Report not found");
        });
    }
}
