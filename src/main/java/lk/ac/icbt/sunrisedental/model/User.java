package lk.ac.icbt.sunrisedental.model;

public record User(long id, String username, String passwordHash, String fullName, boolean active) { }
