package com.ppai.cu36.model;

public class Estado {

    private String ambito;
    private String descripcion;
    private String nombre;

    public Estado() {}

    public Estado(String ambito, String descripcion, String nombre) {
        this.ambito = ambito;
        this.descripcion = descripcion;
        this.nombre = nombre;
    }

    public boolean sosEnviado() {
        return "Enviado".equalsIgnoreCase(nombre);
    }

    public String getAmbito() { return ambito; }
    public String getDescripcion() { return descripcion; }
    public String getNombre() { return nombre; }

    public void setAmbito(String ambito) { this.ambito = ambito; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
