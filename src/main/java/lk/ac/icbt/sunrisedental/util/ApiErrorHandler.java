package lk.ac.icbt.sunrisedental.util;

import com.fasterxml.jackson.core.JacksonException;
import jakarta.servlet.http.HttpServletResponse;
import lk.ac.icbt.sunrisedental.exception.ConflictException;
import lk.ac.icbt.sunrisedental.exception.NotFoundException;
import lk.ac.icbt.sunrisedental.exception.ValidationException;

import java.io.IOException;
import java.time.DateTimeException;

public final class ApiErrorHandler {
    private ApiErrorHandler() { }

    public static void handle(HttpServletResponse response, Action action) throws IOException {
        handle(response, 422, action);
    }

    public static void handle(HttpServletResponse response, int validationStatus, Action action) throws IOException {
        try {
            action.run();
        } catch (ConflictException exception) {
            JsonResponse.error(response, 409, exception.getMessage());
        } catch (NotFoundException exception) {
            JsonResponse.error(response, 404, exception.getMessage());
        } catch (ValidationException exception) {
            JsonResponse.error(response, validationStatus, exception.getMessage());
        } catch (JacksonException | DateTimeException | NumberFormatException exception) {
            JsonResponse.error(response, 400, "Invalid request");
        } catch (Exception exception) {
            JsonResponse.error(response, 500, "The request could not be completed");
        }
    }

    @FunctionalInterface
    public interface Action {
        void run() throws Exception;
    }
}
