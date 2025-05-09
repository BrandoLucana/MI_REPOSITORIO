package pe.edu.vallegrande;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FormularioEntrenador extends JFrame {

    // Campos del formulario
    private JTextField txtNombre, txtApellido, txtTelefono, txtEmail;
    private JComboBox<String> comboEspecialidad;
    private JCheckBox chkEstado;
    private JButton btnGuardar, btnCancelar, btnEliminar;

    // Estructura para el CRUD
    private ArrayList<Entrenador> entrenadores = new ArrayList<>();
    private JTable tablaEntrenadores;
    private DefaultTableModel modeloTabla;
    private int filaSeleccionada = -1;

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
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);

        // Apellido
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        txtApellido = new JTextField(15);
        panelFormulario.add(txtApellido, gbc);

        // Especialidad
        gbc.gridx = 0; gbc.gridy = 2;
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
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        panelFormulario.add(txtTelefono, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 4;
        panelFormulario.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(15);
        panelFormulario.add(txtEmail, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 5;
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

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);

        panelPrincipal.add(panelFormulario, BorderLayout.WEST);

        // Tabla de entrenadores (Center)
        String[] columnas = {"Nombre", "Apellido", "Especialidad", "Teléfono", "Email", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
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
                    cargarFormulario(filaSeleccionada);
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
            // Crear
            Entrenador e = new Entrenador(nombre, apellido, especialidad, telefono, email, activo);
            entrenadores.add(e);
            modeloTabla.addRow(new Object[]{nombre, apellido, especialidad, telefono, email, activo});
        } else {
            // Actualizar
            Entrenador e = entrenadores.get(filaSeleccionada);
            e.setNombre(nombre); e.setApellido(apellido);
            e.setEspecialidad(especialidad); e.setTelefono(telefono);
            e.setEmail(email); e.setActivo(activo);

            modeloTabla.setValueAt(nombre, filaSeleccionada, 0);
            modeloTabla.setValueAt(apellido, filaSeleccionada, 1);
            modeloTabla.setValueAt(especialidad, filaSeleccionada, 2);
            modeloTabla.setValueAt(telefono, filaSeleccionada, 3);
            modeloTabla.setValueAt(email, filaSeleccionada, 4);
            modeloTabla.setValueAt(activo, filaSeleccionada, 5);
        }
        limpiarFormulario();
    }

    private void eliminarSeleccionado() {
        if (filaSeleccionada != -1) {
            int resp = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar entrenador seleccionado?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                entrenadores.remove(filaSeleccionada);
                modeloTabla.removeRow(filaSeleccionada);
                limpiarFormulario();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador para eliminar.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarFormulario(int fila) {
        Entrenador e = entrenadores.get(fila);
        txtNombre.setText(e.getNombre());
        txtApellido.setText(e.getApellido());
        comboEspecialidad.setSelectedItem(e.getEspecialidad());
        txtTelefono.setText(e.getTelefono());
        txtEmail.setText(e.getEmail());
        chkEstado.setSelected(e.isActivo());
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        comboEspecialidad.setSelectedIndex(0);
        txtTelefono.setText("");
        txtEmail.setText("");
        chkEstado.setSelected(false);
        tablaEntrenadores.clearSelection();
        filaSeleccionada = -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormularioEntrenador().setVisible(true));
    }

    // Clase interna para modelar un entrenador
    private static class Entrenador {
        private String nombre, apellido, especialidad, telefono, email;
        private boolean activo;
        public Entrenador(String n, String a, String esp, String t, String e, boolean ac) {
            nombre=n; apellido=a; especialidad=esp; telefono=t; email=e; activo=ac;
        }
        public String getNombre() { return nombre; }
        public void setNombre(String n) { nombre = n; }
        public String getApellido() { return apellido; }
        public void setApellido(String a) { apellido = a; }
        public String getEspecialidad() { return especialidad; }
        public void setEspecialidad(String s) { especialidad = s; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String t) { telefono = t; }
        public String getEmail() { return email; }
        public void setEmail(String e) { email = e; }
        public boolean isActivo() { return activo; }
        public void setActivo(boolean a) { activo = a; }
    }
}
