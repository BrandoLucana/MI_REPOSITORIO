package pe.edu.vallegrande.service;

import pe.edu.vallegrande.model.Entrenador;
import java.sql.SQLException;
import java.util.List;

public interface EntrenadorService {

    void insertarEntrenador(Entrenador entrenador) throws SQLException;

    Entrenador obtenerEntrenadorPorId(int id) throws SQLException; // Opcional, si necesitas buscar por ID

    List<Entrenador> listarEntrenadores() throws SQLException;

    void actualizarEntrenador(Entrenador entrenador) throws SQLException;

    void eliminarEntrenador(int id) throws SQLException;
}