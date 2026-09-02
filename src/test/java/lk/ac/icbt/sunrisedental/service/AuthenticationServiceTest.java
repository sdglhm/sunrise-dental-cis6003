package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.UserDao;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.exception.InvalidCredentialsException;
import lk.ac.icbt.sunrisedental.model.User;
import lk.ac.icbt.sunrisedental.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {
    @Test void authenticatesActiveUserWithMatchingCredentials() {
        User user = new User(1, "staff", PasswordUtil.hash("secret"), "Staff User", true);
        AuthenticationService service = new AuthenticationService(name -> Optional.of(user));
        assertEquals("staff", service.authenticate("staff", "secret").username());
    }
    @Test void rejectsIncorrectPassword() {
        AuthenticationService service = new AuthenticationService(name -> Optional.of(new User(1, "staff", PasswordUtil.hash("secret"), "Staff", true)));
        assertThrows(InvalidCredentialsException.class, () -> service.authenticate("staff", "wrong"));
    }
    @Test void rejectsBlankCredentials() {
        AuthenticationService service = new AuthenticationService(name -> Optional.empty());
        assertThrows(ValidationException.class, () -> service.authenticate(" ", ""));
    }
    @Test void rejectsInactiveUserWithoutDisclosingStatus() {
        User user = new User(1, "staff", PasswordUtil.hash("secret"), "Staff", false);
        AuthenticationService service = new AuthenticationService(name -> Optional.of(user));
        InvalidCredentialsException error = assertThrows(InvalidCredentialsException.class, () -> service.authenticate("staff", "secret"));
        assertEquals("Invalid username or password", error.getMessage());
    }
}
