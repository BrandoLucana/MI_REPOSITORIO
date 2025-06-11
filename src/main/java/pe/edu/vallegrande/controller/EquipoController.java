package pe.edu.vallegrande.controller;

import pe.edu.vallegrande.model.Equipo;
import pe.edu.vallegrande.service.EquipoServiceImpl;

import java.util.List;

public class EquipoController {
    private final EquipoServiceImpl service = new EquipoServiceImpl();

    public List<Equipo> listar() {
        return service.listar();
    }

    public void registrar(Equipo equipo) {
        service.registrar(equipo);
    }
}
