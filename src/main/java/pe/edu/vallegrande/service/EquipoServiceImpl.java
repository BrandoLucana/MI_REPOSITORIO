package pe.edu.vallegrande.service;

import pe.edu.vallegrande.model.Equipo;
import pe.edu.vallegrande.model.EquipoDAO;
import pe.edu.vallegrande.service.EquiposService;

import java.util.List;

public class EquipoServiceImpl implements EquiposService {
    private final EquipoDAO dao = new EquipoDAO();

    @Override
    public List<Equipo> listar() {
        return dao.listar();
    }

    @Override
    public void registrar(Equipo equipo) {
        dao.insertar(equipo);
    }
}
