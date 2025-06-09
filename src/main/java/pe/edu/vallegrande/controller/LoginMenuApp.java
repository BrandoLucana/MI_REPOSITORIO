package pe.edu.vallegrande.controller;

import pe.edu.vallegrande.view.FormularioEntrenador;
import pe.edu.vallegrande.view.FormularioEstudiante;
import pe.edu.vallegrande.view.FormularioPolo;
import pe.edu.vallegrande.view.GestionTorneos;
import pe.edu.vallegrande.view.EquiposVoley;
import pe.edu.vallegrande.view.MenuPrincipal;
import pe.edu.vallegrande.view.PanelBienvenida;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LoginMenuApp extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(LoginMenuApp.class.getName());
    private boolean sesionActiva = false;
    private final JMenuItem loginItem;
    private final JMenuItem logoutItem;
    private final JMenu menuFormularios;
    private final JPanel panelContenido;

    public LoginMenuApp() {
        setTitle("Sistema de Gestión - Valle Grande");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.add(new PanelBienvenida(), BorderLayout.CENTER);
        add(panelContenido);

        JMenuBar menuBar = new JMenuBar();

        JMenu loginMenu = new JMenu("Sesión");
        loginItem = new JMenuItem("Iniciar Sesión");
        logoutItem = new JMenuItem("Cerrar Sesión");

        loginItem.addActionListener(e -> mostrarFormularioLogin());
        logoutItem.addActionListener(e -> cerrarSesion());

        loginMenu.add(loginItem);
        loginMenu.add(logoutItem);
        menuBar.add(loginMenu);

        menuFormularios = new JMenu("Formularios");

        JMenuItem poloForm = new JMenuItem("Formulario Polo");
        poloForm.addActionListener(e -> mostrarContenido(new FormularioPolo()));

        JMenuItem equiposVoleyForm = new JMenuItem("Equipos de Vóley");
        equiposVoleyForm.addActionListener(e -> mostrarContenido(new EquiposVoley()));

        JMenuItem entrenadorForm = new JMenuItem("Formulario Entrenador");
        entrenadorForm.addActionListener(e -> mostrarContenido(new FormularioEntrenador()));

        JMenuItem estudianteForm = new JMenuItem("Formulario Estudiante");
        estudianteForm.addActionListener(e -> mostrarContenido(new FormularioEstudiante()));

        JMenuItem gestionTorneosForm = new JMenuItem("Gestión de Torneos");
        gestionTorneosForm.addActionListener(e -> mostrarContenido(new GestionTorneos()));



        menuFormularios.add(poloForm);
        menuFormularios.add(equiposVoleyForm);
        menuFormularios.add(entrenadorForm);
        menuFormularios.add(estudianteForm);
        menuFormularios.add(gestionTorneosForm);


        menuBar.add(menuFormularios);

        JMenu acercaDeMenu = new JMenu("Acerca de");
        JMenuItem infoItem = new JMenuItem("Créditos");
        infoItem.addActionListener(e -> mostrarCreditos());
        acercaDeMenu.add(infoItem);
        menuBar.add(acercaDeMenu);

        setJMenuBar(menuBar);
        actualizarOpcionesSesion();
    }

    private void mostrarFormularioLogin() {
        try {
            JTextField userField = new JTextField();
            JPasswordField passField = new JPasswordField();
            Object[] fields = {
                    "Usuario:", userField,
                    "Contraseña:", passField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Iniciar Sesión", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String usuario = userField.getText();
                String contrasena = new String(passField.getPassword());

                if (usuario.equals("admin") && contrasena.equals("1234")) {
                    sesionActiva = true;
                    JOptionPane.showMessageDialog(this, "¡Bienvenido, " + usuario + "!",
                            "Acceso concedido", JOptionPane.INFORMATION_MESSAGE);
                    actualizarOpcionesSesion();
                    LOGGER.info("Sesión iniciada para el usuario: " + usuario);
                } else {
                    JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
                    LOGGER.warning("Intento de inicio de sesión fallido para el usuario: " + usuario);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al mostrar formulario de login", ex);
            JOptionPane.showMessageDialog(this, "Error al procesar el inicio de sesión", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrarSesion() {
        try {
            sesionActiva = false;
            JOptionPane.showMessageDialog(this, "Sesión cerrada correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
            actualizarOpcionesSesion();
            panelContenido.removeAll();
            panelContenido.add(new PanelBienvenida(), BorderLayout.CENTER);
            panelContenido.revalidate();
            panelContenido.repaint();
            LOGGER.info("Sesión cerrada correctamente");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al cerrar sesión", ex);
            JOptionPane.showMessageDialog(this, "Error al cerrar sesión", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarOpcionesSesion() {
        loginItem.setEnabled(!sesionActiva);
        logoutItem.setEnabled(sesionActiva);
        menuFormularios.setEnabled(sesionActiva);
    }

    private void mostrarContenido(JPanel nuevoContenido) {
        try {
            panelContenido.removeAll();
            panelContenido.add(nuevoContenido, BorderLayout.CENTER);
            panelContenido.revalidate();
            panelContenido.repaint();
            LOGGER.info("Contenido mostrado: " + nuevoContenido.getClass().getSimpleName());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al mostrar contenido", ex);
            JOptionPane.showMessageDialog(this, "Error al cargar el formulario", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarCreditos() {
        JOptionPane.showMessageDialog(this,
                """
                Sistema de Gestión - Valle Grande
                Creado por el Team 02
                Estudiantes del Instituto Superior Valle Grande
                Proyecto de práctica con Java Swing
                Versión 1.0
                """, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                LoginMenuApp app = new LoginMenuApp();
                app.setVisible(true);
                LOGGER.info("Aplicación iniciada correctamente");
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error al iniciar la aplicación", ex);
                JOptionPane.showMessageDialog(null, "Error al iniciar la aplicación", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}