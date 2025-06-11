package pe.edu.vallegrande.controller;

import pe.edu.vallegrande.model.PoloDAO;
import pe.edu.vallegrande.model.Polo;

import java.util.List;

public class PoloController {
    private final PoloDAO dao = new PoloDAO();

    public void registrar(Polo polo) {
        dao.insertar(polo);
    }

    public void actualizar(Polo polo) {
        dao.actualizar(polo);
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }

    public List<Polo> listar() {
        return dao.listar();
    }
}
