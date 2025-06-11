package pe.edu.vallegrande.view;

import pe.edu.vallegrande.model.Torneo;
import pe.edu.vallegrande.model.TorneoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
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
    private final TorneoDAO torneoDAO;

    public GestionTorneos() {
        torneoDAO = new TorneoDAO();

        // Colores base
        Color fondoRojo = Color.decode("#B71C1C"); // Rojo fuerte
        Color fondoBoton = Color.decode("#D32F2F"); // Rojo botón
        Color textoBlanco = Color.WHITE;
        Color textoClaro = Color.decode("#F5F5F5");

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
        txtNombre = new JTextField(15);
        estilizarCampo(txtNombre, fuente);
        gbc.gridx = 1;
        add(txtNombre, gbc);

        // Campo: Fecha
        gbc.gridx = 2;
        JLabel lblFecha = new JLabel("Fecha:");
        estilizarLabel(lblFecha, textoBlanco, fuente);
        add(lblFecha, gbc);
        spFecha = new JSpinner(new SpinnerDateModel());
        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "dd/MM/yyyy"));
        estilizarCampo(spFecha, fuente);
        gbc.gridx = 3;
        add(spFecha, gbc);

        // Campo: Lugar
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblLugar = new JLabel("Lugar:");
        estilizarLabel(lblLugar, textoBlanco, fuente);
        add(lblLugar, gbc);
        txtLugar = new JTextField(15);
        estilizarCampo(txtLugar, fuente);
        gbc.gridx = 1;
        add(txtLugar, gbc);

        // Campo: Nivel
        gbc.gridx = 2;
        JLabel lblNivel = new JLabel("Nivel:");
        estilizarLabel(lblNivel, textoBlanco, fuente);
        add(lblNivel, gbc);
        cbNivel = new JComboBox<>(new String[]{"Aficionado", "Intermedio", "Avanzado"});
        estilizarCampo(cbNivel, fuente);
        gbc.gridx = 3;
        add(cbNivel, gbc);

        // Campo: Descripción
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDescripcion = new JLabel("Descripción:");
        estilizarLabel(lblDescripcion, textoBlanco, fuente);
        add(lblDescripcion, gbc);
        txtDescripcion = new JTextField(30);
        estilizarCampo(txtDescripcion, fuente);
        gbc.gridx = 1; gbc.gridwidth = 3;
        add(txtDescripcion, gbc);
        gbc.gridwidth = 1;

        // Campo: Estado
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblEstado = new JLabel("Estado:");
        estilizarLabel(lblEstado, textoBlanco, fuente);
        add(lblEstado, gbc);
        cbEstado = new JComboBox<>(new String[]{"Pendiente", "En Curso", "Finalizado"});
        estilizarCampo(cbEstado, fuente);
        gbc.gridx = 1;
        add(cbEstado, gbc);

        // Panel Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setBackground(fondoRojo);

        JButton btnAgregar = new JButton("Registrar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        // En el panel de botones
        for (JButton b : new JButton[]{btnAgregar, btnModificar, btnEliminar}) {
            b.setBackground(Color.WHITE);
            b.setForeground(Color.decode("#B71C1C")); // Texto rojo
            b.setFont(fuente);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Borde negro
            b.setPreferredSize(new Dimension(120, 35)); // Tamaño uniforme
            panelBotones.add(b);
        }


        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        add(panelBotones, gbc);
        gbc.gridwidth = 1;

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Fecha", "Lugar", "Nivel", "Descripción", "Estado"
        }, 0);
        tabla = new JTable(modeloTabla);
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
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                    try {
                        java.util.Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(tabla.getValueAt(fila, 2).toString());
                        spFecha.setValue(fecha);
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error al parsear fecha", ex);
                    }
                    txtLugar.setText(tabla.getValueAt(fila, 3).toString());
                    cbNivel.setSelectedItem(tabla.getValueAt(fila, 4).toString());
                    txtDescripcion.setText(tabla.getValueAt(fila, 5).toString());
                    cbEstado.setSelectedItem(tabla.getValueAt(fila, 6).toString());
                }
            }
        });
    }

    private void registrarTorneo() {
        String nombre = txtNombre.getText();
        String lugar = txtLugar.getText();
        String descripcion = txtDescripcion.getText();
        String nivel = Objects.requireNonNull(cbNivel.getSelectedItem()).toString();
        String estado = Objects.requireNonNull(cbEstado.getSelectedItem()).toString();
        java.util.Date fecha = (java.util.Date) spFecha.getValue();

        if (!nombre.isEmpty() && !lugar.isEmpty()) {
            Torneo nuevo = new Torneo(0, nombre, fecha, lugar, nivel, descripcion, estado);
            try {
                torneoDAO.addTorneo(nuevo);
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
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            int id = (int) tabla.getValueAt(fila, 0);
            Torneo modificado = new Torneo(
                    id,
                    txtNombre.getText(),
                    (java.util.Date) spFecha.getValue(),
                    txtLugar.getText(),
                    cbNivel.getSelectedItem().toString(),
                    txtDescripcion.getText(),
                    cbEstado.getSelectedItem().toString()
            );
            try {
                torneoDAO.updateTorneo(modificado);
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
            int id = (int) tabla.getValueAt(fila, 0);
            try {
                torneoDAO.deleteTorneo(id);
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
            List<Torneo> torneos = torneoDAO.getAllTorneos();
            for (Torneo t : torneos) {
                modeloTabla.addRow(new Object[]{
                        t.getId(), t.getNombre(), t.getFecha(), t.getLugar(),
                        t.getNivel(), t.getDescripcion(), t.getEstado()
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
        spFecha.setValue(new java.util.Date());
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
