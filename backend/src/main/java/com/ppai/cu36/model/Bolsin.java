package com.ppai.cu36.model;

import java.time.LocalDate;
import java.util.List;

public class Bolsin {

    private LocalDate fecha;
    private int numeroBolsin;
    private int numeroPrecinto;
    private double peso;
    private ComisionMedica cmOrigen;
    private ComisionMedica cmDestino;
    private List<CambioEstadoBolsin> cambiosEstado;

    public Bolsin() {}

    public Bolsin(LocalDate fecha, int numeroBolsin, int numeroPrecinto, double peso,
                  ComisionMedica cmOrigen, ComisionMedica cmDestino,
                  List<CambioEstadoBolsin> cambiosEstado) {
        this.fecha = fecha;
        this.numeroBolsin = numeroBolsin;
        this.numeroPrecinto = numeroPrecinto;
        this.peso = peso;
        this.cmOrigen = cmOrigen;
        this.cmDestino = cmDestino;
        this.cambiosEstado = cambiosEstado;
    }

    public boolean esTuCMOrigen(String codigoCM) {
        return cmOrigen != null && cmOrigen.getCodigo().equals(codigoCM);
    }

    public boolean buscarBolsinesEnviados() {
        if (cambiosEstado == null) return false;
        return cambiosEstado.stream().anyMatch(CambioEstadoBolsin::sosEnviado);
    }

    public ComisionMedica obtenerCMDestino() {
        return cmDestino;
    }

    public String buscarDireccionCorreo() {
        if (cmDestino == null) return null;
        Empleado gcm = cmDestino.buscarGerente();
        return gcm != null ? gcm.getEmail() : cmDestino.getEmail();
    }

    public int getNumeroBolsin() { return numeroBolsin; }
    public int getNumeroPrecinto() { return numeroPrecinto; }
    public LocalDate getFecha() { return fecha; }
    public double getPeso() { return peso; }
    public ComisionMedica getCmOrigen() { return cmOrigen; }
    public List<CambioEstadoBolsin> getCambiosEstado() { return cambiosEstado; }

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setNumeroBolsin(int numeroBolsin) { this.numeroBolsin = numeroBolsin; }
    public void setNumeroPrecinto(int numeroPrecinto) { this.numeroPrecinto = numeroPrecinto; }
    public void setPeso(double peso) { this.peso = peso; }
    public void setCmOrigen(ComisionMedica cmOrigen) { this.cmOrigen = cmOrigen; }
    public void setCmDestino(ComisionMedica cmDestino) { this.cmDestino = cmDestino; }
    public void setCambiosEstado(List<CambioEstadoBolsin> cambiosEstado) { this.cambiosEstado = cambiosEstado; }
}
