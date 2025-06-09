package pe.edu.vallegrande.controller;

import pe.edu.vallegrande.model.Entrenador;
import pe.edu.vallegrande.view.FormularioEntrenador;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import pe.edu.vallegrande.service.EntrenadorService;
import pe.edu.vallegrande.service.EntrenadorServiceImpl;

public class EntrenadorController {

    private FormularioEntrenador vista;
    private EntrenadorService servicio;

    public EntrenadorController(FormularioEntrenador vista) {
        this.vista = vista;
        this.servicio = new EntrenadorServiceImpl(); // Inicializa la instancia de EntrenadorService
        this.vista.setController(this);
        cargarEntrenadores();
    }

    public void guardarEntrenador(String nombre, String apellido, String especialidad, String telefono, String email, boolean activo) {
        try {
            Entrenador entrenador = new Entrenador(nombre, apellido, especialidad, telefono, email, activo);
            servicio.insertarEntrenador(entrenador);
            cargarEntrenadores();
            vista.mostrarMensaje("Entrenador registrado exitosamente.");
            vista.limpiarFormulario();
        } catch (SQLException ex) {
            vista.mostrarError("Error al guardar el entrenador: " + ex.getMessage());
        }
    }

    public void eliminarEntrenador(int id) {
        try {
            servicio.eliminarEntrenador(id);
            cargarEntrenadores();
            vista.mostrarMensaje("Entrenador eliminado exitosamente.");
            vista.limpiarFormulario();
        } catch (SQLException ex) {
            vista.mostrarError("Error al eliminar el entrenador: " + ex.getMessage());
        }
    }

    public void cargarEntrenadorParaEditar(int fila) {
        try {
            if (fila >= 0) {
                int id = (int) vista.getModeloTabla().getValueAt(fila, 0); // Obtener el ID de la tabla
                Entrenador entrenador = servicio.obtenerEntrenadorPorId(id);
                if (entrenador != null) {
                    vista.cargarDatosFormulario(entrenador);
                } else {
                    vista.mostrarError("No se encontró el entrenador con ID: " + id);
                }
            }
        } catch (SQLException ex) {
            vista.mostrarError("Error al cargar entrenador para editar: " + ex.getMessage());
        } catch (IndexOutOfBoundsException ex) {
            vista.mostrarError("Fila seleccionada no válida: " + ex.getMessage());
        }
    }

    public void actualizarEntrenador(int id, String nombre, String apellido, String especialidad, String telefono, String email, boolean activo) {
        try {
            Entrenador entrenador = new Entrenador(id, nombre, apellido, especialidad, telefono, email, activo);
            servicio.actualizarEntrenador(entrenador);
            cargarEntrenadores();
            vista.mostrarMensaje("Entrenador actualizado exitosamente.");
            vista.limpiarFormulario();
        } catch (SQLException ex) {
            vista.mostrarError("Error al actualizar el entrenador: " + ex.getMessage());
        }
    }

    public void cargarEntrenadores() {
        DefaultTableModel modeloTabla = vista.getModeloTabla();
        modeloTabla.setRowCount(0);
        try {
            List<Entrenador> entrenadores = servicio.listarEntrenadores();
            for (Entrenador entrenador : entrenadores) {
                modeloTabla.addRow(new Object[]{
                        entrenador.getId(), // Asegúrate de que ID esté incluido
                        entrenador.getNombre(),
                        entrenador.getApellido(),
                        entrenador.getEspecialidad(),
                        entrenador.getTelefono(),
                        entrenador.getEmail(),
                        entrenador.isActivo()
                });
            }
        } catch (SQLException ex) {
            vista.mostrarError("Error al cargar los entrenadores: " + ex.getMessage());
        }
    }
}