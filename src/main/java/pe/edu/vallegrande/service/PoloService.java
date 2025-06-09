package pe.edu.vallegrande.service;

import pe.edu.vallegrande.model.Polo;
import java.util.List;

public interface PoloService {
    void registrar(Polo polo);
    void actualizar(Polo polo);
    void eliminar(int id);
    List<Polo> listar();
}