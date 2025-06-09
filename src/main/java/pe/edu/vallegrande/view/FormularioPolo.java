package pe.edu.vallegrande.view;

import pe.edu.vallegrande.controller.PoloController;
import pe.edu.vallegrande.model.Polo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class FormularioPolo extends JPanel {
    private final PoloController controller = new PoloController();
    private final JTable tabla;
    private final DefaultTableModel modelo;

    private final JTextField txtNombre;
    private final JTextField txtNumero;
    private final JComboBox<String> cmbTalla;
    private final JComboBox<String> cmbDeporte;
    private final JCheckBox chkShort;
    private final JCheckBox chkMedias;

    private int idSeleccionado = -1;

    public FormularioPolo() {
        Color rosa = new Color(255, 204, 229);
        setBackground(rosa);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        panelFormulario.setBackground(rosa);
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Formulario para Uniforme Deportivo"));

        txtNombre = new JTextField();
        txtNumero = new JTextField();
        cmbTalla = new JComboBox<>(new String[]{"S", "M", "L", "XL"});
        cmbDeporte = new JComboBox<>(new String[]{"Vóley", "Fútbol", "Básquet"});
        chkShort = new JCheckBox("Incluye Short");
        chkShort.setBackground(rosa);
        chkMedias = new JCheckBox("Incluye Medias");
        chkMedias.setBackground(rosa);

        panelFormulario.add(new JLabel("Nombre en el Polo:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Número en el Polo:"));
        panelFormulario.add(txtNumero);
        panelFormulario.add(new JLabel("Talla:"));
        panelFormulario.add(cmbTalla);
        panelFormulario.add(new JLabel("Deporte:"));
        panelFormulario.add(cmbDeporte);
        panelFormulario.add(chkShort);
        panelFormulario.add(chkMedias);

        add(panelFormulario, BorderLayout.NORTH);

        // Tabla
        modelo = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Número", "Talla", "Deporte", "Short", "Medias"
        }, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(rosa);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnGuardar.addActionListener(e -> {
            Polo p = new Polo();
            p.setNombreEnPolo(txtNombre.getText());
            p.setNumeroEnPolo(Integer.parseInt(txtNumero.getText()));
            p.setTalla(Objects.requireNonNull(cmbTalla.getSelectedItem()).toString());
            p.setDeporte(Objects.requireNonNull(cmbDeporte.getSelectedItem()).toString());
            p.setIncluyeShort(chkShort.isSelected());
            p.setIncluyeMedias(chkMedias.isSelected());

            controller.registrar(p);
            limpiarFormulario();
            cargarTabla();
        });

        btnModificar.addActionListener(e -> {
            if (idSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un polo para modificar.");
                return;
            }
            Polo p = new Polo();
            p.setId(idSeleccionado);
            p.setNombreEnPolo(txtNombre.getText());
            p.setNumeroEnPolo(Integer.parseInt(txtNumero.getText()));
            p.setTalla(Objects.requireNonNull(cmbTalla.getSelectedItem()).toString());
            p.setDeporte(Objects.requireNonNull(cmbDeporte.getSelectedItem()).toString());
            p.setIncluyeShort(chkShort.isSelected());
            p.setIncluyeMedias(chkMedias.isSelected());

            controller.actualizar(p);
            limpiarFormulario();
            cargarTabla();
        });

        btnEliminar.addActionListener(e -> {
            if (idSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un polo para eliminar.");
                return;
            }
            controller.eliminar(idSeleccionado);
            limpiarFormulario();
            cargarTabla();
        });

        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    idSeleccionado = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
                    txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                    txtNumero.setText(tabla.getValueAt(fila, 2).toString());
                    cmbTalla.setSelectedItem(tabla.getValueAt(fila, 3).toString());
                    cmbDeporte.setSelectedItem(tabla.getValueAt(fila, 4).toString());
                    chkShort.setSelected(tabla.getValueAt(fila, 5).toString().equals("true"));
                    chkMedias.setSelected(tabla.getValueAt(fila, 6).toString().equals("true"));
                }
            }
        });

        cargarTabla();
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtNumero.setText("");
        cmbTalla.setSelectedIndex(0);
        cmbDeporte.setSelectedIndex(0);
        chkShort.setSelected(false);
        chkMedias.setSelected(false);
        idSeleccionado = -1;
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        for (Polo p : controller.listar()) {
            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getNombreEnPolo(),
                    p.getNumeroEnPolo(),
                    p.getTalla(),
                    p.getDeporte(),
                    p.isIncluyeShort(),
                    p.isIncluyeMedias()
            });
        }
    }
}