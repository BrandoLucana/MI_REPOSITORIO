package pe.edu.vallegrande.service;

import pe.edu.vallegrande.model.Torneo;
import pe.edu.vallegrande.dataBase.ConexionMySQL;
import pe.edu.vallegrande.dataBase.PruebaConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TorneoService {

    public List<Torneo> getAllTorneos() {
        List<Torneo> torneos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConexionMySQL.conectar(); // Cambiado "ConexiónMySQL" por "ConexionMySQL"
            String sql = "SELECT id, nombre, fecha, lugar, nivel, descripcion, estado FROM torneos";
            assert conn != null;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Torneo torneo = new Torneo();
                torneo.setId(rs.getInt("id"));
                torneo.setNombre(rs.getString("nombre"));
                torneo.setFecha(rs.getDate("fecha"));
                torneo.setLugar(rs.getString("lugar"));
                torneo.setNivel(rs.getString("nivel"));
                torneo.setDescripcion(rs.getString("descripcion"));
                torneo.setEstado(rs.getString("estado"));
                torneos.add(torneo);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace(); // Considerar un mejor manejo de errores
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return torneos;
    }

    public void addTorneo(Torneo torneo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConexionMySQL.conectar(); // Cambiado "ConexiónMySQL" por "ConexionMySQL"
            String sql = "INSERT INTO torneos (nombre, fecha, lugar, nivel, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, torneo.getNombre());
            pstmt.setDate(2, new java.sql.Date(torneo.getFecha().getTime()));
            pstmt.setString(3, torneo.getLugar());
            pstmt.setString(4, torneo.getNivel());
            pstmt.setString(5, torneo.getDescripcion());
            pstmt.setString(6, torneo.getEstado());
            pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace(); // Considerar un mejor manejo de errores
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTorneo(Torneo torneo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConexionMySQL.conectar(); // Cambiado "ConexiónMySQL" por "ConexionMySQL"
            String sql = "UPDATE torneos SET nombre = ?, fecha = ?, lugar = ?, nivel = ?, descripcion = ?, estado = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, torneo.getNombre());
            pstmt.setDate(2, new java.sql.Date(torneo.getFecha().getTime()));
            pstmt.setString(3, torneo.getLugar());
            pstmt.setString(4, torneo.getNivel());
            pstmt.setString(5, torneo.getDescripcion());
            pstmt.setString(6, torneo.getEstado());
            pstmt.setInt(7, torneo.getId());
            pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace(); // Considerar un mejor manejo de errores
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteTorneo(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConexionMySQL.conectar(); // Cambiado "ConexiónMySQL" por "ConexionMySQL"
            String sql = "DELETE FROM torneos WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace(); // Considerar un mejor manejo de errores
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}