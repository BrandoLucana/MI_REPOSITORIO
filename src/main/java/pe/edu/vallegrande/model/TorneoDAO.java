package pe.edu.vallegrande.model;

import pe.edu.vallegrande.dataBase.ConexionMySQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TorneoDAO {

    public List<Torneo> getAllTorneos() {
        List<Torneo> torneos = new ArrayList<>();
        String sql = "SELECT * FROM torneos";
        try (Connection conn = ConexionMySQL.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Torneo torneo = new Torneo(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDate("fecha"),
                        rs.getString("lugar"),
                        rs.getString("nivel"),
                        rs.getString("descripcion"),
                        rs.getString("estado")
                );
                torneos.add(torneo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return torneos;
    }

    public void addTorneo(Torneo torneo) {
        String sql = "INSERT INTO torneos (nombre, fecha, lugar, nivel, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, torneo.getNombre());
            pstmt.setDate(2, new java.sql.Date(torneo.getFecha().getTime()));
            pstmt.setString(3, torneo.getLugar());
            pstmt.setString(4, torneo.getNivel());
            pstmt.setString(5, torneo.getDescripcion());
            pstmt.setString(6, torneo.getEstado());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para actualizar un torneo
    public void updateTorneo(Torneo torneo) {
        String sql = "UPDATE torneos SET nombre = ?, fecha = ?, lugar = ?, nivel = ?, descripcion = ?, estado = ? WHERE id = ?";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, torneo.getNombre());
            pstmt.setDate(2, new java.sql.Date(torneo.getFecha().getTime()));
            pstmt.setString(3, torneo.getLugar());
            pstmt.setString(4, torneo.getNivel());
            pstmt.setString(5, torneo.getDescripcion());
            pstmt.setString(6, torneo.getEstado());
            pstmt.setInt(7, torneo.getId()); // Establecer el ID del torneo a actualizar
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para eliminar un torneo
    public void deleteTorneo(int id) {
        String sql = "DELETE FROM torneos WHERE id = ?";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id); // Establecer el ID del torneo a eliminar
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
