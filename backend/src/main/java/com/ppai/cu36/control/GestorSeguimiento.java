package com.ppai.cu36.control;

import com.ppai.cu36.boundary.GPSTrackerXTR4500L;
import com.ppai.cu36.boundary.GoogleMapsBoundary;
import com.ppai.cu36.dto.BolsinDTO;
import com.ppai.cu36.dto.BolsinLocalizacion;
import com.ppai.cu36.dto.ConsultaSeguimientoResponse;
import com.ppai.cu36.model.*;
import com.ppai.cu36.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GestorSeguimiento {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private GPSTrackerXTR4500L gpsTracker;

    @Autowired
    private GoogleMapsBoundary googleMaps;

    private List<Bolsin> bolsinesEnviados;
    private Bolsin bolsinSeleccionado;
    private ComisionMedica CMDestino;
    private String CMOrigen;
    private String fechaHoraActual;
    private String nombreCMUsuarioLogueado;
    private int numeroBolsin;
    private int numeroPrecinto;
    private List<BolsinLocalizacion> posicionBolsin;

    public ConsultaSeguimientoResponse crearConsultaSeguimiento() {
        this.nombreCMUsuarioLogueado = buscarNombreCMUsuarioOrigen();
        if (this.nombreCMUsuarioLogueado == null) {
            return new ConsultaSeguimientoResponse("No hay sesión activa.");
        }

        this.CMOrigen = buscarBolsinCMOrigen();
        this.bolsinesEnviados = buscarBolsinesEnviados(this.CMOrigen);

        if (this.bolsinesEnviados.isEmpty()) {
            ConsultaSeguimientoResponse resp = new ConsultaSeguimientoResponse();
            resp.setNombreCMUsuarioLogueado(this.nombreCMUsuarioLogueado);
            resp.setNombreUsuarioLogueado(buscarNombreUsuarioLogueado());
            resp.setMensaje("No hay bolsines en estado Enviado para su comisión.");
            resp.setBolsines(new ArrayList<>());
            return resp;
        }

        List<Integer> numeros = new ArrayList<>();
        for (Bolsin b : this.bolsinesEnviados) {
            numeros.add(b.getNumeroBolsin());
        }

        this.posicionBolsin = obtenerDatosLocalizacionBolsines(numeros, this.CMOrigen);
        this.posicionBolsin = obtenerMapa(this.posicionBolsin);

        List<BolsinDTO> dtos = armarBolsinDTOs(this.bolsinesEnviados, this.posicionBolsin);

        ConsultaSeguimientoResponse response = new ConsultaSeguimientoResponse();
        response.setNombreCMUsuarioLogueado(this.nombreCMUsuarioLogueado);
        response.setNombreUsuarioLogueado(buscarNombreUsuarioLogueado());
        response.setBolsines(dtos);
        return response;
    }

    public ConsultaSeguimientoResponse tomarSeleccionBolsin(int numeroBolsin) {
        this.bolsinSeleccionado = dataRepository.getBolsinPorNumero(numeroBolsin);
        if (this.bolsinSeleccionado == null) {
            return new ConsultaSeguimientoResponse("Bolsín no encontrado.");
        }

        this.numeroBolsin = this.bolsinSeleccionado.getNumeroBolsin();
        this.numeroPrecinto = this.bolsinSeleccionado.getNumeroPrecinto();
        this.CMDestino = this.bolsinSeleccionado.obtenerCMDestino();

        enviarEmail();

        ConsultaSeguimientoResponse response = new ConsultaSeguimientoResponse();
        response.setMensaje("¿Desea notificar al GCM destino la ubicación del bolsín?");

        BolsinDTO dto = new BolsinDTO();
        dto.setNumeroBolsin(this.numeroBolsin);
        dto.setNumeroPrecinto(this.numeroPrecinto);
        if (this.CMDestino != null) {
            dto.setCmDestinoNombre(this.CMDestino.getNombre());
        }
        response.setBolsines(List.of(dto));
        return response;
    }

    public ConsultaSeguimientoResponse tomarConfirmacionEmail(int numeroBolsin, boolean confirma) {
        ConsultaSeguimientoResponse response = new ConsultaSeguimientoResponse();

        if (!confirma) {
            response.setMensaje("Consulta de seguimiento finalizada.");
            finCU();
            return response;
        }

        String emailDestino = buscarDireccionCorreo(numeroBolsin);
        getFechaHoraActual();
        String resultado = llamarCUNotificarUbicacionDeBolsin(emailDestino, this.fechaHoraActual);

        response.setMensaje(resultado);
        finCU();
        return response;
    }

    private String buscarNombreCMUsuarioOrigen() {
        Sesion sesion = Sesion.getInstancia();
        if (sesion == null) return null;

        Usuario usuario = sesion.buscarUsuarioLogueado();
        if (usuario == null) return null;

        Empleado empleado = usuario.obtenerEmpleado();
        if (empleado == null || empleado.obtenerCM() == null) return null;

        return empleado.obtenerCM().getNombre();
    }

    private String buscarBolsinCMOrigen() {
        Sesion sesion = Sesion.getInstancia();
        if (sesion == null) return null;
        Usuario usuario = sesion.buscarUsuarioLogueado();
        if (usuario == null) return null;
        Empleado empleado = usuario.obtenerEmpleado();
        if (empleado == null || empleado.obtenerCM() == null) return null;
        return empleado.obtenerCM().getCodigo();
    }

    private String buscarNombreUsuarioLogueado() {
        Sesion sesion = Sesion.getInstancia();
        if (sesion == null) return null;
        Usuario usuario = sesion.buscarUsuarioLogueado();
        if (usuario == null) return null;
        return usuario.getNombre();
    }

    private List<Bolsin> buscarBolsinesEnviados(String codigoCM) {
        List<Bolsin> resultado = new ArrayList<>();
        for (Bolsin b : dataRepository.getAllBolsines()) {
            if (b.esTuCMOrigen(codigoCM) && b.buscarBolsinesEnviados()) {
                resultado.add(b);
            }
        }
        return resultado;
    }

    private List<BolsinLocalizacion> obtenerDatosLocalizacionBolsines(List<Integer> numeros, String codigoCMOrigen) {
        try {
            return gpsTracker.getBolsinLocation(numeros, codigoCMOrigen);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<BolsinLocalizacion> obtenerMapa(List<BolsinLocalizacion> localizaciones) {
        return googleMaps.solicitarPosicionBolsinesEnMapa(localizaciones);
    }

    private void enviarEmail() {
    }

    private String llamarCUNotificarUbicacionDeBolsin(String emailDestino, String fechaHora) {
        if (emailDestino != null) {
            return "Notificación preparada para: " + emailDestino + " | Fecha: " + fechaHora;
        }
        return "No se encontró email del GCM destino. Consulta finalizada. | Fecha: " + fechaHora;
    }

    private void finCU() {
    }

    private String buscarDireccionCorreo(int numeroBolsin) {
        Bolsin bolsin = dataRepository.getBolsinPorNumero(numeroBolsin);
        if (bolsin == null) return null;
        return bolsin.buscarDireccionCorreo();
    }

    private String getFechaHoraActual() {
        this.fechaHoraActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return this.fechaHoraActual;
    }

    private List<BolsinDTO> armarBolsinDTOs(List<Bolsin> bolsines, List<BolsinLocalizacion> localizaciones) {
        List<BolsinDTO> dtos = new ArrayList<>();
        for (Bolsin b : bolsines) {
            BolsinDTO dto = new BolsinDTO();
            dto.setNumeroBolsin(b.getNumeroBolsin());
            dto.setNumeroPrecinto(b.getNumeroPrecinto());
            if (b.getCmOrigen() != null) {
                dto.setCmOrigenNombre(b.getCmOrigen().getNombre());
            }
            if (b.obtenerCMDestino() != null) {
                dto.setCmDestinoNombre(b.obtenerCMDestino().getNombre());
            }
            localizaciones.stream()
                    .filter(l -> l.getNumeroBolsin() == b.getNumeroBolsin())
                    .findFirst()
                    .ifPresent(loc -> {
                        dto.setLatitud(loc.getLatitud());
                        dto.setLongitud(loc.getLongitud());
                        if (loc.getFechaHoraActualizacion() != null) {
                            dto.setFechaHoraActualizacion(
                                loc.getFechaHoraActualizacion()
                                   .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                            );
                        }
                    });
            dtos.add(dto);
        }
        return dtos;
    }
}
