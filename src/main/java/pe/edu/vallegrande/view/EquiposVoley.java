package pe.edu.vallegrande.view;

import pe.edu.vallegrande.controller.EquipoController;
import pe.edu.vallegrande.model.Equipo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import java.awt.*;

public class EquiposVoley extends JPanel {
    private final EquipoController controller = new EquipoController();

    private final JTextField txtCliente = new JTextField();
    private final JComboBox<String> cboProducto = new JComboBox<>(new String[]{"Balón de Voleibol", "Red de Voleibol", "Rodilleras (par)", "Zapatillas de Voleibol"});
    private final JTextField txtCantidad = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtTotal = new JTextField();
    private final JTable tabla = new JTable();

    private final double[] precios = {25.00, 40.00, 15.00, 60.00};

    public EquiposVoley() {
        Color rojo = new Color(200, 50, 50);
        setLayout(new BorderLayout());
        setBackground(rojo);

        JPanel formulario = new JPanel(new GridLayout(6, 2, 10, 10));
        formulario.setBackground(rojo);
        formulario.setBorder(new EmptyBorder(15, 15, 15, 15)); // elimina borde blanco y da margen interno

        Font fontNegrita = new Font("Arial", Font.BOLD, 12); // fuente en negrita

        // Crear labels en negrita y color blanco
        JLabel lblCliente = crearLabel("Cliente:", fontNegrita);
        JLabel lblProducto = crearLabel("Producto:", fontNegrita);
        JLabel lblCantidad = crearLabel("Cantidad:", fontNegrita);
        JLabel lblPrecio = crearLabel("Precio Unitario:", fontNegrita);
        JLabel lblTotal = crearLabel("Total:", fontNegrita);

        formulario.add(lblCliente); formulario.add(txtCliente);
        formulario.add(lblProducto); formulario.add(cboProducto);
        formulario.add(lblCantidad); formulario.add(txtCantidad);
        formulario.add(lblPrecio); formulario.add(txtPrecio);
        formulario.add(lblTotal); formulario.add(txtTotal);

        JButton btnCalcular = new JButton("Calcular");
        JButton btnGuardar = new JButton("Guardar");
        formulario.add(btnCalcular); formulario.add(btnGuardar);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        txtPrecio.setEditable(false);
        txtTotal.setEditable(false);

        // Limitar campo "Cantidad" a 2 caracteres numéricos
        ((AbstractDocument) txtCantidad.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= 2 && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String string, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                if ((fb.getDocument().getLength() - length + string.length()) <= 2 && string.matches("\\d*")) {
                    super.replace(fb, offset, length, string, attrs);
                }
            }
        });

        cboProducto.addActionListener(e -> txtPrecio.setText(String.valueOf(precios[cboProducto.getSelectedIndex()])));
        cboProducto.setSelectedIndex(0);

        btnCalcular.addActionListener(e -> {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText());
                double precio = precios[cboProducto.getSelectedIndex()];
                txtPrecio.setText(String.valueOf(precio));
                txtTotal.setText(String.valueOf(precio * cantidad));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al calcular total.");
            }
        });

        btnGuardar.addActionListener(e -> {
            try {
                Equipo equipo = new Equipo();
                equipo.setNombreCliente(txtCliente.getText());
                equipo.setProducto((String) cboProducto.getSelectedItem());
                equipo.setCantidad(Integer.parseInt(txtCantidad.getText()));
                equipo.setPrecioUnitario(Double.parseDouble(txtPrecio.getText()));
                equipo.setTotalPago(Double.parseDouble(txtTotal.getText()));
                controller.registrar(equipo);
                JOptionPane.showMessageDialog(this, "Equipo registrado.");
                listarDatos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar.");
            }
        });

        listarDatos();
    }

    private JLabel crearLabel(String texto, Font font) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(font);
        return label;
    }

    private void listarDatos() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Cliente", "Producto", "Cantidad", "Precio", "Total"});
        for (Equipo e : controller.listar()) {
            model.addRow(new Object[]{
                    e.getId(), e.getNombreCliente(), e.getProducto(),
                    e.getCantidad(), e.getPrecioUnitario(), e.getTotalPago()
            });
        }
        tabla.setModel(model);
    }
}
