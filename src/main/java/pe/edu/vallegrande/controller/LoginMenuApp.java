package pe.edu.vallegrande.controller;

import pe.edu.vallegrande.view.*;
import javax.swing.*;
import java.awt.*;

public class LoginMenuApp extends JFrame {

    private boolean sesionActiva = false;
    private final JPanel panelContenido;
    private final JLabel estadoUsuario;
    private final JButton btnSesion;

    public LoginMenuApp() {
        setTitle("Sistema de Gestión - Valle Grande");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // BARRA DE MENÚ
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(176, 0, 32));
        menuBar.setForeground(Color.WHITE);
        menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // MENÚ "Sistema" con ícono
        JMenu menuSistema = new JMenu("Sistema");
        menuSistema.setForeground(Color.WHITE);
        menuSistema.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // Cargar el ícono
        java.net.URL imgURL = getClass().getResource("/computadora.png");
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            menuSistema.setIcon(icon);
        } else {
            System.err.println("No se pudo cargar el ícono: /resources/computadora.png");
            // Depuración: Imprimir el classpath para verificar
            System.out.println("Classpath: " + System.getProperty("java.class.path"));
        }

        // ÍTEMS DEL MENÚ "Sistema"
        menuSistema.add(crearItemMenu("Gestión de Torneos", new GestionTorneos()));
        menuSistema.add(crearItemMenu("Formulario Entrenador", new FormularioEntrenador()));
        menuSistema.add(crearItemMenu("Formulario Estudiante", new FormularioEstudiante()));
        menuSistema.add(crearItemMenu("Formulario Polo", new FormularioPolo()));
        menuSistema.add(crearItemMenu("Equipos de Vóley", new EquiposVoley()));

        // MENÚ "Acerca de" como un menú con un solo ítem
        JMenu menuAcercaDe = new JMenu("Acerca de");
        menuAcercaDe.setForeground(Color.WHITE);
        menuAcercaDe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // Cargar el ícono
        java.net.URL imagURL = getClass().getResource("/acerca.png");
        if (imagURL != null) {
            ImageIcon icon = new ImageIcon(imagURL);
            menuAcercaDe.setIcon(icon);
        } else {
            System.err.println("No se pudo cargar el ícono: /resources/acerca.png");
            // Depuración: Imprimir el classpath para verificar
            System.out.println("Classpath: " + System.getProperty("java.class.path"));
        }

        menuAcercaDe.add(crearItemAcercaDe());

        // AÑADIR MENÚS A LA BARRA
        menuBar.add(menuSistema);
        menuBar.add(menuAcercaDe);
        setJMenuBar(menuBar);

        // PANEL SUPERIOR
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        estadoUsuario = new JLabel("Invitado");
        estadoUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topPanel.add(estadoUsuario, BorderLayout.WEST);

        JPanel botonesTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesTop.setBackground(Color.WHITE);

        btnSesion = new JButton("Iniciar Sesión");
        btnSesion.addActionListener(e -> {
            if (sesionActiva) cerrarSesion();
            else mostrarLogin();
        });

        botonesTop.add(btnSesion);
        topPanel.add(botonesTop, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // PANEL DE CONTENIDO
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Color.WHITE);
        mostrarBienvenida();
        add(panelContenido, BorderLayout.CENTER);
    }

    private JMenuItem crearItemMenu(String nombre, JPanel contenido) {
        JMenuItem item = new JMenuItem(nombre);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.addActionListener(e -> {
            if (sesionActiva) {
                panelContenido.removeAll();
                panelContenido.add(contenido, BorderLayout.CENTER);
                panelContenido.revalidate();
                panelContenido.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Debe iniciar sesión para acceder.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            }
        });
        return item;
    }

    private JMenuItem crearItemAcercaDe() {
        JMenuItem item = new JMenuItem("Acerca de");
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.addActionListener(e -> mostrarCreditos());
        return item;
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
                Sistema de Gestión - Pasión por el Vóley
                Proyecto académico desarrollado en Java Swing

                Bienvenido/a al sistema de gestión de la academia deportiva.

                Este sistema permite registrar, consultar y administrar la información de torneos, entrenadores, estudiantes, polos y equipos de vóley.

                Participantes del equipo:
                - Nayeli Herrera Albino
                - Alejandro Soto Cárdenas
                - Brando Flores Lucana

                Instituto Superior Tecnológico Valle Grande.
                Versión 2025.
                """, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginMenuApp().setVisible(true));
    }
}