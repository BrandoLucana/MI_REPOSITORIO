package pe.edu.vallegrande.view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import pe.edu.vallegrande.controller.EntrenadorController; // Importar el controlador

public class MenuPrincipal extends JFrame {

    private JButton btnFormularioEstudiante;
    private JButton btnFormularioEntrenador;
    private JButton btnTorneoForm;

    public MenuPrincipal() {
        setTitle("Formularios de la Academia Pasión por el Vóley");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // margen entre componentes

        // PANEL SUPERIOR: TÍTULO CON IMAGEN
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(new Color(180, 0, 0));
        panelSuperior.setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("FORMULARIOS DE LA ACADEMIA PASIÓN POR EL VÓLEY", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);

        URL imageUrl = getClass().getClassLoader().getResource("logo_voley.jpg");
        JLabel lblImagen;
        if (imageUrl != null) {
            ImageIcon icono = new ImageIcon(imageUrl);
            lblImagen = new JLabel(icono);
            lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            lblImagen = new JLabel("Imagen no encontrada", JLabel.CENTER);
            lblImagen.setForeground(Color.WHITE);
        }
        panelSuperior.add(lblImagen, BorderLayout.CENTER);

        // PANEL CENTRAL: BOTONES
        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 15, 15));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panelBotones.setBackground(new Color(255, 204, 204)); // fondo claro

        btnFormularioEstudiante = new JButton("Formulario Estudiante");
        btnFormularioEntrenador = new JButton("Formulario Entrenador");
        btnTorneoForm = new JButton("Formulario Torneo");

        panelBotones.add(btnFormularioEstudiante);
        panelBotones.add(btnFormularioEntrenador);
        panelBotones.add(btnTorneoForm);

        // AGREGAR PANELES
        add(panelSuperior, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);

        // EVENTOS
        btnFormularioEstudiante.addActionListener(e -> {
            new FormularioEstudiante().setVisible(true);
        });

        btnFormularioEntrenador.addActionListener(e -> {
            FormularioEntrenador formularioEntrenador = new FormularioEntrenador();
            EntrenadorController entrenadorController = new EntrenadorController(formularioEntrenador);
            formularioEntrenador.setController(entrenadorController);
            formularioEntrenador.setVisible(true);
        });

        btnTorneoForm.addActionListener(e -> new GestionTorneos().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}
