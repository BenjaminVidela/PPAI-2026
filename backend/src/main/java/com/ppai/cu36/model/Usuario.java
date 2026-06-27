package com.ppai.cu36.model;

public class Usuario {

    private String nombre;
    private String contrasena;
    private Empleado empleado;

    public Usuario() {}

    public Usuario(String nombre, String contrasena, Empleado empleado) {
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.empleado = empleado;
    }

    public Empleado obtenerEmpleado() {
        return empleado;
    }

    public String getNombre() { return nombre; }
    public String getContrasena() { return contrasena; }
    public Empleado getEmpleado() { return empleado; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
}
