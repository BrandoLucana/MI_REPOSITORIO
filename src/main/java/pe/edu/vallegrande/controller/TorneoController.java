package pe.edu.vallegrande.controller;

import pe.edu.vallegrande.model.Torneo;
import pe.edu.vallegrande.service.TorneoService;

import java.util.List;
import java.util.Date;

public class TorneoController {

    private TorneoService torneoService;

    public TorneoController() {
        this.torneoService = new TorneoService();
    }

    public List<Torneo> obtenerTorneos() {
        return torneoService.getAllTorneos();
    }

    public void agregarTorneo(String nombre, Date fecha, String lugar, String nivel, String descripcion, String estado) {
        Torneo torneo = new Torneo(0, nombre, fecha, lugar, nivel, descripcion, estado);
        torneoService.addTorneo(torneo);
    }

    public void modificarTorneo(int id, String nombre, Date fecha, String lugar, String nivel, String descripcion, String estado) {
        Torneo torneo = new Torneo(id, nombre, fecha, lugar, nivel, descripcion, estado);
        torneoService.updateTorneo(torneo);
    }

    public void eliminarTorneo(int id) {
        torneoService.deleteTorneo(id);
    }
}

