package lk.ac.icbt.sunrisedental.controller;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;

final class ServletTestSupport {
    private ServletTestSupport() { }

    static HttpServletRequest request(String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn(path);
        return request;
    }

    static HttpServletRequest request(String path, String json) throws IOException {
        HttpServletRequest request = request(path);
        ByteArrayInputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(request.getInputStream()).thenReturn(new ServletInputStream() {
            @Override public boolean isFinished() { return input.available() == 0; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(ReadListener listener) { }
            @Override public int read() { return input.read(); }
        });
        return request;
    }

    static CapturedResponse response() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        AtomicInteger status = new AtomicInteger();
        doAnswer(invocation -> { status.set(invocation.getArgument(0)); return null; }).when(response).setStatus(anyInt());
        return new CapturedResponse(response, body, status);
    }

    record CapturedResponse(HttpServletResponse response, StringWriter body, AtomicInteger status) { }
}
