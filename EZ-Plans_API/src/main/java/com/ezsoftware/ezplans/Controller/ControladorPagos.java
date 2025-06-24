package com.ezsoftware.ezplans.Controller;

import com.ezsoftware.ezplans.Model.DTO.Pagos.DatosNuevoPago;
import com.ezsoftware.ezplans.Model.DTO.Pagos.DatosVistaPago;
import com.ezsoftware.ezplans.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/pagos")
public class ControladorPagos {

    @Autowired
    private PagosRepository repositorioPagos;

    @PostMapping("/nuevo_pago")
    public ResponseEntity<String> registrarPago(@RequestBody DatosNuevoPago datos) {
        repositorioPagos.insertarPago(
                datos.idDeuda(),
                datos.montoPago(),
                datos.formaPago(),
                datos.comprobantePago()
        );
        return ResponseEntity.ok("Pago registrado con éxito");
    }

    @GetMapping("/ver")
    public ResponseEntity<List<DatosVistaPago>> verPagosUsuario(@RequestParam Integer idUsuario) {
        try {
            List<DatosVistaPago> pagos = repositorioPagos.obtenerDatosVistaPago(idUsuario);

            if (pagos == null || pagos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            System.out.println("Error al obtener pagos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
