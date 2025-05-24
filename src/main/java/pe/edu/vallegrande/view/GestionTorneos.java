package pe.edu.vallegrande.view;

import pe.edu.vallegrande.model.Torneo;
import pe.edu.vallegrande.model.TorneoDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GestionTorneos extends JFrame {
    private JTextField txtNombre, txtLugar, txtDescripcion;
    private JComboBox<String> cbNivel, cbEstado;
    private JSpinner spFecha;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TorneoDAO torneoDAO;

    public GestionTorneos() {
        torneoDAO = new TorneoDAO();

        setTitle("Gestión de Torneos de Vóley");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblNombre = new JLabel("Nombre del Torneo:");
        lblNombre.setBounds(20, 20, 150, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(170, 20, 200, 25);
        add(txtNombre);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setBounds(400, 20, 100, 25);
        add(lblFecha);

        spFecha = new JSpinner(new SpinnerDateModel());
        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "dd/MM/yyyy"));
        spFecha.setBounds(450, 20, 150, 25);
        add(spFecha);

        JLabel lblLugar = new JLabel("Lugar:");
        lblLugar.setBounds(20, 60, 150, 25);
        add(lblLugar);

        txtLugar = new JTextField();
        txtLugar.setBounds(170, 60, 200, 25);
        add(txtLugar);

        JLabel lblNivel = new JLabel("Nivel:");
        lblNivel.setBounds(400, 60, 100, 25);
        add(lblNivel);

        cbNivel = new JComboBox<>(new String[]{"Aficionado", "Intermedio", "Avanzado"});
        cbNivel.setBounds(450, 60, 150, 25);
        add(cbNivel);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setBounds(20, 100, 150, 25);
        add(lblDescripcion);

        txtDescripcion = new JTextField();
        txtDescripcion.setBounds(170, 100, 430, 25);
        add(txtDescripcion);

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setBounds(20, 140, 150, 25);
        add(lblEstado);

        cbEstado = new JComboBox<>(new String[]{"Pendiente", "En Curso", "Finalizado"});
        cbEstado.setBounds(170, 140, 200, 25);
        add(cbEstado);

        // Botones
        JButton btnAgregar = new JButton("Registrar");
        btnAgregar.setBounds(20, 180, 120, 30);
        btnAgregar.setBackground(Color.RED);
        btnAgregar.setForeground(Color.WHITE);
        add(btnAgregar);

        JButton btnModificar = new JButton("Modificar");
        btnModificar.setBounds(160, 180, 120, 30);
        btnModificar.setBackground(Color.RED);
        btnModificar.setForeground(Color.WHITE);
        add(btnModificar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(300, 180, 120, 30);
        btnEliminar.setBackground(Color.RED);
        btnEliminar.setForeground(Color.WHITE);
        add(btnEliminar);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Fecha", "Lugar", "Nivel", "Descripción", "Estado"
        }, 0);

        tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBounds(20, 230, 740, 200);
        add(scrollPane);

        // Cargar torneos existentes
        cargarTorneos();

        // Acciones de los botones
        btnAgregar.addActionListener(e -> {
            String nombre = txtNombre.getText();
            String lugar = txtLugar.getText();
            String descripcion = txtDescripcion.getText();
            String nivel = cbNivel.getSelectedItem().toString();
            String estado = cbEstado.getSelectedItem().toString();
            java.util.Date fecha = (java.util.Date) spFecha.getValue();

            if (!nombre.isEmpty() && !lugar.isEmpty()) {
                Torneo nuevoTorneo = new Torneo(0, nombre, fecha, lugar, nivel, descripcion, estado);
                torneoDAO.addTorneo(nuevoTorneo);
                cargarTorneos();  // Refrescar la tabla con los nuevos datos
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(null, "Completa al menos el nombre y lugar.");
            }
        });

        btnModificar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                int id = (int) tabla.getValueAt(fila, 0);
                String nombre = txtNombre.getText();
                String lugar = txtLugar.getText();
                String descripcion = txtDescripcion.getText();
                String nivel = cbNivel.getSelectedItem().toString();
                String estado = cbEstado.getSelectedItem().toString();
                java.util.Date fecha = (java.util.Date) spFecha.getValue();

                Torneo torneoModificado = new Torneo(id, nombre, fecha, lugar, nivel, descripcion, estado);
                torneoDAO.updateTorneo(torneoModificado);
                cargarTorneos();  // Refrescar la tabla con los nuevos datos
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para modificar.");
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                int id = (int) tabla.getValueAt(fila, 0);
                torneoDAO.deleteTorneo(id);
                cargarTorneos();  // Refrescar la tabla con los nuevos datos
            } else {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar.");
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
                        ex.printStackTrace();
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
        modeloTabla.setRowCount(0); // Limpiar la tabla antes de cargar nuevos datos
        List<Torneo> torneos = torneoDAO.getAllTorneos();
        for (Torneo torneo : torneos) {
            modeloTabla.addRow(new Object[]{
                    torneo.getId(), torneo.getNombre(), torneo.getFecha(), torneo.getLugar(),
                    torneo.getNivel(), torneo.getDescripcion(), torneo.getEstado()
            });
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionTorneos().setVisible(true));
    }
}
