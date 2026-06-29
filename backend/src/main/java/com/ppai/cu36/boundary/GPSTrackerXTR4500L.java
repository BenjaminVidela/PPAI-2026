package com.ppai.cu36.boundary;

import com.ppai.cu36.dto.BolsinLocalizacion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class GPSTrackerXTR4500L {

    private static final java.util.Map<Integer, double[]> COORDS_POR_BOLSIN = new java.util.HashMap<>() {{
        put(1001, new double[]{-32.9442, -60.6505});
        put(1002, new double[]{-33.3317, -60.2133});
        put(1003, new double[]{-33.2950, -66.3356});
        put(1004, new double[]{-31.4135, -64.1811});
    }};

    private static final double[][] RUTAS = {
        {-34.6037, -58.5500},
        {-34.9205, -58.2000},
        {-33.1232, -60.2070},
        {-32.9442, -60.6505},
        {-31.4135, -64.1811},
        {-32.8908, -68.8272},
    };

    private double latitud;
    private double longitud;
    private int numeroBolsin;
    private LocalDateTime fechaHoraUltimaUbicacion;

    public void getLocation() {
        double[] coords = coordenadasSimuladas(this.numeroBolsin);
        this.latitud = coords[0];
        this.longitud = coords[1];
        this.fechaHoraUltimaUbicacion = LocalDateTime.now().minusMinutes(new Random().nextInt(30));
    }

    private BolsinLocalizacion getBolsinLocation(int numeroBolsin, String codigoCMOrigen) {
        this.numeroBolsin = numeroBolsin;
        getLocation();

        BolsinLocalizacion loc = new BolsinLocalizacion();
        loc.setNumeroBolsin(this.numeroBolsin);
        loc.setLatitud(this.latitud);
        loc.setLongitud(this.longitud);
        loc.setFechaHoraActualizacion(this.fechaHoraUltimaUbicacion);

        return loc;
    }

    public List<BolsinLocalizacion> getBolsinLocation(List<Integer> numeros, String codigoCMOrigen) {
        List<BolsinLocalizacion> resultado = new ArrayList<>();
        for (int numero : numeros) {
            resultado.add(getBolsinLocation(numero, codigoCMOrigen));
        }
        return resultado;
    }

    private double[] coordenadasSimuladas(int numeroBolsin) {
        if (COORDS_POR_BOLSIN.containsKey(numeroBolsin)) {
            return COORDS_POR_BOLSIN.get(numeroBolsin);
        }
        return RUTAS[numeroBolsin % RUTAS.length];
    }
}
