package pe.edu.vallegrande.model;

public class Estudiante {
    private int id;
    private String nombre;
    private String apellido;
    private int edad;
    private String tipoDocumento;  // Nuevo campo: "DNI" o "Carné de Extranjería"
    private String numeroDocumento; // Reemplaza al campo dni
    private String correo;
    private String celular;
    private String categoria;
    private String genero;

    public Estudiante(String nombre, String apellido, int edad, String categoria) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.categoria = categoria;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public int getEdad() {
        return edad;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCelular() {
        return celular;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getGenero() {
        return genero;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
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

    // Método para mostrar los datos actualizado
    public String getResumen() {
        return "Nombre: " + nombre + " " + apellido + "\n" +
                "Edad: " + edad + "\n" +
                "Documento: " + tipoDocumento + " - " + numeroDocumento + "\n" +
                "Correo: " + correo + "\n" +
                "Celular: " + celular + "\n" +
                "Género: " + genero + "\n" +
                "Categoría: " + categoria;
    }
}