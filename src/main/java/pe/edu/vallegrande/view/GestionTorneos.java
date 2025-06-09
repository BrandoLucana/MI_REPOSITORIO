package pe.edu.vallegrande.view;

import pe.edu.vallegrande.model.Torneo;
import pe.edu.vallegrande.model.TorneoDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

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
    private final TorneoDAO torneoDAO;

    public GestionTorneos() {
        torneoDAO = new TorneoDAO();

        // Configuración del panel principal con GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo: Nombre del Torneo
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nombre del Torneo:"), gbc);
        txtNombre = new JTextField(15);
        gbc.gridx = 1;
        add(txtNombre, gbc);

        // Campo: Fecha
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(new JLabel("Fecha:"), gbc);
        spFecha = new JSpinner(new SpinnerDateModel());
        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "dd/MM/yyyy"));
        gbc.gridx = 3;
        add(spFecha, gbc);

        // Campo: Lugar
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Lugar:"), gbc);
        txtLugar = new JTextField(15);
        gbc.gridx = 1;
        add(txtLugar, gbc);

        // Campo: Nivel
        gbc.gridx = 2;
        gbc.gridy = 1;
        add(new JLabel("Nivel:"), gbc);
        cbNivel = new JComboBox<>(new String[]{"Aficionado", "Intermedio", "Avanzado"});
        gbc.gridx = 3;
        add(cbNivel, gbc);

        // Campo: Descripción
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        add(txtDescripcion, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Campo: Estado
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Estado:"), gbc);
        cbEstado = new JComboBox<>(new String[]{"Pendiente", "En Curso", "Finalizado"});
        gbc.gridx = 1;
        add(cbEstado, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnAgregar = new JButton("Registrar");
        btnAgregar.setBackground(Color.RED);
        btnAgregar.setForeground(Color.WHITE);
        JButton btnModificar = new JButton("Modificar");
        btnModificar.setBackground(Color.RED);
        btnModificar.setForeground(Color.WHITE);
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(Color.RED);
        btnEliminar.setForeground(Color.WHITE);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        add(panelBotones, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Fecha", "Lugar", "Nivel", "Descripción", "Estado"
        }, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);

        // Cargar torneos existentes
        cargarTorneos();

        // Acciones de los botones
        btnAgregar.addActionListener(e -> {
            String nombre = txtNombre.getText();
            String lugar = txtLugar.getText();
            String descripcion = txtDescripcion.getText();
            String nivel = Objects.requireNonNull(cbNivel.getSelectedItem()).toString();
            String estado = Objects.requireNonNull(cbEstado.getSelectedItem()).toString();
            java.util.Date fecha = (java.util.Date) spFecha.getValue();

            if (!nombre.isEmpty() && !lugar.isEmpty()) {
                Torneo nuevoTorneo = new Torneo(0, nombre, fecha, lugar, nivel, descripcion, estado);
                try {
                    torneoDAO.addTorneo(nuevoTorneo);
                    cargarTorneos();
                    limpiarCampos();
                } catch (java.sql.SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error al agregar torneo", ex);
                    JOptionPane.showMessageDialog(null, "Error al registrar el torneo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Completa al menos el nombre y lugar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnModificar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                int id = (int) tabla.getValueAt(fila, 0);
                String nombre = txtNombre.getText();
                String lugar = txtLugar.getText();
                String descripcion = txtDescripcion.getText();
                String nivel = Objects.requireNonNull(cbNivel.getSelectedItem()).toString();
                String estado = Objects.requireNonNull(cbEstado.getSelectedItem()).toString();
                java.util.Date fecha = (java.util.Date) spFecha.getValue();

                Torneo torneoModificado = new Torneo(id, nombre, fecha, lugar, nivel, descripcion, estado);
                try {
                    torneoDAO.updateTorneo(torneoModificado);
                    cargarTorneos();
                    limpiarCampos();
                } catch (java.sql.SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error al modificar torneo", ex);
                    JOptionPane.showMessageDialog(null, "Error al modificar el torneo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                int id = (int) tabla.getValueAt(fila, 0);
                try {
                    torneoDAO.deleteTorneo(id);
                    cargarTorneos();
                } catch (java.sql.SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error al eliminar torneo", ex);
                    JOptionPane.showMessageDialog(null, "Error al eliminar el torneo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                    try {
                        java.util.Date fecha = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tabla.getValueAt(fila, 2).toString());
                        spFecha.setValue(fecha);
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error al parsear fecha", ex);
                        JOptionPane.showMessageDialog(null, "Error al cargar la fecha.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    txtLugar.setText(tabla.getValueAt(fila, 3).toString());
                    cbNivel.setSelectedItem(tabla.getValueAt(fila, 4).toString());
                    txtDescripcion.setText(tabla.getValueAt(fila, 5).toString());
                    cbEstado.setSelectedItem(tabla.getValueAt(fila, 6).toString());
                }
            }
        });
    }

    private void cargarTorneos() {
        modeloTabla.setRowCount(0);
        try {
            List<Torneo> torneos = torneoDAO.getAllTorneos();
            for (Torneo torneo : torneos) {
                modeloTabla.addRow(new Object[]{
                        torneo.getId(), torneo.getNombre(), torneo.getFecha(), torneo.getLugar(),
                        torneo.getNivel(), torneo.getDescripcion(), torneo.getEstado()
                });
            }
        } catch (java.sql.SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al cargar torneos", ex);
            JOptionPane.showMessageDialog(null, "Error al cargar los torneos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtLugar.setText("");
        txtDescripcion.setText("");
        cbNivel.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        spFecha.setValue(new java.util.Date());
    }
}