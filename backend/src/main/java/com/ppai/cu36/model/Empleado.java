package com.ppai.cu36.model;

public class Empleado {

    private String nombre;
    private String apellido;
    private String email;
    private ComisionMedica cm;
    private Rol rol;

    public Empleado() {}

    public Empleado(String nombre, String apellido, String email, ComisionMedica cm, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.cm = cm;
        this.rol = rol;
    }

    public boolean sosGCM() {
        return rol != null && rol.esGCM();
    }

    public boolean esTuCM(String codigoCM) {
        return cm != null && cm.getCodigo().equals(codigoCM);
    }

    public ComisionMedica obtenerCM() { return cm; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public Rol getRol() { return rol; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEmail(String email) { this.email = email; }
    public void setCm(ComisionMedica cm) { this.cm = cm; }
    public void setRol(Rol rol) { this.rol = rol; }
}
