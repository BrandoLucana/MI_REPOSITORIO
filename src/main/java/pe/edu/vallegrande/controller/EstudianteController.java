package pe.edu.vallegrande.controller;

import pe.edu.vallegrande.model.Estudiante;
import pe.edu.vallegrande.service.EstudianteService;
import pe.edu.vallegrande.view.FormularioEstudiante;

import java.sql.SQLException;

public class EstudianteController {

    private final FormularioEstudiante vista;
    private final EstudianteService servicio;

    public EstudianteController(FormularioEstudiante vista) {
        this.vista = vista;
        this.servicio = new EstudianteService();
    }

    public void guardarEstudiante(String nombre, String apellido, int edad,
                                  String tipoDocumento, String numeroDocumento,
                                  String correo, String celular,
                                  String categoria, String genero) {
        Estudiante estudiante = new Estudiante(nombre, apellido, edad, categoria);
        estudiante.setTipoDocumento(tipoDocumento);
        estudiante.setNumeroDocumento(numeroDocumento);
        estudiante.setCorreo(correo);
        estudiante.setCelular(celular);
        estudiante.setGenero(genero);

        try {
            servicio.insertarEstudiante(estudiante);
            vista.mostrarMensaje("Estudiante registrado exitosamente.");
            vista.limpiarFormulario();
        } catch (SQLException e) {
            vista.mostrarError("Error al guardar estudiante: " + e.getMessage());
        }
    }
}