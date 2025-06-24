package com.ezsoftware.ezplans.Controller;

import com.ezsoftware.ezplans.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class ControladorUsuario {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/datos")
    public ResponseEntity<?> obtenerUsuario(@RequestParam String celularUsuario) {
        
        var datosUsuario = usuarioRepository.obtenerDatosUsuario(celularUsuario);
        
        if (datosUsuario.isPresent()) {
            return ResponseEntity.ok(datosUsuario);
        }
        return ResponseEntity.notFound().build();
    }
}