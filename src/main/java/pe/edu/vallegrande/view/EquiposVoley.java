package pe.edu.vallegrande.view;

import pe.edu.vallegrande.controller.EquipoController;
import pe.edu.vallegrande.model.Equipo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        setLayout(new BorderLayout());
        JPanel formulario = new JPanel(new GridLayout(6, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Formulario de Registro"));

        formulario.add(new JLabel("Cliente:")); formulario.add(txtCliente);
        formulario.add(new JLabel("Producto:")); formulario.add(cboProducto);
        formulario.add(new JLabel("Cantidad:")); formulario.add(txtCantidad);
        formulario.add(new JLabel("Precio Unitario:")); formulario.add(txtPrecio);
        formulario.add(new JLabel("Total:")); formulario.add(txtTotal);

        JButton btnCalcular = new JButton("Calcular");
        JButton btnGuardar = new JButton("Guardar");
        formulario.add(btnCalcular); formulario.add(btnGuardar);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        txtPrecio.setEditable(false);
        txtTotal.setEditable(false);

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
