package lk.ac.icbt.sunrisedental.model;

import java.math.BigDecimal;

public record Treatment(long id, String name, BigDecimal price, boolean active) { }
