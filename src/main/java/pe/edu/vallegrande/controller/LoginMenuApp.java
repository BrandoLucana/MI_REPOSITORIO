package pe.edu.vallegrande.controller;

import pe.edu.vallegrande.view.*;

import javax.swing.*;
import java.awt.*;

public class LoginMenuApp extends JFrame {

    private boolean sesionActiva = false;
    private JPanel panelContenido;
    private JLabel estadoUsuario;
    private JButton btnSesion;

    public LoginMenuApp() {
        setTitle("Sistema de Gestión - Valle Grande");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // PANEL IZQUIERDO (MENÚ)
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setBackground(new Color(176, 0, 32));
        panelIzquierdo.setPreferredSize(new Dimension(220, getHeight()));
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));

        JLabel lblTituloMenu = new JLabel("Panel de Control", SwingConstants.CENTER);
        lblTituloMenu.setForeground(Color.WHITE);
        lblTituloMenu.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloMenu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        lblTituloMenu.setAlignmentX(Component.CENTER_ALIGNMENT);

        // PANELES COMO SECCIONES DE NAVEGACIÓN
        panelIzquierdo.add(lblTituloMenu);
        panelIzquierdo.add(crearItemMenu("Gestión de Torneos", new GestionTorneos()));
        panelIzquierdo.add(crearItemMenu("Formulario Entrenador", new FormularioEntrenador()));
        panelIzquierdo.add(crearItemMenu("Formulario Estudiante", new FormularioEstudiante()));
        panelIzquierdo.add(crearItemMenu("Formulario Polo", new FormularioPolo()));
        panelIzquierdo.add(crearItemMenu("Equipos de Vóley", new EquiposVoley()));

        add(panelIzquierdo, BorderLayout.WEST);

        // PANEL SUPERIOR
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        estadoUsuario = new JLabel("Invitado");
        estadoUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topPanel.add(estadoUsuario, BorderLayout.WEST);

        JPanel botonesTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesTop.setBackground(Color.WHITE);

        JButton btnAcercaDe = new JButton("Acerca de");
        btnAcercaDe.addActionListener(e -> mostrarCreditos());

        btnSesion = new JButton("Iniciar Sesión");
        btnSesion.addActionListener(e -> {
            if (sesionActiva) cerrarSesion();
            else mostrarLogin();
        });

        botonesTop.add(btnAcercaDe);
        botonesTop.add(btnSesion);
        topPanel.add(botonesTop, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // PANEL DE CONTENIDO
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Color.WHITE);
        mostrarBienvenida();
        add(panelContenido, BorderLayout.CENTER);
    }

    private JPanel crearItemMenu(String nombre, JPanel contenido) {
        JLabel label = new JLabel(nombre);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(176, 0, 32));
        panel.add(label, BorderLayout.CENTER);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (sesionActiva) {
                    panelContenido.removeAll();
                    panelContenido.add(contenido, BorderLayout.CENTER);
                    panelContenido.revalidate();
                    panelContenido.repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "Debe iniciar sesión para acceder.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void mostrarLogin() {
        JTextField usuario = new JTextField();
        JPasswordField password = new JPasswordField();
        Object[] campos = {"Usuario:", usuario, "Contraseña:", password};
        int opcion = JOptionPane.showConfirmDialog(this, campos, "Iniciar Sesión", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            if (usuario.getText().equals("admin") && new String(password.getPassword()).equals("1234")) {
                sesionActiva = true;
                estadoUsuario.setText("Sesión activa: admin");
                btnSesion.setText("Cerrar Sesión");
                mostrarBienvenida();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cerrarSesion() {
        sesionActiva = false;
        estadoUsuario.setText("Invitado");
        btnSesion.setText("Iniciar Sesión");
        mostrarBienvenida();
    }

    private void mostrarBienvenida() {
        panelContenido.removeAll();

        JPanel panelTexto = new JPanel();
        panelTexto.setBackground(Color.WHITE);
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Sistema de Gestión - Pasión por el Vóley");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Proyecto académico desarrollado en Java Swing");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JTextArea contenido = new JTextArea(
                """
                Bienvenido/a al sistema de gestión de la academia deportiva.

                Este sistema permite registrar, consultar y administrar la información de torneos, entrenadores, estudiantes, polos y equipos de vóley.

                Participantes del equipo:
                - Nayeli Herrera Albino
                - Alejandro Soto Cárdenas
                - Brando Flores Lucana

                Instituto Superior Tecnológico Valle Grande.
                Versión 2025.
                """
        );
        contenido.setEditable(false);
        contenido.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        contenido.setWrapStyleWord(true);
        contenido.setLineWrap(true);
        contenido.setBackground(Color.WHITE);

        panelTexto.add(titulo);
        panelTexto.add(subtitulo);
        panelTexto.add(contenido);

        panelContenido.add(panelTexto, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarCreditos() {
        JOptionPane.showMessageDialog(this,
                """
                Sistema de Gestión - Valle Grande
                Proyecto académico en Java Swing

                Participantes:
                - Nayeli Herrera Albino
                - Alejandro Soto Cárdenas
                - Brando Flores Lucana

                Instituto Superior Tecnológico Valle Grande
                Versión 1.0 - 2025
                """, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginMenuApp().setVisible(true);
        });
    }
}
