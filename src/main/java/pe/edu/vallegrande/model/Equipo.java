package pe.edu.vallegrande.model;

public class Equipo {
    private int id;
    private String nombreCliente;
    private String producto;
    private int cantidad;
    private double precioUnitario;
    private double totalPago;

    // Getters
    public int getId() {
        return id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getTotalPago() {
        return totalPago;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setTotalPago(double totalPago) {
        this.totalPago = totalPago;
    }
}
