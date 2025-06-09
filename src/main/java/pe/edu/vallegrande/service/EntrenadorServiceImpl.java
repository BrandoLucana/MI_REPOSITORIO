package pe.edu.vallegrande.service;

import pe.edu.vallegrande.dataBase.ConexionMySQL;
import pe.edu.vallegrande.model.Entrenador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorServiceImpl implements EntrenadorService {

    @Override
    public void insertarEntrenador(Entrenador entrenador) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConexionMySQL.conectar();
            String sql = "INSERT INTO entrenadores (nombre, apellido, especialidad, telefono, email, activo) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, entrenador.getNombre());
            pstmt.setString(2, entrenador.getApellido());
            pstmt.setString(3, entrenador.getEspecialidad());
            pstmt.setString(4, entrenador.getTelefono());
            pstmt.setString(5, entrenador.getEmail());
            pstmt.setBoolean(6, entrenador.isActivo());
            pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el driver de MySQL", e);
        } finally {
            cerrarRecursos(conn, pstmt, null);
        }
    }

    @Override
    public Entrenador obtenerEntrenadorPorId(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Entrenador entrenador = null;
        try {
            conn = ConexionMySQL.conectar();
            String sql = "SELECT id, nombre, apellido, especialidad, telefono, email, activo FROM entrenadores WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                entrenador = crearEntrenadorDesdeResultSet(rs);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el driver de MySQL", e);
        } finally {
            cerrarRecursos(conn, pstmt, rs);
        }
        return entrenador;
    }

    @Override
    public List<Entrenador> listarEntrenadores() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Entrenador> entrenadores = new ArrayList<>();
        try {
            conn = ConexionMySQL.conectar();
            String sql = "SELECT id, nombre, apellido, especialidad, telefono, email, activo FROM entrenadores";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Entrenador entrenador = crearEntrenadorDesdeResultSet(rs);
                entrenadores.add(entrenador);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el driver de MySQL", e);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
        return entrenadores;
    }

    @Override
    public void actualizarEntrenador(Entrenador entrenador) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConexionMySQL.conectar();
            String sql = "UPDATE entrenadores SET nombre = ?, apellido = ?, especialidad = ?, telefono = ?, email = ?, activo = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, entrenador.getNombre());
            pstmt.setString(2, entrenador.getApellido());
            pstmt.setString(3, entrenador.getEspecialidad());
            pstmt.setString(4, entrenador.getTelefono());
            pstmt.setString(5, entrenador.getEmail());
            pstmt.setBoolean(6, entrenador.isActivo());
            pstmt.setInt(7, entrenador.getId());
            pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el driver de MySQL", e);
        } finally {
            cerrarRecursos(conn, pstmt, null);
        }
    }

    @Override
    public void eliminarEntrenador(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConexionMySQL.conectar();
            String sql = "DELETE FROM entrenadores WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el driver de MySQL", e);
        } finally {
            cerrarRecursos(conn, pstmt, null);
        }
    }

    private Entrenador crearEntrenadorDesdeResultSet(ResultSet rs) throws SQLException {
        return new Entrenador(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("especialidad"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getBoolean("activo")
        );
    }

    private void cerrarRecursos(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}