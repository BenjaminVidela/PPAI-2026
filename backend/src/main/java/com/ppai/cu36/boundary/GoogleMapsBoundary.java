package com.ppai.cu36.boundary;

import com.ppai.cu36.dto.BolsinLocalizacion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoogleMapsBoundary {

    private List<BolsinLocalizacion> coordenadasGPS;

    public List<BolsinLocalizacion> solicitarPosicionBolsinesEnMapa(List<BolsinLocalizacion> localizaciones) {
        this.coordenadasGPS = localizaciones;
        solicitarPosicionBolsines();
        return this.coordenadasGPS;
    }

    public void solicitarPosicionBolsines() {
    }
}
