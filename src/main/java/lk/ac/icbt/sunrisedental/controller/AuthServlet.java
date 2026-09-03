package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lk.ac.icbt.sunrisedental.dto.LoginRequest;
import lk.ac.icbt.sunrisedental.exception.InvalidCredentialsException;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.User;
import lk.ac.icbt.sunrisedental.util.AppServices;
import lk.ac.icbt.sunrisedental.util.JsonResponse;
import lk.ac.icbt.sunrisedental.service.AuthenticationService;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private final AuthenticationService authentication;

    public AuthServlet() { this(AppServices.authentication()); }
    AuthServlet(AuthenticationService authentication) { this.authentication = authentication; }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if ("/login".equals(request.getPathInfo())) login(request, response);
            else if ("/logout".equals(request.getPathInfo())) logout(request, response);
            else JsonResponse.error(response, 404, "Endpoint not found");
        } catch (ValidationException exception) {
            JsonResponse.error(response, 400, exception.getMessage());
        } catch (InvalidCredentialsException exception) {
            JsonResponse.error(response, 401, exception.getMessage());
        } catch (IOException exception) {
            JsonResponse.error(response, 400, "Invalid request");
        } catch (Exception exception) {
            JsonResponse.error(response, 500, "The request could not be completed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) JsonResponse.error(response, 401, "Not authenticated");
        else JsonResponse.write(response, 200, Map.of("authenticated", true, "username", user.username(), "fullName", user.fullName()));
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginRequest login = JsonResponse.body(request, LoginRequest.class);
        User user = authentication.authenticate(login.username(), login.password());
        HttpSession session = request.getSession(false);
        if (session == null) session = request.getSession(true);
        else request.changeSessionId();
        session.setAttribute("user", user);
        JsonResponse.write(response, 200, Map.of("username", user.username(), "fullName", user.fullName()));
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        JsonResponse.write(response, 200, Map.of("message", "Logged out"));
    }
}
