package com.ppai.cu36.model;

import java.time.LocalDateTime;

public class CambioEstadoBolsin {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String responsableCE;
    private Estado estado;

    public CambioEstadoBolsin() {}

    public CambioEstadoBolsin(LocalDateTime fechaHoraInicio, String responsableCE, Estado estado) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.responsableCE = responsableCE;
        this.estado = estado;
    }

    public boolean esEstadoActual() {
        return fechaHoraFin == null;
    }

    public boolean sosEnviado() {
        return esEstadoActual() && estado != null && estado.sosEnviado();
    }

    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public String getResponsableCE() { return responsableCE; }
    public Estado getEstado() { return estado; }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
    public void setEstado(Estado estado) { this.estado = estado; }
}
