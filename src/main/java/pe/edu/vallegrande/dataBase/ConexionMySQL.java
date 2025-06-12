package pe.edu.vallegrande.dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ConexionMySQL {
    private static final Logger LOGGER = Logger.getLogger(ConexionMySQL.class.getName());
    private static final String URL = "jdbc:mysql://database-2.cbch08rjasxu.us-east-1.rds.amazonaws.com:3306/appJava?useSSL=false&serverTimezone=UTC";
    private static final String USER = "admin"; // Cambia según tu usuario
    private static final String PASSWORD = "981837328rds"; // Cambia según tu contraseña

    public static Connection conectar() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        try {
            // Cargar el driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.log(Level.INFO, "Driver JDBC cargado correctamente.");
            // Establecer la conexión
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.log(Level.INFO, "Conexión a la base de datos establecida correctamente.");
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Error: Driver JDBC no encontrado. Asegúrate de que el conector MySQL esté en el classpath.", ex);
            throw ex; // Propagar la excepción
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al conectar a la base de datos: " + ex.getMessage(), ex);
            throw ex; // Propagar la excepción
        }
        return conn;
    }
}