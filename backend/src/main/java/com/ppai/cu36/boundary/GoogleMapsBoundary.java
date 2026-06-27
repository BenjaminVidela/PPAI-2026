package com.ppai.cu36.boundary;

import com.ppai.cu36.dto.BolsinLocalizacion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoogleMapsBoundary {

    public List<BolsinLocalizacion> solicitarPosicionBolsinesEnMapa(List<BolsinLocalizacion> localizaciones) {
        return localizaciones;
    }

}
