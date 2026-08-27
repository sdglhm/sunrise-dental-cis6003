package lk.ac.icbt.sunrisedental.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class DatabaseConnectionFactoryTest {
    @Test
    void returnsTheSameFactoryInstance() {
        assertSame(DatabaseConnectionFactory.getInstance(), DatabaseConnectionFactory.getInstance());
    }
}
