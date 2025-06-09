package pe.edu.vallegrande.view;

import javax.swing.*;
import java.awt.*;

public class PanelBienvenida extends JPanel {

    public PanelBienvenida() {
        setLayout(new BorderLayout());

        // Mensaje de bienvenida
        JLabel mensaje = new JLabel("Bienvenido a la aplicación del Team 02", SwingConstants.CENTER);
        mensaje.setFont(new Font("Arial", Font.BOLD, 24));
        mensaje.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Imagen (asegúrate de tener una imagen en tu carpeta del proyecto)
        ImageIcon icono = new ImageIcon("logo.png"); // Cambia "logo.png" por tu imagen
        Image imagenEscalada = icono.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel imagen = new JLabel(new ImageIcon(imagenEscalada));
        imagen.setHorizontalAlignment(SwingConstants.CENTER);

        // Añadir al panel
        add(mensaje, BorderLayout.NORTH);
        add(imagen, BorderLayout.CENTER);
    }
}