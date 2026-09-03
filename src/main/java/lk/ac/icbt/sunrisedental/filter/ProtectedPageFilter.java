package lk.ac.icbt.sunrisedental.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {"/appointments.html", "/patients.html", "/catalog.html", "/register.html", "/reports.html", "/help.html", "/receipt.html"})
public class ProtectedPageFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getSession(false) == null || httpRequest.getSession(false).getAttribute("user") == null) {
            ((HttpServletResponse) response).sendRedirect(httpRequest.getContextPath() + "/index.html");
            return;
        }
        chain.doFilter(request, response);
    }
}
