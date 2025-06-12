package pe.edu.vallegrande.view;

import pe.edu.vallegrande.controller.PoloController;
import pe.edu.vallegrande.model.Polo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class FormularioPolo extends JPanel {
    private final PoloController controlador = new PoloController();
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
        Color rojo = new Color(200, 50, 50);
        setBackground(rojo);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        panelFormulario.setBackground(rojo);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        Font negrita = new Font("Arial", Font.BOLD, 12);

        JLabel lblNombre = new JLabel("Nombre en el Polo:");
        JLabel lblNumero = new JLabel("Número en el Polo:");
        JLabel lblTalla = new JLabel("Talla:");
        JLabel lblDeporte = new JLabel("Deporte:");

        lblNombre.setForeground(Color.WHITE);
        lblNumero.setForeground(Color.WHITE);
        lblTalla.setForeground(Color.WHITE);
        lblDeporte.setForeground(Color.WHITE);

        lblNombre.setFont(negrita);
        lblNumero.setFont(negrita);
        lblTalla.setFont(negrita);
        lblDeporte.setFont(negrita);

        txtNombre = new JTextField();
        txtNumero = new JTextField();

        limitarCaracteres(txtNombre, 20);
        limitarCaracteres(txtNumero, 2);

        cmbTalla = new JComboBox<>(new String[]{"S", "M", "L", "XL"});
        cmbDeporte = new JComboBox<>(new String[]{"Vóley", "Fútbol", "Básquet"});

        chkShort = new JCheckBox("Incluye Short");
        chkMedias = new JCheckBox("Incluye Medias");

        chkShort.setBackground(rojo);
        chkMedias.setBackground(rojo);
        chkShort.setForeground(Color.WHITE);
        chkMedias.setForeground(Color.WHITE);

        panelFormulario.add(lblNombre);
        panelFormulario.add(txtNombre);
        panelFormulario.add(lblNumero);
        panelFormulario.add(txtNumero);
        panelFormulario.add(lblTalla);
        panelFormulario.add(cmbTalla);
        panelFormulario.add(lblDeporte);
        panelFormulario.add(cmbDeporte);
        panelFormulario.add(chkShort);
        panelFormulario.add(chkMedias);

        add(panelFormulario, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Número", "Talla", "Deporte", "Short", "Medias"}, 0);
        tabla = new JTable(modelo);
        JScrollPane desplazamiento = new JScrollPane(tabla);
        add(desplazamiento, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(rojo);
        JButton btnGuardar = new JButton("Guardar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> {
            Polo p = new Polo();
            p.setNombreEnPolo(txtNombre.getText());
            p.setNumeroEnPolo(Integer.parseInt(txtNumero.getText()));
            p.setTalla(Objects.requireNonNull(cmbTalla.getSelectedItem()).toString());
            p.setDeporte(Objects.requireNonNull(cmbDeporte.getSelectedItem()).toString());
            p.setIncluyeShort(chkShort.isSelected());
            p.setIncluyeMedias(chkMedias.isSelected());
            controlador.registrar(p);
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
            controlador.actualizar(p);
            limpiarFormulario();
            cargarTabla();
        });

        btnEliminar.addActionListener(e -> {
            if (idSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un polo para eliminar.");
                return;
            }
            controlador.eliminar(idSeleccionado);
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
        for (Polo p : controlador.listar()) {
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

    private void limitarCaracteres(JTextField textField, int limite) {
        PlainDocument doc = (PlainDocument) textField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= limite) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if ((fb.getDocument().getLength() - length + (text != null ? text.length() : 0)) <= limite) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}


