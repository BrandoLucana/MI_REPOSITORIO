package pe.edu.vallegrande.model;

public class Polo {
    private int id;
    private String nombreEnPolo;
    private int numeroEnPolo;
    private String talla;
    private String deporte;
    private boolean incluyeShort;
    private boolean incluyeMedias;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreEnPolo() { return nombreEnPolo; }
    public void setNombreEnPolo(String nombreEnPolo) { this.nombreEnPolo = nombreEnPolo; }

    public int getNumeroEnPolo() { return numeroEnPolo; }
    public void setNumeroEnPolo(int numeroEnPolo) { this.numeroEnPolo = numeroEnPolo; }

    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public String getDeporte() { return deporte; }
    public void setDeporte(String deporte) { this.deporte = deporte; }

    public boolean isIncluyeShort() { return incluyeShort; }
    public void setIncluyeShort(boolean incluyeShort) { this.incluyeShort = incluyeShort; }

    public boolean isIncluyeMedias() { return incluyeMedias; }
    public void setIncluyeMedias(boolean incluyeMedias) { this.incluyeMedias = incluyeMedias; }
}
