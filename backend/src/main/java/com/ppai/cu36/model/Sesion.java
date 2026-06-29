package com.ppai.cu36.model;

public class Sesion {

    private Usuario usuario;
    private java.time.LocalDateTime fechaHoraInicio;
    private java.time.LocalDateTime fechaHoraFin;

    private static Sesion instancia;

    private Sesion(Usuario usuario) {
        this.usuario = usuario;
        this.fechaHoraInicio = java.time.LocalDateTime.now();
    }

    public static Sesion getInstancia() {
        return instancia;
    }

    public static void iniciarSesion(Usuario usuario) {
        instancia = new Sesion(usuario);
    }

    public Usuario buscarUsuarioLogueado() {
        return usuario;
    }

}
