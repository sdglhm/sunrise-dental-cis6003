package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lk.ac.icbt.sunrisedental.exception.InvalidCredentialsException;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.User;
import lk.ac.icbt.sunrisedental.service.AuthenticationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServletTest {
    private final AuthenticationService authentication = mock(AuthenticationService.class);
    private final AuthServlet servlet = new AuthServlet(authentication);

    @Test void logsInAndRotatesExistingSession() throws Exception {
        User user = new User(1, "staff", "hash", "Clinic Staff", true);
        when(authentication.authenticate("staff", "secret")).thenReturn(user);
        HttpServletRequest request = ServletTestSupport.request("/login", "{\"username\":\"staff\",\"password\":\"secret\"}");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        var response = ServletTestSupport.response();
        servlet.doPost(request, response.response());
        assertEquals(200, response.status().get());
        verify(request).changeSessionId();
        verify(session).setAttribute("user", user);
    }

    @Test void returnsBadRequestForBlankCredentials() throws Exception {
        when(authentication.authenticate("", "")).thenThrow(new ValidationException("Username and password are required"));
        var response = ServletTestSupport.response();
        servlet.doPost(ServletTestSupport.request("/login", "{\"username\":\"\",\"password\":\"\"}"), response.response());
        assertEquals(400, response.status().get());
    }

    @Test void returnsUnauthorizedForInvalidCredentials() throws Exception {
        when(authentication.authenticate("staff", "wrong")).thenThrow(new InvalidCredentialsException());
        var response = ServletTestSupport.response();
        servlet.doPost(ServletTestSupport.request("/login", "{\"username\":\"staff\",\"password\":\"wrong\"}"), response.response());
        assertEquals(401, response.status().get());
        assertFalse(response.body().toString().contains("hash"));
    }

    @Test void logsOutAndInvalidatesSession() throws Exception {
        HttpServletRequest request = ServletTestSupport.request("/logout");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        var response = ServletTestSupport.response();
        servlet.doPost(request, response.response());
        verify(session).invalidate();
        assertEquals(200, response.status().get());
    }
}
