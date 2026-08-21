package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.model.User;
import java.util.Optional;

public interface UserDao { Optional<User> findByUsername(String username); }
