package com.ppai.cu36.dto;

import java.time.LocalDateTime;

public class BolsinLocalizacion {

    private int numeroBolsin;
    private double latitud;
    private double longitud;
    private LocalDateTime fechaHoraActualizacion;

    public BolsinLocalizacion() {}

    public int getNumeroBolsin() { return numeroBolsin; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public LocalDateTime getFechaHoraActualizacion() { return fechaHoraActualizacion; }

    public void setNumeroBolsin(int numeroBolsin) { this.numeroBolsin = numeroBolsin; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public void setFechaHoraActualizacion(LocalDateTime fechaHoraActualizacion) {
        this.fechaHoraActualizacion = fechaHoraActualizacion;
    }
}
