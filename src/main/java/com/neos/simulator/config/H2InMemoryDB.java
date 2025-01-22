package com.neos.simulator.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class H2InMemoryDB {
    private static String jdbcUrl = "jdbc:h2:mem:testdb";
    private static String username = "sa";
    private static String password = "";

    public static Connection makeConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
