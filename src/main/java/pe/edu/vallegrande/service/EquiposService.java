package pe.edu.vallegrande.service;

import pe.edu.vallegrande.model.Equipo;
import java.util.List;

public interface EquiposService {
    List<Equipo> listar();
    void registrar(Equipo equipo);
}
