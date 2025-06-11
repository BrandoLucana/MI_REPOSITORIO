package pe.edu.vallegrande.model;
import pe.edu.vallegrande.dataBase.ConexionMySQL;
import pe.edu.vallegrande.model.Polo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PoloDAO {

    public void insertar(Polo polo) {
        String sql = "INSERT INTO polos (nombre_en_polo, numero_en_polo, talla, deporte, incluye_short, incluye_medias) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, polo.getNombreEnPolo());
            stmt.setInt(2, polo.getNumeroEnPolo());
            stmt.setString(3, polo.getTalla());
            stmt.setString(4, polo.getDeporte());
            stmt.setBoolean(5, polo.isIncluyeShort());
            stmt.setBoolean(6, polo.isIncluyeMedias());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizar(Polo polo) {
        String sql = "UPDATE polos SET nombre_en_polo=?, numero_en_polo=?, talla=?, deporte=?, incluye_short=?, incluye_medias=? WHERE id=?";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, polo.getNombreEnPolo());
            stmt.setInt(2, polo.getNumeroEnPolo());
            stmt.setString(3, polo.getTalla());
            stmt.setString(4, polo.getDeporte());
            stmt.setBoolean(5, polo.isIncluyeShort());
            stmt.setBoolean(6, polo.isIncluyeMedias());
            stmt.setInt(7, polo.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM polos WHERE id=?";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Polo> listar() {
        List<Polo> lista = new ArrayList<>();
        String sql = "SELECT * FROM polos";
        try (Connection conn = ConexionMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Polo polo = new Polo();
                polo.setId(rs.getInt("id"));
                polo.setNombreEnPolo(rs.getString("nombre_en_polo"));
                polo.setNumeroEnPolo(rs.getInt("numero_en_polo"));
                polo.setTalla(rs.getString("talla"));
                polo.setDeporte(rs.getString("deporte"));
                polo.setIncluyeShort(rs.getBoolean("incluye_short"));
                polo.setIncluyeMedias(rs.getBoolean("incluye_medias"));
                lista.add(polo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

}
