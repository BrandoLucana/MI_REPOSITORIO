package pe.edu.vallegrande.dataBase;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexion {
    public static void main(String[] args) {
        Connection conexion = null;
        try {
            conexion = ConexionMySQL.conectar();
            if (conexion != null) {
                System.out.println("Conexión establecida correctamente a " + ConexionMySQL.class.getName());
            } else {
                System.out.println("No se pudo establecer la conexión (valor nulo retornado).");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("No se pudo cargar el controlador JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos: " + e.getMessage());
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }
}