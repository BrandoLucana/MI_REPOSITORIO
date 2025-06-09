package pe.edu.vallegrande.service;

import pe.edu.vallegrande.model.Polo;
import java.util.ArrayList;
import java.util.List;

public class PoloServiceImpl implements PoloService {
    private final List<Polo> lista = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void registrar(Polo polo) {
        polo.setId(nextId++);
        lista.add(polo);
    }

    @Override
    public void actualizar(Polo polo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == polo.getId()) {
                lista.set(i, polo);
                break;
            }
        }
    }

    @Override
    public void eliminar(int id) {
        lista.removeIf(p -> p.getId() == id);
    }

    @Override
    public List<Polo> listar() {
        return lista;
    }
}