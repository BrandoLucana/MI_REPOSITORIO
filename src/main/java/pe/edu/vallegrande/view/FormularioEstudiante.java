package pe.edu.vallegrande.view;

import pe.edu.vallegrande.model.Estudiante;
import pe.edu.vallegrande.dataBase.ConexionMySQL;
import pe.edu.vallegrande.service.EstudianteService; // Importar EstudianteService
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class FormularioEstudiante extends JFrame {
    private JTextField campoNombre, campoApellido, campoEdad, campoDni, campoCorreo, campoCelular;
    private JComboBox<String> comboCategoria;
    private JRadioButton radioMasculino, radioFemenino;
    private JTextArea resultado;
    private ButtonGroup grupoGenero;
    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;
    private EstudianteService estudianteService; // Agregamos una instancia de EstudianteService


    public FormularioEstudiante() {
        setTitle("Inscripción Academia de Vóley");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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

        // Inicializar la tabla y el modelo
        modeloTabla = new DefaultTableModel();
        tablaEstudiantes = new JTable(modeloTabla);
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
        gbc.gridwidth = 2; // Para que el scrollPane ocupe 2 columnas
        gbc.fill = GridBagConstraints.BOTH; // Para que se expanda en ambas direcciones
        gbc.weighty = 1; // Para que el scrollPane se expanda verticalmente
        add(scrollPane, gbc);

        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        add(new JScrollPane(resultado), gbc);

        botonGuardar.addActionListener(this::guardarDatos);
        botonVolver.addActionListener(e -> {
            dispose();
            new MenuPrincipal().setVisible(true);
        });

        estudianteService = new EstudianteService(); // Inicializamos la instancia de EstudianteService
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

            estudianteService.insertarEstudiante(estudiante); // Pasa el objeto estudiante como argumento
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

    private void insertarEstudiante(Estudiante estudiante) throws SQLException {
        Connection conn = ConexionMySQL.conectar();
        if (conn != null) {
            try {
                String sql = "INSERT INTO estudiantes (nombre, apellido, edad, dni, correo, celular, categoria, genero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, estudiante.getNombre());
                pstmt.setString(2, estudiante.getApellido());
                pstmt.setInt(3, estudiante.getEdad());
                pstmt.setString(4, estudiante.getDni());
                pstmt.setString(5, estudiante.getCorreo());
                pstmt.setString(6, estudiante.getCelular());
                pstmt.setString(7, estudiante.getCategoria());
                pstmt.setString(8, estudiante.getGenero());
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Datos del estudiante guardados correctamente.");
                } else {
                    System.out.println("No se pudieron guardar los datos del estudiante.");
                }
            } catch (SQLException ex) {
                throw new SQLException("Error al guardar datos: " + ex.getMessage());
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.out.println("Error al cerrar la conexión: " + ex.getMessage());
                }
            }
        } else {
            System.out.println("No se pudo establecer la conexión.");
        }
    }

    // Nuevos métodos para mostrar mensajes, limpiar el formulario y actualizar la tabla
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

    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    public void cargarDatosFormulario(Estudiante estudiante) {
        campoNombre.setText(estudiante.getNombre());
        campoApellido.setText(estudiante.getApellido());
        campoEdad.setText(String.valueOf(estudiante.getEdad()));
        campoDni.setText(estudiante.getDni());
        campoCorreo.setText(estudiante.getCorreo());
        campoCelular.setText(estudiante.getCelular());
        comboCategoria.setSelectedItem(estudiante.getCategoria());
        if (estudiante.getGenero().equals("Masculíno")) {
            radioMasculino.setSelected(true);
        } else if (estudiante.getGenero().equals("Femenino")) {
            radioFemenino.setSelected(true);
        } else {
            grupoGenero.clearSelection();
        }
    }

    public void actualizarTablaEstudiantes() {
        modeloTabla.setRowCount(0);
        try {
            List<Estudiante> estudiantes = estudianteService.obtenerTodosEstudiantes(); // Aquí se usa el servicio
            for (Estudiante estudiante : estudiantes) {
                Object[] fila = {
                        estudiante.getId(),  // Asegúrate de que el estudiante tenga un ID
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
