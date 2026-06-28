package com.ppai.cu36.dto;

public class BolsinDTO {

    private int numeroBolsin;
    private int numeroPrecinto;
    private String cmDestinoNombre;
    private Double latitud;
    private Double longitud;
    private String fechaHoraActualizacion;
    private String cmOrigenNombre;

    public BolsinDTO() {}

    public int getNumeroBolsin() { return numeroBolsin; }
    public int getNumeroPrecinto() { return numeroPrecinto; }
    public String getCmDestinoNombre() { return cmDestinoNombre; }
    public Double getLatitud() { return latitud; }
    public Double getLongitud() { return longitud; }
    public String getFechaHoraActualizacion() { return fechaHoraActualizacion; }
    public String getCmOrigenNombre() { return cmOrigenNombre; }

    public void setNumeroBolsin(int numeroBolsin) { this.numeroBolsin = numeroBolsin; }
    public void setNumeroPrecinto(int numeroPrecinto) { this.numeroPrecinto = numeroPrecinto; }
    public void setCmDestinoNombre(String cmDestinoNombre) { this.cmDestinoNombre = cmDestinoNombre; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public void setFechaHoraActualizacion(String fechaHoraActualizacion) {
        this.fechaHoraActualizacion = fechaHoraActualizacion;
    }
    public void setCmOrigenNombre(String cmOrigenNombre) { this.cmOrigenNombre = cmOrigenNombre; }
}
