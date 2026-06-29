package com.ppai.cu36.model;

public class Rol {

    private String nombre;
    private String descripcion;

    public Rol() {}

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public boolean esGCM() {
        return "GCM".equalsIgnoreCase(nombre);
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
