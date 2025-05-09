package pe.edu.vallegrande.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import pe.edu.vallegrande.controller.EntrenadorController; // Importar el controlador
import pe.edu.vallegrande.model.Entrenador;

public class FormularioEntrenador extends JFrame {

    // Campos del formulario
    private JTextField txtNombre, txtApellido, txtTelefono, txtEmail;
    private JComboBox<String> comboEspecialidad;
    private JCheckBox chkEstado;
    private JButton btnGuardar, btnCancelar, btnEliminar;

    // Estructura para el CRUD
    private JTable tablaEntrenadores;
    private DefaultTableModel modeloTabla;
    private int filaSeleccionada = -1;

    // Referencia al controlador
    private EntrenadorController controller;

    public FormularioEntrenador() {
        setTitle("Formulario de Entrenador");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(255, 204, 204));
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(panelPrincipal);

        // Banner superior
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT));
        banner.setOpaque(false);
        JLabel lblLogoTexto = new JLabel("Pasión por el Vóley");
        lblLogoTexto.setFont(new Font("Arial", Font.BOLD, 18));
        lblLogoTexto.setForeground(Color.DARK_GRAY);
        banner.add(lblLogoTexto);
        panelPrincipal.add(banner, BorderLayout.NORTH);

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
        btnEliminar = new JButton("Eliminar");
        btnCancelar = new JButton("Cancelar");
        btnGuardar = new JButton("Guardar");
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);

        panelPrincipal.add(panelFormulario, BorderLayout.WEST);

        // Tabla de entrenadores (Center)
        String[] columnas = {"Nombre", "Apellido", "Especialidad", "Teléfono", "Email", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEntrenadores = new JTable(modeloTabla);
        tablaEntrenadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaEntrenadores);
        panelPrincipal.add(scroll, BorderLayout.CENTER);

        // Escucha selección en tabla
        tablaEntrenadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                filaSeleccionada = tablaEntrenadores.getSelectedRow();
                if (filaSeleccionada != -1) {
                    controller.cargarEntrenadorParaEditar(filaSeleccionada); // Llama al controlador
                }
            }
        });

        // Acciones de botones
        btnGuardar.addActionListener(e -> guardarOActualizar());
        btnCancelar.addActionListener(e -> {
            // Cierra el formulario actual
            dispose();
            // Abre el MenuPrincipal
            new MenuPrincipal().setVisible(true);
        });
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
    }

    // Método para establecer el controlador
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
    public void cargarDatosFormulario(Entrenador entrenador) { // Recibe un Entrenador
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

        if (filaSeleccionada == -1) {
            // Nuevo entrenador
            controller.guardarEntrenador(nombre, apellido, especialidad, telefono, email, activo);
        } else {
            // Actualizar entrenador existente
            int id = obtenerIdEntrenador(filaSeleccionada); // Debes obtener el ID de la fila seleccionada
            controller.actualizarEntrenador(id, nombre, apellido, especialidad, telefono, email, activo);
        }
    }

    private void eliminarSeleccionado() {
        if (filaSeleccionada != -1) {
            int resp = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar entrenador seleccionado?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                int id = obtenerIdEntrenador(filaSeleccionada); // Obtener el ID del entrenador a eliminar
                controller.eliminarEntrenador(id);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador para eliminar.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int obtenerIdEntrenador(int fila) {
        // Asumiendo que la primera columna de tu tabla es el ID.
        // Si no lo es, ajusta el índice de la columna (0 en este caso)
        return (int) modeloTabla.getValueAt(fila, 0);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormularioEntrenador formulario = new FormularioEntrenador();
            // Crear el controlador y pasar la vista
            EntrenadorController controller = new EntrenadorController(formulario);
            formulario.setController(controller); // Establecer el controlador en la vista
            formulario.setVisible(true);
        });
    }
}
