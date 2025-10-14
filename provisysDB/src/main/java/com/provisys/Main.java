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
                //si la conexión no es nula, procede con el resto del programa
                System.out.println("Conexión exitosa a la base de datos PostgreSQL.");
                //creación del scanner para lectura en el menú
                Scanner sc = new Scanner(System.in);
                do {
                    System.out.println("Seleccione una opción:\n1. Mostrar 5 primeros clientes junto a sus datos relevantes\n2. Contar clientes en total\n3. Salir del programa");
                    try{ //try catch para asegurar que el valor introducido sea un integer
                        int opt = sc.nextInt();
                        sc.nextLine();
                        //switch para el menú
                        switch(opt) {
                            case 1:
                                //llama al método de listado de clientes utilizando la conexión preestablecida
                                listarClientes(conn);
                                break;
                            case 2:
                                //llama al método de contado de clientes utilizando la conexión preestablecida
                                contarClientes(conn);
                                break;
                            case 3:
                                //salida del sistema con cerrado del scanner
                                System.out.println("Adios.");
                                sc.close();
                                return;
                            default:
                                System.out.println("No.");
                                break;
                        }
                    } catch (InputMismatchException e) {
                        //vuelve a intentarlo en caso de introducirlo equivocadamente
                        System.out.println("Opción invalida.");
                        break;
                    }
                    

                } while (true);
            }
        } catch (SQLException e) {
            //en caso de error, imprime la causa y cierra el programa.
            System.out.println("¡Oops!:");
            e.printStackTrace();
        }
    }

        private static void listarClientes(Connection conn) {
        String sql = "SELECT id, create_date, name, email, street FROM public.res_partner ORDER BY id LIMIT 5";
        //prepara el statement de la BBDD de antemano, y luego prueba a ejecutarlo utilizando la conexión que se le pasa en la llamada de método
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
                //crea el resultset que guarda los resultados de la consulta, y luego asigna los valores del resultset a diferentes campos para su impresión
            System.out.println("\nMostrando clientes.");
            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp fecha = rs.getTimestamp("create_date");
                String nombre = rs.getString("name");
                String email = rs.getString("email");
                String calle = rs.getString("street");
                System.out.println("ID: "+id+" | Nombre: "+nombre+" | Correo Electrónico: "+email+" | Calle: "+calle+" | Fecha de registro: "+fecha.toString()+"\n----------------------------------------------------");
            }

        } catch (SQLException e) {
            //en caso de que ocurra cualquier error, lo reporta aquí abajo y vuelve al menú anterior
            System.out.println("Error listando clientes: ");
            e.printStackTrace();
        }
    }

    private static void contarClientes(Connection conn) {
        String sql = "SELECT COUNT(*) AS total FROM public.res_partner";
        //prepara el statement de la BBDD de antemano, y luego prueba a ejecutarlo utilizando la conexión que se le pasa en la llamada de método
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
                //en este caso, el resultset solo es un valor, así que no es necesario asignarlo a una variable; lo imprimimos directamente.
            if (rs.next()) {
                System.out.println("\nTotal de clientes: " + rs.getInt("total"));
            }

        } catch (SQLException e) {
            //en caso de que ocurra cualquier error, lo reporta aquí abajo y vuelve al menú anterior
            System.out.println("Error contando clientes: ");
            e.printStackTrace();
        }
    }
}