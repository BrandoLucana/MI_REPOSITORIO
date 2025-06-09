package pe.edu.vallegrande.view;

import pe.edu.vallegrande.model.Estudiante;
import pe.edu.vallegrande.service.EstudianteService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class FormularioEstudiante extends JPanel {

    private final JTextField campoNombre;
    private final JTextField campoApellido;
    private final JTextField campoEdad;
    private final JTextField campoDni;
    private final JTextField campoCorreo;
    private final JTextField campoCelular;
    private final JComboBox<String> comboCategoria;
    private final JRadioButton radioMasculino;
    private final JRadioButton radioFemenino;
    private final JTextArea resultado;
    private final ButtonGroup grupoGenero;
    private final DefaultTableModel modeloTabla;
    private final EstudianteService estudianteService;

    public FormularioEstudiante() {
        // Configuración del panel principal con GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoNombre = new JTextField(15);
        campoApellido = new JTextField(15);
        campoEdad = new JTextField(5);
        campoDni = new JTextField(10);
        campoCorreo = new JTextField(15);
        campoCelular = new JTextField(10);
        comboCategoria = new JComboBox<>(new String[]{"Infantil", "Juvenil", "Adulto"});

        radioMasculino = new JRadioButton("Masculino");
        radioFemenino = new JRadioButton("Femenino");
        grupoGenero = new ButtonGroup();
        grupoGenero.add(radioMasculino);
        grupoGenero.add(radioFemenino);

        JButton botonGuardar = new JButton("Guardar");
        JButton botonVolver = new JButton("Volver al Menú");

        resultado = new JTextArea(6, 30);
        resultado.setEditable(false);

        // Inicializar la tabla y el modelo
        modeloTabla = new DefaultTableModel();
        JTable tablaEstudiantes = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaEstudiantes);
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Edad");
        modeloTabla.addColumn("DNI");
        modeloTabla.addColumn("Correo");
        modeloTabla.addColumn("Celular");
        modeloTabla.addColumn("Categoría");
        modeloTabla.addColumn("Género");
        tablaEstudiantes.getColumnModel().getColumn(0).setPreferredWidth(30);

        int y = 0;
        agregarCampo("Nombre:", campoNombre, gbc, y++);
        agregarCampo("Apellido:", campoApellido, gbc, y++);
        agregarCampo("Edad:", campoEdad, gbc, y++);
        agregarCampo("DNI:", campoDni, gbc, y++);
        agregarCampo("Correo electrónico:", campoCorreo, gbc, y++);
        agregarCampo("Celular:", campoCelular, gbc, y++);
        agregarCampo("Categoría:", comboCategoria, gbc, y++);

        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel("Género:"), gbc);
        gbc.gridx = 1;
        JPanel panelGenero = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelGenero.add(radioMasculino);
        panelGenero.add(radioFemenino);
        add(panelGenero, gbc);
        y++;

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(botonGuardar);
        panelBotones.add(botonVolver);
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 2;
        add(panelBotones, gbc);

        gbc.gridy = y++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        add(scrollPane, gbc);

        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(new JScrollPane(resultado), gbc);

        botonGuardar.addActionListener(this::guardarDatos);
        botonVolver.addActionListener(e -> limpiarFormulario()); // Solo limpia el formulario, la navegación se manejará en LoginMenuApp

        estudianteService = new EstudianteService();
        actualizarTablaEstudiantes();
    }

    private void agregarCampo(String etiqueta, Component campo, GridBagConstraints gbc, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        add(campo, gbc);
    }

    private void guardarDatos(ActionEvent e) {
        try {
            Estudiante estudiante = getEstudiante();

            estudianteService.insertarEstudiante(estudiante);
            mostrarMensaje("Estudiante registrado exitosamente.");
            limpiarFormulario();
            actualizarTablaEstudiantes();
            resultado.setText("Registrado:\n" + estudiante.getResumen());
        } catch (NumberFormatException ex) {
            mostrarError("Edad inválida. Debe ser un número.");
        } catch (Exception ex) {
            mostrarError("Error al registrar: " + ex.getMessage());
        }
    }

    private Estudiante getEstudiante() {
        String nombre = campoNombre.getText().trim();
        String apellido = campoApellido.getText().trim();
        int edad = Integer.parseInt(campoEdad.getText().trim());
        String dni = campoDni.getText().trim();
        String correo = campoCorreo.getText().trim();
        String celular = campoCelular.getText().trim();
        String categoria = (String) comboCategoria.getSelectedItem();
        String genero = radioMasculino.isSelected() ? "Masculino" : radioFemenino.isSelected() ? "Femenino" : "No especificado";

        Estudiante estudiante = new Estudiante(nombre, apellido, edad, categoria);
        estudiante.setDni(dni);
        estudiante.setCorreo(correo);
        estudiante.setCelular(celular);
        estudiante.setGenero(genero);
        return estudiante;
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void limpiarFormulario() {
        campoNombre.setText("");
        campoApellido.setText("");
        campoEdad.setText("");
        campoDni.setText("");
        campoCorreo.setText("");
        campoCelular.setText("");
        comboCategoria.setSelectedIndex(0);
        grupoGenero.clearSelection();
    }

    public void actualizarTablaEstudiantes() {
        modeloTabla.setRowCount(0);
        try {
            List<Estudiante> estudiantes = estudianteService.obtenerTodosEstudiantes();
            for (Estudiante estudiante : estudiantes) {
                Object[] fila = {
                        estudiante.getId(),
                        estudiante.getNombre(),
                        estudiante.getApellido(),
                        estudiante.getEdad(),
                        estudiante.getDni(),
                        estudiante.getCorreo(),
                        estudiante.getCelular(),
                        estudiante.getCategoria(),
                        estudiante.getGenero()
                };
                modeloTabla.addRow(fila);
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar la tabla de estudiantes: " + e.getMessage());
        }
    }
}