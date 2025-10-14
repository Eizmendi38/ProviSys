package com.provisys;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "odoo";
    private static final String PASSWORD = "odoo";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (conn != null) {
                System.out.println("Conexión exitosa a la base de datos PostgreSQL.");
            }
        } catch (SQLException e) {
            System.out.println("KILL YOURSELF:");
            e.printStackTrace();
        }
    }
}