package com.ezsoftware.ezplans.Controller;

import com.ezsoftware.ezplans.Infra.Security.TokenService;
import com.ezsoftware.ezplans.Model.DTO.Acceso.DatosAutenticacionUsuario;
import com.ezsoftware.ezplans.Model.DTO.Acceso.DatosJWTToken;
import com.ezsoftware.ezplans.Model.Entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> autenticarUsuario(@RequestBody DatosAutenticacionUsuario datos) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(datos.celularUsuario(), datos.pass().replace(" ", ""));
        System.out.println(datos.celularUsuario());
        System.out.println(datos.pass());
        var usuarioAutenticado = authenticationManager.authenticate(authToken);
        var jwtToken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
        System.out.println(jwtToken);
        return ResponseEntity.ok(new DatosJWTToken(jwtToken));
    }
}
