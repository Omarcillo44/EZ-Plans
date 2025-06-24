package com.ezsoftware.ezplans.Controller;

import com.ezsoftware.ezplans.Model.DTO.Dashboard.DatosDashboard;
import com.ezsoftware.ezplans.Repository.PlanesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/dashboard")
    public class ControladorDashboard {

        @Autowired
        private PlanesRepository planesRepository;

        @GetMapping
        public ResponseEntity<DatosDashboard> obtenerPlanesUsuario(
                @RequestParam Integer idUsuario,
                @RequestParam(required = false) Boolean soloCompletos,
                @RequestParam(required = false) Boolean esAdmin
        ) {
            var resumenUsuario = planesRepository.obtenResumenDashboardPorUsuario(idUsuario);
            var planes = planesRepository.obtenerPlanesPorUsuario(idUsuario, soloCompletos, esAdmin);
            return ResponseEntity.ok(new DatosDashboard(resumenUsuario, planes));
        }
    }
