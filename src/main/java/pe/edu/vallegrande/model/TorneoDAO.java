package pe.edu.vallegrande.model;

import pe.edu.vallegrande.dataBase.ConexionMySQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TorneoDAO {

    public List<Torneo> getAllTorneos() throws SQLException {
        List<Torneo> torneos = new ArrayList<>();
        String sql = "SELECT * FROM torneos";
        try (Connection conn = ConexionMySQL.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Torneo torneo = new Torneo(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        new Date(rs.getDate("fecha").getTime()),
                        rs.getString("lugar"),
                        rs.getString("nivel"),
                        rs.getString("descripcion"),
                        rs.getString("estado")
                );
                torneos.add(torneo);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el controlador JDBC: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new SQLException("Error al obtener torneos: " + e.getMessage(), e);
        }
        return torneos;
    }

    public void addTorneo(Torneo torneo) throws SQLException {
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
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el controlador JDBC: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new SQLException("Error al agregar torneo: " + e.getMessage(), e);
        }
    }

    public void updateTorneo(Torneo torneo) throws SQLException {
        String sql = "UPDATE torneos SET nombre = ?, fecha = ?, lugar = ?, nivel = ?, descripcion = ?, estado = ? WHERE id = ?";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, torneo.getNombre());
            pstmt.setDate(2, new java.sql.Date(torneo.getFecha().getTime()));
            pstmt.setString(3, torneo.getLugar());
            pstmt.setString(4, torneo.getNivel());
            pstmt.setString(5, torneo.getDescripcion());
            pstmt.setString(6, torneo.getEstado());
            pstmt.setInt(7, torneo.getId());
            pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el controlador JDBC: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new SQLException("Error al actualizar torneo: " + e.getMessage(), e);
        }
    }

    public void deleteTorneo(int id) throws SQLException {
        String sql = "DELETE FROM torneos WHERE id = ?";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el controlador JDBC: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new SQLException("Error al eliminar torneo: " + e.getMessage(), e);
        }
    }
}