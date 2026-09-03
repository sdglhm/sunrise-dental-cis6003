package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ac.icbt.sunrisedental.util.ApiErrorHandler;
import lk.ac.icbt.sunrisedental.util.AppServices;
import lk.ac.icbt.sunrisedental.util.JsonResponse;
import lk.ac.icbt.sunrisedental.service.ReportService;

import java.io.IOException;

@WebServlet("/api/reports/*")
public class ReportServlet extends HttpServlet {
    private final ReportService reports;

    public ReportServlet() { this(AppServices.reports()); }
    ReportServlet(ReportService reports) { this.reports = reports; }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorHandler.handle(response, 400, () -> {
            String path = request.getPathInfo();
            if ("/daily".equals(path)) JsonResponse.write(response, 200, reports.daily(request.getParameter("date")));
            else if ("/dentists".equals(path)) JsonResponse.write(response, 200, reports.dentists());
            else if ("/treatments".equals(path)) JsonResponse.write(response, 200, reports.treatments());
            else if ("/revenue".equals(path)) JsonResponse.write(response, 200, reports.revenue());
            else if ("/summary".equals(path)) JsonResponse.write(response, 200, reports.summary());
            else JsonResponse.error(response, 404, "Report not found");
        });
    }
}
