package com.ppai.cu36.dto;

import java.util.List;

public class ConsultaSeguimientoResponse {

    private String nombreCMUsuarioLogueado;
    private String nombreUsuarioLogueado;
    private List<BolsinDTO> bolsines;
    private String mensaje;

    public ConsultaSeguimientoResponse() {}

    public ConsultaSeguimientoResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombreCMUsuarioLogueado() { return nombreCMUsuarioLogueado; }
    public String getNombreUsuarioLogueado() { return nombreUsuarioLogueado; }
    public List<BolsinDTO> getBolsines() { return bolsines; }
    public String getMensaje() { return mensaje; }

    public void setNombreCMUsuarioLogueado(String nombreCMUsuarioLogueado) {
        this.nombreCMUsuarioLogueado = nombreCMUsuarioLogueado;
    }
    public void setNombreUsuarioLogueado(String nombreUsuarioLogueado) {
        this.nombreUsuarioLogueado = nombreUsuarioLogueado;
    }
    public void setBolsines(List<BolsinDTO> bolsines) { this.bolsines = bolsines; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
