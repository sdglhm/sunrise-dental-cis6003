package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.model.User;
import lk.ac.icbt.sunrisedental.util.DatabaseConnectionFactory;
import java.sql.*;
import java.util.Optional;

public class JdbcUserDao implements UserDao {
    public Optional<User> findByUsername(String username) {
        try (Connection c = DatabaseConnectionFactory.getInstance().getConnection(); PreparedStatement s = c.prepareStatement("SELECT user_id, username, password_hash, full_name, active FROM users WHERE username = ?")) {
            s.setString(1, username); ResultSet r = s.executeQuery(); return r.next() ? Optional.of(new User(r.getLong(1), r.getString(2), r.getString(3), r.getString(4), r.getBoolean(5))) : Optional.empty();
        } catch (SQLException e) { throw new IllegalStateException("Could not read user", e); }
    }
}
