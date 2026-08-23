package lk.ac.icbt.sunrisedental.service;

import lk.ac.icbt.sunrisedental.dao.UserDao;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.User;
import lk.ac.icbt.sunrisedental.util.PasswordUtil;
import java.util.Optional;

public class AuthenticationService {
    private final UserDao users;
    public AuthenticationService(UserDao users) { this.users = users; }
    public User authenticate(String username, String password) {
        if (username == null || password == null) throw new ValidationException("Username and password are required");
        Optional<User> user = users.findByUsername(username.trim());
        if (user.isEmpty() || !user.get().active() || !PasswordUtil.matches(password, user.get().passwordHash())) throw new ValidationException("Invalid username or password");
        return user.get();
    }
}
