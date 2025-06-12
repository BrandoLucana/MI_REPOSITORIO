package pe.edu.vallegrande.view;

import pe.edu.vallegrande.model.Estudiante;
import pe.edu.vallegrande.service.EstudianteService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class FormularioEstudiante extends JPanel {

    private final JTextField campoNombre;
    private final JTextField campoApellido;
    private final JTextField campoEdad;
    private final JComboBox<String> comboTipoDocumento;
    private final JTextField campoDocumento;
    private final JTextField campoCorreo;
    private final JTextField campoCelular;
    private final JComboBox<String> comboCategoria;
    private final JRadioButton radioMasculino;
    private final JRadioButton radioFemenino;
    private final ButtonGroup grupoGenero;
    private final DefaultTableModel modeloTabla;
    private final EstudianteService estudianteService;
    private final Pattern celularPattern = Pattern.compile("^9[0-9]{8}$");
    private final JPanel panelFormulario;

    public FormularioEstudiante() {
        // Configuración del fondo y texto
        setBackground(new Color(200, 50, 50));
        setForeground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        // Panel para los campos del formulario
        panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(new Color(200, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicialización de componentes
        campoNombre = new JTextField(25);
        campoApellido = new JTextField(25);
        campoEdad = new JTextField(5);
        comboTipoDocumento = new JComboBox<>(new String[]{"DNI", "Carné de Extranjería"});
        campoDocumento = new JTextField(20);
        campoCorreo = new JTextField(30);
        campoCelular = new JTextField(12);
        comboCategoria = new JComboBox<>(new String[]{"Infantil", "Juvenil", "Adulto"});

        // Configurar colores de los componentes
        configurarComponentesBlancos(campoNombre, campoApellido, campoEdad, campoDocumento,
                campoCorreo, campoCelular, comboTipoDocumento, comboCategoria);

        // Configurar radio buttons
        radioMasculino = new JRadioButton("Masculino");
        radioFemenino = new JRadioButton("Femenino");
        radioMasculino.setForeground(Color.WHITE);
        radioFemenino.setForeground(Color.WHITE);
        radioMasculino.setOpaque(false);
        radioFemenino.setOpaque(false);

        grupoGenero = new ButtonGroup();
        grupoGenero.add(radioMasculino);
        grupoGenero.add(radioFemenino);

        JButton botonGuardar = new JButton("Guardar");
        JButton botonVolver = new JButton("Volver al Menú");

        // Configurar botones
        botonGuardar.setBackground(Color.WHITE);
        botonVolver.setBackground(Color.WHITE);
        botonGuardar.setForeground(new Color(200, 50, 50));
        botonVolver.setForeground(new Color(200, 50, 50));

        // Configurar validación de documento
        comboTipoDocumento.addActionListener(e -> actualizarValidacionDocumento());
        campoDocumento.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String tipo = (String)comboTipoDocumento.getSelectedItem();
                if (tipo.equals("DNI") && campoDocumento.getText().length() >= 8) {
                    e.consume();
                } else if (tipo.equals("Carné de Extranjería") && campoDocumento.getText().length() >= 12) {
                    e.consume();
                }
            }
        });

        // Configurar validación de celular
        campoCelular.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (campoCelular.getText().length() >= 9 || !Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });

        // Organización de componentes en el formulario
        int y = 0;
        agregarCampo("Nombre:", campoNombre, gbc, y++);
        agregarCampo("Apellido:", campoApellido, gbc, y++);
        agregarCampo("Edad:", campoEdad, gbc, y++);

        // Campo de documento con tipo
        gbc.gridx = 0;
        gbc.gridy = y;
        JLabel lblTipoDoc = new JLabel("Tipo Documento:");
        lblTipoDoc.setForeground(Color.WHITE);
        panelFormulario.add(lblTipoDoc, gbc);
        gbc.gridx = 1;
        panelFormulario.add(comboTipoDocumento, gbc);
        y++;

        agregarCampo("N° Documento:", campoDocumento, gbc, y++);
        agregarCampo("Correo electrónico:", campoCorreo, gbc, y++);
        agregarCampo("Celular:", campoCelular, gbc, y++);
        agregarCampo("Categoría:", comboCategoria, gbc, y++);

        // Campo de género
        gbc.gridx = 0;
        gbc.gridy = y;
        JLabel lblGenero = new JLabel("Género:");
        lblGenero.setForeground(Color.WHITE);
        panelFormulario.add(lblGenero, gbc);
        gbc.gridx = 1;
        JPanel panelGenero = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelGenero.setOpaque(false);
        panelGenero.add(radioMasculino);
        panelGenero.add(radioFemenino);
        panelFormulario.add(panelGenero, gbc);
        y++;

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        panelBotones.add(botonGuardar);
        panelBotones.add(botonVolver);
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);

        // Configurar la tabla
        modeloTabla = new DefaultTableModel();
        JTable tablaEstudiantes = new JTable(modeloTabla) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Personalizar apariencia de la tabla
        tablaEstudiantes.setBackground(new Color(200, 50, 50));
        tablaEstudiantes.setForeground(Color.WHITE);
        tablaEstudiantes.setGridColor(Color.LIGHT_GRAY);
        tablaEstudiantes.setSelectionBackground(new Color(180, 45, 45));
        tablaEstudiantes.setSelectionForeground(Color.WHITE);
        tablaEstudiantes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaEstudiantes.setRowHeight(25);
        tablaEstudiantes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Personalizar el header de la tabla
        JTableHeader header = tablaEstudiantes.getTableHeader();
        header.setBackground(new Color(180, 45, 45));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Configurar columnas de la tabla
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Edad");
        modeloTabla.addColumn("Tipo Doc");
        modeloTabla.addColumn("N° Documento");
        modeloTabla.addColumn("Correo");
        modeloTabla.addColumn("Celular");
        modeloTabla.addColumn("Categoría");
        modeloTabla.addColumn("Género");

        // Ajustar anchos de columnas (modificado para mejor visualización)
        tablaEstudiantes.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        tablaEstudiantes.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre
        tablaEstudiantes.getColumnModel().getColumn(2).setPreferredWidth(150);  // Apellido
        tablaEstudiantes.getColumnModel().getColumn(3).setPreferredWidth(50);   // Edad
        tablaEstudiantes.getColumnModel().getColumn(4).setPreferredWidth(120);  // Tipo Doc
        tablaEstudiantes.getColumnModel().getColumn(5).setPreferredWidth(150);  // N° Documento
        tablaEstudiantes.getColumnModel().getColumn(6).setPreferredWidth(250);  // Correo
        tablaEstudiantes.getColumnModel().getColumn(7).setPreferredWidth(100);  // Celular
        tablaEstudiantes.getColumnModel().getColumn(8).setPreferredWidth(100);  // Categoría
        tablaEstudiantes.getColumnModel().getColumn(9).setPreferredWidth(100);  // Género

        // Configurar scroll pane para la tabla (con tamaño aumentado)
        JScrollPane scrollPaneTabla = new JScrollPane(tablaEstudiantes);
        scrollPaneTabla.setPreferredSize(new Dimension(1200, 400));
        scrollPaneTabla.setMaximumSize(new Dimension(1200, 400));

        // Panel contenedor para la tabla (modificado para mejor centrado)
        JPanel panelTabla = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelTabla.setBackground(new Color(200, 50, 50));
        panelTabla.add(scrollPaneTabla);

        // Configurar eventos
        botonGuardar.addActionListener(this::guardarDatos);
        botonVolver.addActionListener(e -> limpiarFormulario());

        // Organizar componentes principales
        add(panelFormulario, BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);

        estudianteService = new EstudianteService();
        actualizarTablaEstudiantes();
    }

    private void configurarComponentesBlancos(JComponent... componentes) {
        for (JComponent comp : componentes) {
            comp.setBackground(Color.WHITE);
            comp.setForeground(Color.BLACK);
            if (comp instanceof JTextField) {
                ((JTextField)comp).setCaretColor(Color.BLACK);
            }
            if (comp instanceof JComboBox) {
                ((JComboBox<?>)comp).setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                        return this;
                    }
                });
            }
        }
    }

    private void agregarCampo(String etiqueta, Component campo, GridBagConstraints gbc, int y) {
        JLabel label = new JLabel(etiqueta);
        label.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        panelFormulario.add(label, gbc);
        gbc.gridx = 1;
        panelFormulario.add(campo, gbc);
    }

    private void actualizarValidacionDocumento() {
        String tipo = (String)comboTipoDocumento.getSelectedItem();
        if (tipo.equals("DNI")) {
            campoDocumento.setText("");
            campoDocumento.setToolTipText("Ingrese 8 dígitos numéricos");
        } else {
            campoDocumento.setText("");
            campoDocumento.setToolTipText("Ingrese hasta 12 caracteres alfanuméricos");
        }
    }

    private void guardarDatos(ActionEvent e) {
        try {
            if (!validarDatos()) {
                return;
            }

            Estudiante estudiante = getEstudiante();
            estudianteService.insertarEstudiante(estudiante);
            mostrarMensaje("Estudiante registrado exitosamente.");
            limpiarFormulario();
            actualizarTablaEstudiantes();
        } catch (NumberFormatException ex) {
            mostrarError("Edad inválida. Debe ser un número.");
        } catch (Exception ex) {
            mostrarError("Error al registrar: " + ex.getMessage());
        }
    }

    private boolean validarDatos() {
        try {
            Integer.parseInt(campoEdad.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("La edad debe ser un número válido.");
            return false;
        }

        String tipoDocumento = (String)comboTipoDocumento.getSelectedItem();
        String documento = campoDocumento.getText().trim();

        if (tipoDocumento.equals("DNI")) {
            if (documento.length() != 8 || !documento.matches("\\d+")) {
                mostrarError("El DNI debe tener exactamente 8 dígitos numéricos.");
                return false;
            }
        } else {
            if (documento.length() > 12 || documento.isEmpty()) {
                mostrarError("El Carné de Extranjería debe tener hasta 12 caracteres.");
                return false;
            }
        }

        String celular = campoCelular.getText().trim();
        if (celular.length() != 9 || !celularPattern.matcher(celular).matches()) {
            mostrarError("El celular debe tener 9 dígitos y comenzar con 9.");
            return false;
        }

        return true;
    }

    private Estudiante getEstudiante() {
        String nombre = campoNombre.getText().trim();
        String apellido = campoApellido.getText().trim();
        int edad = Integer.parseInt(campoEdad.getText().trim());
        String tipoDocumento = (String)comboTipoDocumento.getSelectedItem();
        String documento = campoDocumento.getText().trim();
        String correo = campoCorreo.getText().trim();
        String celular = campoCelular.getText().trim();
        String categoria = (String)comboCategoria.getSelectedItem();
        String genero = radioMasculino.isSelected() ? "Masculino" : radioFemenino.isSelected() ? "Femenino" : "No especificado";

        Estudiante estudiante = new Estudiante(nombre, apellido, edad, categoria);
        estudiante.setTipoDocumento(tipoDocumento);
        estudiante.setNumeroDocumento(documento);
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
        comboTipoDocumento.setSelectedIndex(0);
        campoDocumento.setText("");
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
                        estudiante.getTipoDocumento(),
                        estudiante.getNumeroDocumento(),
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