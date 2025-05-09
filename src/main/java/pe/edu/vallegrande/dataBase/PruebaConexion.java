package pe.edu.vallegrande.dataBase;

import java.sql.Connection;

public class PruebaConexion {
    public static void main(String[] args) {
        Connection conexion = ConexionMySQL.conectar();
        if (conexion != null) {
            System.out.println("Conexión establecida correctamente.");
        } else {
            System.out.println("No se pudo establecer la conexión.");
        }
    }
}
