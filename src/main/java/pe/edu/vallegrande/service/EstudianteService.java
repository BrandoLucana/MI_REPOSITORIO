package pe.edu.vallegrande.service;

import pe.edu.vallegrande.model.Estudiante;
import pe.edu.vallegrande.dataBase.ConexionMySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet; // Importar ResultSet

public class EstudianteService {

    public List<Estudiante> insertarEstudiante(Estudiante estudiante) throws SQLException {
        Connection conn = ConexionMySQL.conectar();
        if (conn != null) {
            try {
                String sql = "INSERT INTO estudiantes (nombre, apellido, edad, dni, correo, celular, categoria, genero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, estudiante.getNombre());
                pstmt.setString(2, estudiante.getApellido());
                pstmt.setInt(3, estudiante.getEdad());
                pstmt.setString(4, estudiante.getDni());
                pstmt.setString(5, estudiante.getCorreo());
                pstmt.setString(6, estudiante.getCelular());
                pstmt.setString(7, estudiante.getCategoria());
                pstmt.setString(8, estudiante.getGenero());
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                throw new SQLException("Error al guardar datos: " + ex.getMessage());
            } finally {
                conn.close();
            }
        } else {
            throw new SQLException("No se pudo conectar a la base de datos.");
        }
        return null;
    }

    public List<Estudiante> obtenerTodosEstudiantes() throws SQLException {
        List<Estudiante> estudiantes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConexionMySQL.conectar();
            String sql = "SELECT id, nombre, apellido, edad, dni, correo, celular, categoria, genero FROM estudiantes";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Estudiante estudiante = new Estudiante(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getInt("edad"),
                        rs.getString("categoria")
                );
                estudiante.setId(rs.getInt("id"));
                estudiante.setDni(rs.getString("dni"));
                estudiante.setCorreo(rs.getString("correo"));
                estudiante.setCelular(rs.getString("celular"));
                estudiante.setGenero(rs.getString("genero"));
                estudiantes.add(estudiante);
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener estudiantes: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return estudiantes;
    }
}
