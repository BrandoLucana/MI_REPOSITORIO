package pe.edu.vallegrande;

import pe.edu.vallegrande.modelo.Estudiante;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FormularioEstudiante extends JFrame {
    private JTextField campoNombre, campoApellido, campoEdad, campoDni, campoCorreo, campoCelular;
    private JComboBox<String> comboCategoria;
    private JRadioButton radioMasculino, radioFemenino;
    private JTextArea resultado;
    private ButtonGroup grupoGenero;

    public FormularioEstudiante() {
        setTitle("Inscripción Academia de Vóley");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Solo cierra esta ventana

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoNombre = new JTextField();
        campoApellido = new JTextField();
        campoEdad = new JTextField();
        campoDni = new JTextField();
        campoCorreo = new JTextField();
        campoCelular = new JTextField();
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

        gbc.gridy = y;
        add(new JScrollPane(resultado), gbc);

        botonGuardar.addActionListener(this::registrar);
        botonVolver.addActionListener(e -> {
            dispose();
            new pe.edu.vallegrande.MenuPrincipal().setVisible(true);
        });
    }

    private void agregarCampo(String etiqueta, Component campo, GridBagConstraints gbc, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        add(campo, gbc);
    }

    private void registrar(ActionEvent e) {
        try {
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

            resultado.setText("Registrado:\n" + estudiante.getResumen());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Edad inválida. Debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}