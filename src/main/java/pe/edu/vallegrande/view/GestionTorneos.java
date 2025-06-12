package pe.edu.vallegrande.view;

import pe.edu.vallegrande.controller.TorneoController;
import pe.edu.vallegrande.model.Torneo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Importación añadida
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestionTorneos extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(GestionTorneos.class.getName());

    private final JTextField txtNombre;
    private final JTextField txtLugar;
    private final JTextField txtDescripcion;
    private final JComboBox<String> cbNivel;
    private final JComboBox<String> cbEstado;
    private final JSpinner spFecha;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final TorneoController torneoController;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public GestionTorneos() {
        // Inicialización de variables
        torneoController = new TorneoController();
        txtNombre = new JTextField(15);
        txtLugar = new JTextField(15);
        txtDescripcion = new JTextField(30);
        cbNivel = new JComboBox<>(new String[]{"Aficionado", "Intermedio", "Avanzado"});
        cbEstado = new JComboBox<>(new String[]{"Pendiente", "En Curso", "Finalizado"});
        SpinnerDateModel fechaModel = new SpinnerDateModel();
        fechaModel.setStart(new Date()); // No permite fechas anteriores a hoy
        spFecha = new JSpinner(fechaModel);
        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "dd/MM/yyyy"));
        modeloTabla = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Fecha", "Lugar", "Nivel", "Descripción", "Estado"
        }, 0);
        tabla = new JTable(modeloTabla);

        // Colores base
        Color fondoRojo = new Color(200, 50, 50);  // Rojo personalizado

        Color textoBlanco = Color.WHITE;

        setBackground(fondoRojo);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fuente = new Font("Segoe UI", Font.PLAIN, 14);

        // Campo: Nombre del Torneo
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre del Torneo:");
        estilizarLabel(lblNombre, textoBlanco, fuente);
        add(lblNombre, gbc);
        estilizarCampo(txtNombre, fuente);
        gbc.gridx = 1;
        add(txtNombre, gbc);

        // Campo: Fecha
        gbc.gridx = 2;
        JLabel lblFecha = new JLabel("Fecha:");
        estilizarLabel(lblFecha, textoBlanco, fuente);
        add(lblFecha, gbc);
        estilizarCampo(spFecha, fuente);
        gbc.gridx = 3;
        add(spFecha, gbc);

        // Campo: Lugar
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblLugar = new JLabel("Lugar:");
        estilizarLabel(lblLugar, textoBlanco, fuente);
        add(lblLugar, gbc);
        estilizarCampo(txtLugar, fuente);
        gbc.gridx = 1;
        add(txtLugar, gbc);

        // Campo: Nivel
        gbc.gridx = 2;
        JLabel lblNivel = new JLabel("Nivel:");
        estilizarLabel(lblNivel, textoBlanco, fuente);
        add(lblNivel, gbc);
        estilizarCampo(cbNivel, fuente);
        gbc.gridx = 3;
        add(cbNivel, gbc);

        // Campo: Descripción (Máximo 50 caracteres)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDescripcion = new JLabel("Descripción:");
        estilizarLabel(lblDescripcion, textoBlanco, fuente);
        add(lblDescripcion, gbc);
        estilizarCampo(txtDescripcion, fuente);

        // Filtro para limitar a 50 caracteres
        ((AbstractDocument) txtDescripcion.getDocument()).setDocumentFilter(new DocumentFilter() {
            private final int MAX_CHARS = 50;

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - MAX_CHARS;
                if (overLimit > 0) {
                    text = text.substring(0, text.length() - overLimit);
                }
                if (text.length() > 0) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        gbc.gridx = 1; gbc.gridwidth = 3;
        add(txtDescripcion, gbc);
        gbc.gridwidth = 1;

        // Campo: Estado
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblEstado = new JLabel("Estado:");
        estilizarLabel(lblEstado, textoBlanco, fuente);
        add(lblEstado, gbc);
        estilizarCampo(cbEstado, fuente);
        gbc.gridx = 1;
        add(cbEstado, gbc);

        // Panel Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setBackground(fondoRojo);

        JButton btnAgregar = new JButton("Registrar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        for (JButton b : new JButton[]{btnAgregar, btnModificar, btnEliminar}) {
            b.setBackground(Color.WHITE);
            b.setForeground(Color.decode("#B71C1C"));
            b.setFont(fuente);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            b.setPreferredSize(new Dimension(120, 35));
            panelBotones.add(b);
        }

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        add(panelBotones, gbc);
        gbc.gridwidth = 1;

        // Configuración de la tabla
        tabla.setFont(fuente);
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(Color.BLACK);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(fuente);
        tabla.getTableHeader().setBackground(Color.DARK_GRAY);
        tabla.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(tabla);
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);

        cargarTorneos();

        // Acciones
        btnAgregar.addActionListener(e -> registrarTorneo());
        btnModificar.addActionListener(e -> modificarTorneo());
        btnEliminar.addActionListener(e -> eliminarTorneo());

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtNombre.setText(String.valueOf(tabla.getValueAt(fila, 1)));
                    try {
                        String fechaStr = String.valueOf(tabla.getValueAt(fila, 2));
                        Date fecha = dateFormat.parse(fechaStr);
                        spFecha.setValue(fecha);
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, "Error al parsear fecha de la tabla", ex);
                        spFecha.setValue(new Date());
                    }
                    txtLugar.setText(String.valueOf(tabla.getValueAt(fila, 3)));
                    cbNivel.setSelectedItem(String.valueOf(tabla.getValueAt(fila, 4)));
                    txtDescripcion.setText(String.valueOf(tabla.getValueAt(fila, 5)));
                    cbEstado.setSelectedItem(String.valueOf(tabla.getValueAt(fila, 6)));
                }
            }
        });
    }

    private void registrarTorneo() {
        Date fechaSeleccionada = (Date) spFecha.getValue();
        Date today = new Date();
        dateFormat.setLenient(false);
        try {
            String fechaSelStr = dateFormat.format(fechaSeleccionada);
            String todayStr = dateFormat.format(today);
            Date fechaSelNormalizada = dateFormat.parse(fechaSelStr);
            Date todayNormalizada = dateFormat.parse(todayStr);
            if (fechaSelNormalizada.before(todayNormalizada)) {
                mostrarError("La fecha no puede ser anterior al día actual");
                return;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error al validar fecha", ex);
            mostrarError("Error al validar la fecha seleccionada");
            return;
        }

        String nombre = txtNombre.getText();
        String lugar = txtLugar.getText();
        String descripcion = txtDescripcion.getText();
        String nivel = Objects.requireNonNull(cbNivel.getSelectedItem()).toString();
        String estado = Objects.requireNonNull(cbEstado.getSelectedItem()).toString();
        Date fecha = (Date) spFecha.getValue();

        if (!nombre.isEmpty() && !lugar.isEmpty()) {
            try {
                torneoController.agregarTorneo(nombre, fecha, lugar, nivel, descripcion, estado);
                cargarTorneos();
                limpiarCampos();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error al agregar torneo", ex);
                mostrarError("Error al registrar el torneo.");
            }
        } else {
            mostrarError("Completa al menos el nombre y lugar.");
        }
    }

    private void modificarTorneo() {
        Date fechaSeleccionada = (Date) spFecha.getValue();
        Date today = new Date();
        dateFormat.setLenient(false);
        try {
            String fechaSelStr = dateFormat.format(fechaSeleccionada);
            String todayStr = dateFormat.format(today);
            Date fechaSelNormalizada = dateFormat.parse(fechaSelStr);
            Date todayNormalizada = dateFormat.parse(todayStr);
            if (fechaSelNormalizada.before(todayNormalizada)) {
                mostrarError("La fecha no puede ser anterior al día actual");
                return;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error al validar fecha", ex);
            mostrarError("Error al validar la fecha seleccionada");
            return;
        }

        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            int id = Integer.parseInt(String.valueOf(tabla.getValueAt(fila, 0)));
            try {
                torneoController.modificarTorneo(
                        id,
                        txtNombre.getText(),
                        (Date) spFecha.getValue(),
                        txtLugar.getText(),
                        cbNivel.getSelectedItem().toString(),
                        txtDescripcion.getText(),
                        cbEstado.getSelectedItem().toString()
                );
                cargarTorneos();
                limpiarCampos();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error al modificar torneo", ex);
                mostrarError("Error al modificar el torneo.");
            }
        } else {
            mostrarError("Selecciona una fila para modificar.");
        }
    }

    private void eliminarTorneo() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            int id = Integer.parseInt(String.valueOf(tabla.getValueAt(fila, 0)));
            try {
                torneoController.eliminarTorneo(id);
                cargarTorneos();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error al eliminar torneo", ex);
                mostrarError("Error al eliminar el torneo.");
            }
        } else {
            mostrarError("Selecciona una fila para eliminar.");
        }
    }

    private void cargarTorneos() {
        modeloTabla.setRowCount(0);
        try {
            List<Torneo> torneos = torneoController.obtenerTorneos();
            for (Torneo t : torneos) {
                modeloTabla.addRow(new Object[]{
                        t.getId(),
                        t.getNombre(),
                        dateFormat.format(t.getFecha()), // Formatear la fecha para la tabla
                        t.getLugar(),
                        t.getNivel(),
                        t.getDescripcion(),
                        t.getEstado()
                });
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al cargar torneos", ex);
            mostrarError("Error al cargar los torneos.");
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtLugar.setText("");
        txtDescripcion.setText("");
        cbNivel.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        spFecha.setValue(new Date());
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void estilizarLabel(JLabel label, Color color, Font font) {
        label.setForeground(color);
        label.setFont(font);
    }

    private void estilizarCampo(JComponent campo, Font font) {
        campo.setFont(font);
        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
    }
}
