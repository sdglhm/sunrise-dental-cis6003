package lk.ac.icbt.sunrisedental.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class AuthenticationFilterTest {
    @Test void blocksUnauthenticatedApiRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getRequestURI()).thenReturn("/sunrise-dental/api/appointments");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        new AuthenticationFilter().doFilter(request, response, chain);
        verify(response).setStatus(401);
        verifyNoInteractions(chain);
    }

    @Test void permitsAuthenticatedApiRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getRequestURI()).thenReturn("/sunrise-dental/api/appointments");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new Object());
        new AuthenticationFilter().doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }
}
