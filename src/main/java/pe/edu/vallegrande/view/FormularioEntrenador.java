package pe.edu.vallegrande.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import pe.edu.vallegrande.controller.EntrenadorController;
import pe.edu.vallegrande.model.Entrenador;

public class FormularioEntrenador extends JPanel {

    // Campos del formulario
    private final JTextField txtNombre;
    private final JTextField txtApellido;
    private final JTextField txtTelefono;
    private final JTextField txtEmail;
    private final JComboBox<String> comboEspecialidad;
    private final JCheckBox chkEstado;

    // Estructura para el CRUD
    private final JTable tablaEntrenadores;
    private final DefaultTableModel modeloTabla;
    private int filaSeleccionada = -1;

    // Referencia al controlador
    private EntrenadorController controller;

    public FormularioEntrenador() {
        // Configuración del panel principal con BorderLayout
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 204, 204));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Inicializar modeloTabla y tabla primero
        String[] columnas = {"ID", "Nombre", "Apellido", "Especialidad", "Teléfono", "Email", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEntrenadores = new JTable(modeloTabla);
        tablaEntrenadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaEntrenadores);
        add(scroll, BorderLayout.CENTER);

        // Escucha selección en tabla
        tablaEntrenadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                filaSeleccionada = tablaEntrenadores.getSelectedRow();
                if (filaSeleccionada != -1) {
                    controller.cargarEntrenadorParaEditar(filaSeleccionada);
                }
            }
        });

        // Ahora inicializamos el controlador
        this.controller = new EntrenadorController(this);

        // Banner superior
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT));
        banner.setOpaque(false);
        JLabel lblLogoTexto = new JLabel("Pasión por el Vóley");
        lblLogoTexto.setFont(new Font("Arial", Font.BOLD, 18));
        lblLogoTexto.setForeground(Color.DARK_GRAY);
        banner.add(lblLogoTexto);
        add(banner, BorderLayout.NORTH);

        // Panel formulario (West)
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);

        // Apellido
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        txtApellido = new JTextField(15);
        panelFormulario.add(txtApellido, gbc);

        // Especialidad
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Especialidad:"), gbc);
        gbc.gridx = 1;
        comboEspecialidad = new JComboBox<>(new String[]{
                "Entrenador general",
                "Preparador Físico",
                "Analista de juego",
                "Gestor deportivo",
                "Entrenador de armadores",
                "Entrenador de categorías de formación"
        });
        panelFormulario.add(comboEspecialidad, gbc);

        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        panelFormulario.add(txtTelefono, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        panelFormulario.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(15);
        panelFormulario.add(txtEmail, gbc);

        // Estado
        gbc.gridx = 0;
        gbc.gridy = 5;
        panelFormulario.add(new JLabel("Activo:"), gbc);
        gbc.gridx = 1;
        chkEstado = new JCheckBox();
        panelFormulario.add(chkEstado, gbc);

        // Botones de formulario
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Guardar");
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);

        add(panelFormulario, BorderLayout.WEST);

        // Acciones de botones
        btnGuardar.addActionListener(e -> guardarOActualizar());
        btnCancelar.addActionListener(e -> {
            // Limpia el formulario al cancelar
            limpiarFormulario();
            // La navegación al MenuPrincipal se manejará desde LoginMenuApp
        });
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
    }

    // Método para establecer el controlador (si decides usar inyección)
    public void setController(EntrenadorController controller) {
        this.controller = controller;
    }

    // Método para mostrar mensajes
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método para mostrar errores
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Método para limpiar el formulario
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        comboEspecialidad.setSelectedIndex(0);
        txtTelefono.setText("");
        txtEmail.setText("");
        chkEstado.setSelected(false);
        tablaEntrenadores.clearSelection();
        filaSeleccionada = -1;
    }

    // Método para cargar los datos de un entrenador en el formulario
    public void cargarDatosFormulario(Entrenador entrenador) {
        txtNombre.setText(entrenador.getNombre());
        txtApellido.setText(entrenador.getApellido());
        comboEspecialidad.setSelectedItem(entrenador.getEspecialidad());
        txtTelefono.setText(entrenador.getTelefono());
        txtEmail.setText(entrenador.getEmail());
        chkEstado.setSelected(entrenador.isActivo());
        filaSeleccionada = tablaEntrenadores.getSelectedRow();
    }

    // Método para obtener el modelo de la tabla
    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    private void guardarOActualizar() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Error: Controlador no inicializado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String especialidad = (String) comboEspecialidad.getSelectedItem();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        boolean activo = chkEstado.isSelected();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Apellido son obligatorios.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (filaSeleccionada == -1) {
                // Nuevo entrenador
                controller.guardarEntrenador(nombre, apellido, especialidad, telefono, email, activo);
            } else {
                // Actualizar entrenador existente
                int id = obtenerIdEntrenador(filaSeleccionada);
                controller.actualizarEntrenador(id, nombre, apellido, especialidad, telefono, email, activo);
            }
            // Recargar la tabla después de guardar o actualizar
            controller.cargarEntrenadores();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError("Error al guardar/actualizar el entrenador: " + e.getMessage());
        }
    }

    private void eliminarSeleccionado() {
        if (filaSeleccionada != -1) {
            int resp = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar entrenador seleccionado?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                try {
                    int id = obtenerIdEntrenador(filaSeleccionada);
                    controller.eliminarEntrenador(id);
                    // Recargar la tabla después de eliminar
                    controller.cargarEntrenadores();
                    limpiarFormulario();
                } catch (Exception e) {
                    mostrarError("Error al eliminar el entrenador: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador para eliminar.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int obtenerIdEntrenador(int fila) {
        if (fila >= 0 && fila < modeloTabla.getRowCount()) {
            return (int) modeloTabla.getValueAt(fila, 0); // Obtener el ID de la primera columna
        }
        throw new IllegalStateException("Fila seleccionada no válida: " + fila);
    }
}