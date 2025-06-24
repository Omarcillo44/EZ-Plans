package com.ezsoftware.ezplans.Controller;

import com.ezsoftware.ezplans.Model.DTO.NuevaActividad.DatosMiembrosNuevaActividad;
import com.ezsoftware.ezplans.Model.DTO.NuevaActividad.DatosNuevaActividad;
import com.ezsoftware.ezplans.Model.Entity.*;
import com.ezsoftware.ezplans.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/actividades")
public class ControladorActividades {

    @Autowired
    private PlanesRepository planesRepository;
    @Autowired
    private ActividadesRepository actividadesRepository;
    @Autowired
    private MiembrosPlanRepository miembrosPlanRepository;
    @Autowired
    private miembrosActividadRepository miembrosActividadRepository;
    @Autowired
    private DeudasRepository deudasRepository;

    @Transactional
    @PostMapping("/nueva_actividad")
    public ResponseEntity<String> crearActividad(@RequestBody DatosNuevaActividad datosActividad) {
        try {
            Plan plan = planesRepository.findById(datosActividad.idPlan())
                    .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

            // 1. Verificar miembros válidos del plan
            List<Integer> idsValidos = miembrosPlanRepository.findUsuariosPorPlan(plan.getId());

            for (DatosMiembrosNuevaActividad miembro : datosActividad.miembros()) {
                if (!idsValidos.contains(miembro.idUsuario())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("El usuario con ID " + miembro.idUsuario() + " no pertenece al plan.");
                }
            }

            // 2. Verificar que no haya IDs duplicados
            List<Integer> idsUsuarios = datosActividad.miembros().stream()
                    .map(DatosMiembrosNuevaActividad::idUsuario)
                    .toList();

            long total = idsUsuarios.size();
            long distintos = idsUsuarios.stream().distinct().count();

            if (total != distintos) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La lista de usuarios contiene IDs duplicados.");
            }

            // Crear y guardar la actividad
            Actividad nuevaActividad = new Actividad();
            nuevaActividad.setIdPlan(plan);
            nuevaActividad.setTituloActividad(datosActividad.titulo());
            nuevaActividad.setDetallesActividad(datosActividad.detalles());
            nuevaActividad.setEstadoActividad(false);

            actividadesRepository.save(nuevaActividad);

            // Guardar miembros
            for (DatosMiembrosNuevaActividad miembro : datosActividad.miembros()) {
                /*Datos del miembro en la actividad*/
                MiembrosActividad nuevoMiembroActividad = new MiembrosActividad();
                nuevoMiembroActividad.setIdActividad(nuevaActividad);
                nuevoMiembroActividad.setIdUsuario(miembro.idUsuario());
                nuevoMiembroActividad.setAportacion(miembro.aportacion());

                miembrosActividadRepository.save(nuevoMiembroActividad); //Se guardan los datos del miembro en la act.

                /*Datos de la deuda del miembro*/
                if (miembro.idAcreedorDeuda() != null) {
                    Deuda deudaActividad = new Deuda();
                    deudaActividad.setMontoDeuda(miembro.montoDeuda());
                    deudaActividad.setIdAcreedor(miembro.idAcreedorDeuda());
                    deudaActividad.setIdMiembrosactividad(nuevoMiembroActividad);

                    /*Validación del monto del miembro
                    * Lo que realmente le corresponde, quitando la parte que ya pagó, es igual a la deuda que tiene
                    * */
                    BigDecimal diferencia = miembro.montoCorrespondiente().subtract(nuevoMiembroActividad.getAportacion());

                    if (diferencia.compareTo(miembro.montoDeuda()) == 0) {
                        deudasRepository.save(deudaActividad);
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Los montos adeudados del usuario " + miembro.idUsuario()
                                        + " no coinciden con el total pagado");
                    }
                }
            }

            return ResponseEntity.ok("Actividad creada con ID: " + nuevaActividad.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la actividad: " + e.getMessage());
        }
    }

}
