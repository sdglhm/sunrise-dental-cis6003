package lk.ac.icbt.sunrisedental.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public final class JsonResponse {
    public static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private JsonResponse() { }
    public static <T> T body(HttpServletRequest request, Class<T> type) throws IOException { return MAPPER.readValue(request.getInputStream(), type); }
    public static void write(HttpServletResponse response, int status, Object value) throws IOException { response.setStatus(status); response.setContentType("application/json"); response.setCharacterEncoding("UTF-8"); MAPPER.writeValue(response.getWriter(), value); }
    public static void error(HttpServletResponse response, int status, String message) throws IOException { write(response, status, Map.of("error", message)); }
}
