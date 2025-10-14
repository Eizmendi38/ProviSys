package com.provisys;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/odoo?currentSchema=public";
    private static final String USER = "odoo";
    private static final String PASSWORD = "odoo";

    public static void main(String[] args) {
        //prueba inicial de conexión, utilizando los datos introducidos.
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (conn != null) {
                System.out.println("Conexión exitosa a la base de datos PostgreSQL.");
                Scanner sc = new Scanner(System.in);
                do {
                    System.out.println("Seleccione una opción:\n1. Mostrar 5 primeros clientes junto a sus datos relevantes\n2. Contar clientes en total\n3. Salir del programa");
                    try{ 
                        int opt = sc.nextInt();
                        sc.nextLine();
                        switch(opt) {
                            case 1:
                                listarClientes(conn);
                                break;
                            case 2:
                                contarClientes(conn);
                                break;
                            case 3:
                                System.out.println("Adios.");
                                return;
                            default:
                                System.out.println("No.");
                                break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Opción invalida.");
                        break;
                    }
                    

                } while (true);
            }
        } catch (SQLException e) {
            //en caso de error, imprime la causa.
            System.out.println("¡Oops!:");
            e.printStackTrace();
        }
    }

        private static void listarClientes(Connection conn) {
        String sql = "SELECT id, create_date, name, email, street FROM public.res_partner ORDER BY id LIMIT 5";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nMostrando clientes.");
            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp fecha = rs.getTimestamp("create_date");
                String nombre = rs.getString("name");
                String email = rs.getString("email");
                String calle = rs.getString("street");
                System.out.println("ID: "+id+" | Nombre: "+nombre+" | Correo Electrónico: "+email+" | Calle: "+calle+" | Fecha de registro: "+fecha.toString());
            }

        } catch (SQLException e) {
            System.out.println("Error listando clientes: ");
            e.printStackTrace();
        }
    }

    private static void contarClientes(Connection conn) {
        String sql = "SELECT COUNT(*) AS total FROM public.res_partner";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("\nTotal de clientes: " + rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error contando clientes: ");
            e.printStackTrace();
        }
    }
}