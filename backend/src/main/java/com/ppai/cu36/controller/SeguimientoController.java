package com.ppai.cu36.controller;

import com.ppai.cu36.control.GestorSeguimiento;
import com.ppai.cu36.dto.ConsultaSeguimientoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/seguimiento")
@CrossOrigin(origins = "http://localhost:4200")
public class SeguimientoController {

    @Autowired
    private GestorSeguimiento gestorSeguimiento;

    @GetMapping("/iniciar")
    public ResponseEntity<ConsultaSeguimientoResponse> iniciarConsulta() {
        ConsultaSeguimientoResponse response = gestorSeguimiento.crearConsultaSeguimiento();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/seleccionar/{numeroBolsin}")
    public ResponseEntity<ConsultaSeguimientoResponse> seleccionarBolsin(
            @PathVariable int numeroBolsin) {
        ConsultaSeguimientoResponse response = gestorSeguimiento.tomarSeleccionBolsin(numeroBolsin);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirmar-email/{numeroBolsin}")
    public ResponseEntity<ConsultaSeguimientoResponse> confirmarEmail(
            @PathVariable int numeroBolsin,
            @RequestBody Map<String, Boolean> body) {
        boolean confirma = Boolean.TRUE.equals(body.get("confirma"));
        ConsultaSeguimientoResponse response = gestorSeguimiento.tomarConfirmacionEmail(numeroBolsin, confirma);
        return ResponseEntity.ok(response);
    }
}
