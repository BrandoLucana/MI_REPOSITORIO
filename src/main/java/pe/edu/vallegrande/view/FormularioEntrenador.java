package pe.edu.vallegrande.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import pe.edu.vallegrande.controller.EntrenadorController;
import pe.edu.vallegrande.model.Entrenador;

public class FormularioEntrenador extends JPanel {

    private final JTextField txtNombre;
    private final JTextField txtApellido;
    private final JTextField txtTelefono;
    private final JTextField txtEmail;
    private final JComboBox<String> comboEspecialidad;
    private final JCheckBox chkEstado;
    private final JTable tablaEntrenadores;
    private final DefaultTableModel modeloTabla;
    private int filaSeleccionada = -1;
    private EntrenadorController controller;

    public FormularioEntrenador() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(200, 50, 50)); // Fondo rojo oscuro
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tabla y scroll
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

        tablaEntrenadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                filaSeleccionada = tablaEntrenadores.getSelectedRow();
                if (filaSeleccionada != -1) {
                    controller.cargarEntrenadorParaEditar(filaSeleccionada);
                }
            }
        });

        this.controller = new EntrenadorController(this);

        // Banner superior
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT));
        banner.setBackground(new Color(200, 50, 50));
        JLabel lblLogoTexto = new JLabel("Pasión por el Vóley");
        lblLogoTexto.setFont(new Font("Arial", Font.BOLD, 18));
        lblLogoTexto.setForeground(Color.WHITE);
        banner.add(lblLogoTexto);
        add(banner, BorderLayout.NORTH);

        // Panel formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(new Color(200, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(labelFont);
        lblNombre.setForeground(Color.WHITE);
        panelFormulario.add(lblNombre, gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);

        // Apellido
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setFont(labelFont);
        lblApellido.setForeground(Color.WHITE);
        panelFormulario.add(lblApellido, gbc);
        gbc.gridx = 1;
        txtApellido = new JTextField(15);
        panelFormulario.add(txtApellido, gbc);

        // Especialidad
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblEspecialidad = new JLabel("Especialidad:");
        lblEspecialidad.setFont(labelFont);
        lblEspecialidad.setForeground(Color.WHITE);
        panelFormulario.add(lblEspecialidad, gbc);
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
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(labelFont);
        lblTelefono.setForeground(Color.WHITE);
        panelFormulario.add(lblTelefono, gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        panelFormulario.add(txtTelefono, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        lblEmail.setForeground(Color.WHITE);
        panelFormulario.add(lblEmail, gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(15);
        panelFormulario.add(txtEmail, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblActivo = new JLabel("Activo:");
        lblActivo.setFont(labelFont);
        lblActivo.setForeground(Color.WHITE);
        panelFormulario.add(lblActivo, gbc);
        gbc.gridx = 1;
        chkEstado = new JCheckBox();
        chkEstado.setOpaque(false);
        chkEstado.setForeground(Color.WHITE);
        panelFormulario.add(chkEstado, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(new Color(200, 50, 50));
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Guardar");
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);

        add(panelFormulario, BorderLayout.WEST);

        // Eventos
        btnGuardar.addActionListener(e -> guardarOActualizar());
        btnCancelar.addActionListener(e -> limpiarFormulario());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
    }

    public void setController(EntrenadorController controller) {
        this.controller = controller;
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

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

    public void cargarDatosFormulario(Entrenador entrenador) {
        txtNombre.setText(entrenador.getNombre());
        txtApellido.setText(entrenador.getApellido());
        comboEspecialidad.setSelectedItem(entrenador.getEspecialidad());
        txtTelefono.setText(entrenador.getTelefono());
        txtEmail.setText(entrenador.getEmail());
        chkEstado.setSelected(entrenador.isActivo());
        filaSeleccionada = tablaEntrenadores.getSelectedRow();
    }

    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    private void guardarOActualizar() {
        if (controller == null) {
            mostrarError("Error: Controlador no inicializado.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String especialidad = (String) comboEspecialidad.getSelectedItem();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        boolean activo = chkEstado.isSelected();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            mostrarError("Nombre y Apellido son obligatorios.");
            return;
        }

        try {
            if (filaSeleccionada == -1) {
                controller.guardarEntrenador(nombre, apellido, especialidad, telefono, email, activo);
            } else {
                int id = obtenerIdEntrenador(filaSeleccionada);
                controller.actualizarEntrenador(id, nombre, apellido, especialidad, telefono, email, activo);
            }
            controller.cargarEntrenadores();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError("Error al guardar/actualizar el entrenador: " + e.getMessage());
        }
    }

    private void eliminarSeleccionado() {
        if (filaSeleccionada != -1) {
            int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar entrenador seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                try {
                    int id = obtenerIdEntrenador(filaSeleccionada);
                    controller.eliminarEntrenador(id);
                    controller.cargarEntrenadores();
                    limpiarFormulario();
                } catch (Exception e) {
                    mostrarError("Error al eliminar el entrenador: " + e.getMessage());
                }
            }
        } else {
            mostrarMensaje("Selecciona un entrenador para eliminar.");
        }
    }

    private int obtenerIdEntrenador(int fila) {
        if (fila >= 0 && fila < modeloTabla.getRowCount()) {
            return (int) modeloTabla.getValueAt(fila, 0);
        }
        throw new IllegalStateException("Fila seleccionada no válida: " + fila);
    }
}
