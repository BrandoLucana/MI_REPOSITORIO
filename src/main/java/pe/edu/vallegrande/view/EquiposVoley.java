package pe.edu.vallegrande.view;

import javax.swing.*;
import java.awt.*;

public class EquiposVoley extends JPanel {
    private final JTextField customerNameField;
    private final JTextField quantityField;
    private final JTextField totalField;
    private final JComboBox<String> productComboBox;
    private final JTextField unitPriceField;

    private final double[] productPrices = {25.00, 40.00, 15.00, 60.00};

    public EquiposVoley() {
        setLayout(new GridLayout(8, 2, 10, 10));

        JLabel customerLabel = new JLabel("Nombre del Cliente:");
        customerNameField = new JTextField(20);
        customerNameField.setToolTipText("Ingrese su nombre completo");

        JLabel productLabel = new JLabel("Producto:");
        String[] products = {"Balón de Voleibol", "Red de Voleibol", "Rodilleras (par)", "Zapatillas de Voleibol"};
        productComboBox = new JComboBox<>(products);
        productComboBox.setToolTipText("Seleccione el producto que desea comprar");

        JLabel quantityLabel = new JLabel("Cantidad:");
        quantityField = new JTextField(10);
        quantityField.setToolTipText("Ingrese la cantidad deseada");

        JLabel unitPriceLabel = new JLabel("Precio Unitario ($):");
        unitPriceField = new JTextField(10);
        unitPriceField.setEditable(false);
        unitPriceField.setToolTipText("El precio se actualiza automáticamente según el producto");

        JLabel totalLabel = new JLabel("Total a Pagar ($):");
        totalField = new JTextField(10);
        totalField.setEditable(false);

        JButton calculateButton = new JButton("Calcular Total");
        JButton clearButton = new JButton("Limpiar");
        JButton exitButton = new JButton("Salir");

        // Añadir componentes al panel
        add(customerLabel);
        add(customerNameField);
        add(productLabel);
        add(productComboBox);
        add(quantityLabel);
        add(quantityField);
        add(unitPriceLabel);
        add(unitPriceField);
        add(totalLabel);
        add(totalField);
        add(calculateButton);
        add(clearButton);
        add(new JLabel(""));
        add(exitButton);

        // Eventos igual que antes...
        calculateButton.addActionListener(e -> {
            try {
                String quantityText = quantityField.getText();
                if (quantityText.isEmpty() || !quantityText.matches("\\d+")) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido para la cantidad.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int quantity = Integer.parseInt(quantityText);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor que 0.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double unitPrice = productPrices[productComboBox.getSelectedIndex()];
                double total = quantity * unitPrice;
                unitPriceField.setText(String.format("%.2f", unitPrice));
                totalField.setText(String.format("%.2f", total));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error en el cálculo. Por favor intente nuevamente.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            customerNameField.setText("");
            quantityField.setText("");
            unitPriceField.setText("");
            totalField.setText("");
            productComboBox.setSelectedIndex(0);
        });

        exitButton.addActionListener(e -> {
            // Puedes decidir si aquí quieres cerrar la app o limpiar el panel, o alguna otra acción
            // Por ejemplo:
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
        });

        productComboBox.addActionListener(e -> {
            int selectedIndex = productComboBox.getSelectedIndex();
            unitPriceField.setText(String.format("%.2f", productPrices[selectedIndex]));
        });

        // Inicializar precio unitario
        unitPriceField.setText(String.format("%.2f", productPrices[0]));
    }
}