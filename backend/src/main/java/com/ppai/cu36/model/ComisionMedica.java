package com.ppai.cu36.model;

import java.util.List;

public class ComisionMedica {

    private String codigo;
    private String nombre;
    private String email;
    private String direccion;
    private String telefono;
    private List<Empleado> empleados;

    public ComisionMedica() {}

    public ComisionMedica(String codigo, String nombre, String email, String direccion, String telefono) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public Empleado buscarGerente() {
        if (empleados == null) return null;
        return empleados.stream()
                .filter(Empleado::sosGCM)
                .findFirst()
                .orElse(null);
    }

    public String getNombre() { return nombre; }
    public String getCodigo() { return codigo; }
    public String getEmail() { return email; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public List<Empleado> getEmpleados() { return empleados; }

    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEmpleados(List<Empleado> empleados) { this.empleados = empleados; }
}
