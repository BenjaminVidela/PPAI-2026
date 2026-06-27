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

    public ConsultaSeguimientoResponse crearConsultaSeguimiento() {
        String nombreCM = buscarNombreCMUsuarioOrigen();
        if (nombreCM == null) {
            return new ConsultaSeguimientoResponse("No hay sesión activa.");
        }

        String codigoCM = buscarBolsinCMOrigen();
        List<Bolsin> bolsinesEnviados = buscarBolsinesEnviados(codigoCM);

        if (bolsinesEnviados.isEmpty()) {
            ConsultaSeguimientoResponse resp = new ConsultaSeguimientoResponse();
            resp.setNombreCMUsuarioLogueado(nombreCM);
            resp.setMensaje("No hay bolsines en estado Enviado para su comisión.");
            resp.setBolsines(new ArrayList<>());
            return resp;
        }

        List<Integer> numeros = new ArrayList<>();
        for (Bolsin b : bolsinesEnviados) {
            numeros.add(b.getNumeroBolsin());
        }

        List<BolsinLocalizacion> localizaciones = obtenerDatosLocalizacionBolsines(numeros, codigoCM);
        localizaciones = obtenerMapa(localizaciones);

        List<BolsinDTO> dtos = armarBolsinDTOs(bolsinesEnviados, localizaciones);

        ConsultaSeguimientoResponse response = new ConsultaSeguimientoResponse();
        response.setNombreCMUsuarioLogueado(nombreCM);
        response.setBolsines(dtos);
        return response;
    }

    public ConsultaSeguimientoResponse tomarSeleccionBolsin(int numeroBolsin) {
        Bolsin seleccionado = dataRepository.getBolsinPorNumero(numeroBolsin);
        if (seleccionado == null) {
            return new ConsultaSeguimientoResponse("Bolsín no encontrado.");
        }

        enviarEmail();

        ConsultaSeguimientoResponse response = new ConsultaSeguimientoResponse();
        response.setMensaje("¿Desea notificar al GCM destino la ubicación del bolsín?");

        BolsinDTO dto = new BolsinDTO();
        dto.setNumeroBolsin(seleccionado.getNumeroBolsin());
        dto.setNumeroPrecinto(seleccionado.getNumeroPrecinto());
        if (seleccionado.obtenerCMDestino() != null) {
            dto.setCmDestinoNombre(seleccionado.obtenerCMDestino().getNombre());
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
        String fechaHoraActual = getFechaHoraActual();
        String resultado = llamarCUNotificarUbicacionDeBolsin(emailDestino, fechaHoraActual);

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

    private List<Bolsin> buscarBolsinesEnviados(String codigoCM) {
        return dataRepository.getBolsinesEnviadosPorCM(codigoCM);
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
        // Mensaje 30 del diagrama de secuencia.
        // En E1 no hay envío real — la integración con Outlook Microsoft Graph se implementa en E2.
    }

    private String llamarCUNotificarUbicacionDeBolsin(String emailDestino, String fechaHora) {
        // Mensaje 41 — <<include>> CU31 Notificar Ubicación de Bolsín. Simulado en E1.
        if (emailDestino != null) {
            return "Notificación preparada para: " + emailDestino + " | Fecha: " + fechaHora;
        }
        return "No se encontró email del GCM destino. Consulta finalizada. | Fecha: " + fechaHora;
    }

    private void finCU() {
        // Mensaje 44 del diagrama de secuencia. Finaliza el caso de uso.
        // En arquitectura REST stateless no hay estado local que limpiar.
    }

    private String buscarDireccionCorreo(int numeroBolsin) {
        Bolsin bolsin = dataRepository.getBolsinPorNumero(numeroBolsin);
        if (bolsin == null) return null;
        return bolsin.buscarDireccionCorreo();
    }

    private String getFechaHoraActual() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private List<BolsinDTO> armarBolsinDTOs(List<Bolsin> bolsines, List<BolsinLocalizacion> localizaciones) {
        List<BolsinDTO> dtos = new ArrayList<>();
        for (Bolsin b : bolsines) {
            BolsinDTO dto = new BolsinDTO();
            dto.setNumeroBolsin(b.getNumeroBolsin());
            dto.setNumeroPrecinto(b.getNumeroPrecinto());
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
