package pe.edu.vallegrande.model;

import pe.edu.vallegrande.dataBase.ConexionMySQL;
import pe.edu.vallegrande.model.Equipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public List<Equipo> listar() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipos_voley";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo e = new Equipo();
                e.setId(rs.getInt("id"));
                e.setNombreCliente(rs.getString("nombre_cliente"));
                e.setProducto(rs.getString("producto"));
                e.setCantidad(rs.getInt("cantidad"));
                e.setPrecioUnitario(rs.getDouble("precio_unitario"));
                e.setTotalPago(rs.getDouble("total_pago"));
                lista.add(e);
            }
        } catch (Exception ex) {
            System.err.println("Error al listar equipos: " + ex.getMessage());
        }
        return lista;
    }

    public void insertar(Equipo equipo) {
        String sql = "INSERT INTO equipos_voley(nombre_cliente, producto, cantidad, precio_unitario, total_pago) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipo.getNombreCliente());
            ps.setString(2, equipo.getProducto());
            ps.setInt(3, equipo.getCantidad());
            ps.setDouble(4, equipo.getPrecioUnitario());
            ps.setDouble(5, equipo.getTotalPago());
            ps.executeUpdate();

        } catch (Exception ex) {
            System.err.println("Error al insertar equipo: " + ex.getMessage());
        }
    }
}
