package pe.edu.vallegrande.model;

import pe.edu.vallegrande.dataBase.ConexionMySQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TorneoDAO {

    public List<Torneo> getAllTorneos() {
        List<Torneo> torneos = new ArrayList<>();
        String sql = "SELECT * FROM tournament";
        try (Connection conn = ConexionMySQL.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Torneo torneo = new Torneo(
                        rs.getInt("tournament_id"),
                        rs.getString("name"),
                        rs.getDate("date"),
                        rs.getString("location"),
                        rs.getString("level"),
                        rs.getString("description"),
                        rs.getString("status")
                );
                torneos.add(torneo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return torneos;
    }

    public void addTorneo(Torneo torneo) {
        String sql = "INSERT INTO tournament (name, date, location, level, description, status) VALUES (?, ?, ?, ?, ?, ?)";
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
        String sql = "UPDATE tournament SET name = ?, date = ?, location = ?, level = ?, description = ?, status = ? WHERE tournament_id = ?";
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
        String sql = "DELETE FROM tournament WHERE tournament_id = ?";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id); // Establecer el ID del torneo a eliminar
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
