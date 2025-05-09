package pe.edu.vallegrande.modelo;

public class Estudiante {
    private String nombre;
    private String apellido;
    private int edad;
    private String categoria;

    private String dni;
    private String correo;
    private String celular;
    private String genero;

    public Estudiante(String nombre, String apellido, int edad, String categoria) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.categoria = categoria;
    }

    // Setters
    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    // Método para mostrar los datos
    public String getResumen() {
        return "Nombre: " + nombre + " " + apellido + "\n" +
                "Edad: " + edad + "\n" +
                "DNI: " + dni + "\n" +
                "Correo: " + correo + "\n" +
                "Celular: " + celular + "\n" +
                "Género: " + genero + "\n" +
                "Categoría: " + categoria;
    }
}