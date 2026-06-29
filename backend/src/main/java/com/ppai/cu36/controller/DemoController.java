package com.ppai.cu36.controller;

import com.ppai.cu36.model.Sesion;
import com.ppai.cu36.model.Usuario;
import com.ppai.cu36.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "http://localhost:4200")
public class DemoController {

    @Autowired
    private DataRepository dataRepository;

    @GetMapping("/login/{username}")
    public ResponseEntity<String> cambiarUsuario(@PathVariable String username) {
        Usuario usuario = dataRepository.getUsuarioPorUsername(username);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado: " + username);
        }
        Sesion.iniciarSesion(usuario);
        String cm = usuario.obtenerEmpleado().obtenerCM().getNombre();
        return ResponseEntity.ok("Sesión: " + username + " | CM: " + cm);
    }
}
