package pe.edu.vallegrande.controller;

import pe.edu.vallegrande.model.Polo;
import pe.edu.vallegrande.service.PoloService;
import pe.edu.vallegrande.service.PoloServiceImpl;

import java.util.List;

public class PoloController {
    private PoloService service = new PoloServiceImpl();

    public void registrar(Polo polo) {
        service.registrar(polo);
    }

    public void actualizar(Polo polo) {
        service.actualizar(polo);
    }

    public void eliminar(int id) {
        service.eliminar(id);
    }

    public List<Polo> listar() {
        return service.listar();
    }
}