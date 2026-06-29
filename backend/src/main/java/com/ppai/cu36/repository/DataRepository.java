package com.ppai.cu36.repository;

import com.ppai.cu36.model.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class DataRepository {

    private static final List<Bolsin> bolsines = new ArrayList<>();
    private static final List<Usuario> usuarios = new ArrayList<>();

    static {
        ComisionMedica cmBuenosAires = new ComisionMedica("CM01", "CM Buenos Aires Central", "cm01@organismo.gob.ar", "Av. Corrientes 1234, CABA", "011-4444-0001");
        ComisionMedica cmCordoba = new ComisionMedica("CM02", "CM Córdoba Capital", "cm02@organismo.gob.ar", "Bv. San Juan 456, Córdoba", "0351-4444-0002");
        ComisionMedica cmRosario = new ComisionMedica("CM03", "CM Rosario", "cm03@organismo.gob.ar", "San Martín 789, Rosario", "0341-4444-0003");
        ComisionMedica cmMendoza = new ComisionMedica("CM04", "CM Mendoza", "cm04@organismo.gob.ar", "Sarmiento 321, Mendoza", "0261-4444-0004");

        Rol rolGCM = new Rol("GCM", "Gerente de Comisión Médica");
        Rol rolEB = new Rol("EB", "Encargado de Bolsines");

        Empleado gcmCordoba = new Empleado("Roberto", "Fernández", "rfernandez@organismo.gob.ar", cmCordoba, rolGCM);
        Empleado gcmRosario = new Empleado("Carla", "Méndez", "cmendez@organismo.gob.ar", cmRosario, rolGCM);
        Empleado gcmMendoza = new Empleado("Luis", "García", "lgarcia@organismo.gob.ar", cmMendoza, rolGCM);
        Empleado empBA = new Empleado("Juan", "Pérez", "jperez@organismo.gob.ar", cmBuenosAires, rolEB);
        Empleado gcmBA = new Empleado("María", "López", "mlopez@organismo.gob.ar", cmBuenosAires, rolGCM);

        cmBuenosAires.setEmpleados(Arrays.asList(empBA, gcmBA));
        cmCordoba.setEmpleados(Arrays.asList(gcmCordoba));
        cmRosario.setEmpleados(Arrays.asList(gcmRosario));
        cmMendoza.setEmpleados(Arrays.asList(gcmMendoza));

        Usuario usuarioBA = new Usuario("jperez", "1234", empBA);
        Usuario usuarioGCMBA = new Usuario("mlopez", "1234", gcmBA);
        Usuario usuarioCordoba = new Usuario("rfernandez", "1234", gcmCordoba);
        Usuario usuarioRosario = new Usuario("cmendez", "1234", gcmRosario);
        Usuario usuarioMendoza = new Usuario("lgarcia", "1234", gcmMendoza);
        usuarios.add(usuarioBA);
        usuarios.add(usuarioGCMBA);
        usuarios.add(usuarioCordoba);
        usuarios.add(usuarioRosario);
        usuarios.add(usuarioMendoza);

        Sesion.iniciarSesion(usuarioBA);

        Estado estadoEnviado = new Estado("Bolsin", "El bolsín fue retirado y está en tránsito", "Enviado");

        CambioEstadoBolsin ce1 = new CambioEstadoBolsin(LocalDateTime.now().minusHours(5), estadoEnviado);
        Bolsin b1 = new Bolsin(LocalDate.now(), 1001, 55001, 320.5, cmBuenosAires, cmCordoba, Arrays.asList(ce1));

        CambioEstadoBolsin ce2 = new CambioEstadoBolsin(LocalDateTime.now().minusHours(3), estadoEnviado);
        Bolsin b2 = new Bolsin(LocalDate.now(), 1002, 55002, 415.0, cmBuenosAires, cmRosario, Arrays.asList(ce2));

        CambioEstadoBolsin ce3 = new CambioEstadoBolsin(LocalDateTime.now().minusHours(8), estadoEnviado);
        Bolsin b3 = new Bolsin(LocalDate.now().minusDays(1), 1003, 55003, 180.0, cmBuenosAires, cmMendoza, Arrays.asList(ce3));

        CambioEstadoBolsin ce4 = new CambioEstadoBolsin(LocalDateTime.now().minusHours(2), estadoEnviado);
        Bolsin b4 = new Bolsin(LocalDate.now(), 1004, 55004, 250.0, cmCordoba, cmBuenosAires, Arrays.asList(ce4));

        bolsines.addAll(Arrays.asList(b1, b2, b3, b4));
    }

    public List<Bolsin> getAllBolsines() {
        return bolsines;
    }

    public List<Bolsin> getBolsinesEnviadosPorCM(String codigoCM) {
        List<Bolsin> resultado = new ArrayList<>();
        for (Bolsin b : bolsines) {
            if (b.esTuCMOrigen(codigoCM) && b.buscarBolsinesEnviados()) {
                resultado.add(b);
            }
        }
        return resultado;
    }

    public Bolsin getBolsinPorNumero(int numeroBolsin) {
        return bolsines.stream()
                .filter(b -> b.getNumeroBolsin() == numeroBolsin)
                .findFirst()
                .orElse(null);
    }

    public Usuario getUsuarioPorUsername(String username) {
        return usuarios.stream()
                .filter(u -> u.getNombre().equals(username))
                .findFirst()
                .orElse(null);
    }
}
