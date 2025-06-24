@file:Suppress("DEPRECATION")

package com.ezsoftware.ezplans.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ezsoftware.ezplans.models.NuevaActividad.DatosMiembrosNuevaActividad
import com.ezsoftware.ezplans.models.NuevaActividad.Miembros.DatosUsuarioEnPlan
import com.ezsoftware.ezplans.ui.components.NuevoPlan.CardUsuariosDisp
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

@Composable
fun TabInfoBasicaActiv(
    titulo: String,
    errorTitulo: Boolean,
    detalles: String,
    onTituloCambio: (String) -> Unit,
    onDetalleCambio: (String) -> Unit,
    onErrorTituloCambio: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 20.dp)
    ) {
        Titulo("Información Básica")
        TextoPeq("Información básica de la actividad")
        Spacer(modifier = Modifier.size(25.dp))

        SubTitulo("Titulo de la actividad", false)
        OutlinedTextForms(
            valor = titulo,
            label = "Plan sano",
            hayError = errorTitulo,
            singleLine = true,
            onValorChange = {
                onTituloCambio(it)
                onErrorTituloCambio(false)
            }
        )

        Spacer(modifier = Modifier.size(15.dp))
        SubTitulo("Detalles (Opcional)", false)
        OutlinedTextForms(
            valor = detalles,
            label = "Detalles sanos",
            alto = 120.dp,
            singleLine = false,
            maxLines = 5,
            onValorChange = {
                onDetalleCambio(it)
            }
        )

        Spacer(modifier = Modifier.size(18.dp))
        Row (horizontalArrangement = Arrangement.spacedBy(8.dp)){
            Icon(imageVector = Icons.Default.Info,
                "informacion",
                        modifier = Modifier.size(16.dp))
            TextoAviso("El gasto total se calculará automáticamente sumando las aportaciones de cada miembro en el siguiente paso")
        }
    }
}

@Composable
fun TabParticipantesActiv(
    usuariosPlan: List<DatosUsuarioEnPlan>,
    usuariosSelect: List<Int>,
    divisionIgual: Boolean,
    nombreUsuSelect: List<String>,
    onUsuariosSelectChange: (List<Int>) -> Unit,
    onDivicionIgual: (Boolean) -> Unit,
    onNombreUsuSelect: (List<String>) -> Unit
) {
    Column (
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 20.dp)
    ){
        Titulo("Participantes")
        TextoPeq("Selecciona quién partició en esta actividad")
        Spacer(modifier = Modifier.size(25.dp))

        SubTitulo("Seleccionar Participantes", false)
        Spacer(modifier = Modifier.size(10.dp))
        Column (
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 6.dp)
        ) {
            usuariosPlan.forEach { usuario ->
                CardUsuariosDisp(
                    nombre = "${usuario.nombreUsuario} ${usuario.apellidosUsuario}",
                    telefono = usuario.celularUsuario,
                    checked = usuario.idUsuario in usuariosSelect,
                    onCheckedChange = { checked ->
                        onUsuariosSelectChange(
                            if (checked) {
                                usuariosSelect + usuario.idUsuario
                            } else {
                                usuariosSelect - usuario.idUsuario
                            }
                        )
                        onNombreUsuSelect(
                            if (checked) {
                                nombreUsuSelect + "${usuario.nombreUsuario} ${usuario.apellidosUsuario}"
                            } else {
                                nombreUsuSelect - "${usuario.nombreUsuario} ${usuario.apellidosUsuario}"
                            }
                        )
                    }
                )
            }
        }
        Spacer(modifier = Modifier.size(18.dp))

        SubTitulo("Método de División", false)
        Spacer(modifier = Modifier.size(5.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(modifier = Modifier.weight(1f)){
                CardSeleccionMetodo(
                    texto = "División Igual",
                    descripcion = "Dividir entre todos por igual",
                    seleccionado = divisionIgual,
                    onClick = { onDivicionIgual(true) }
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Box(modifier = Modifier.weight(1f)){
                CardSeleccionMetodo(
                    texto = "Montos Personalizados",
                    descripcion = "Asignar montos personalizados",
                    seleccionado = !divisionIgual,
                    onClick = { onDivicionIgual(false) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TabContribuciones(
    usuariosSelect: List<Int>,
    nombreUsuSelect: List<String>,
    divisionIgual: Boolean,
    miembrosContribuciones: List<DatosMiembrosNuevaActividad>,
    onMiembrosContribucionesChange: (List<DatosMiembrosNuevaActividad>) -> Unit,
    gastoTotal: BigDecimal,
    todosLosUsuarios: List<String>,
    todosLosIds: List<Int>,
    hayErrorDiferencia: Boolean,
    diferencia: BigDecimal,
    mostrarError: Boolean
){
    LaunchedEffect(divisionIgual, gastoTotal, usuariosSelect.size) {
        if (divisionIgual && usuariosSelect.isNotEmpty() && gastoTotal > BigDecimal.ZERO) {
            val montoPorPersona = try {
                gastoTotal.divide(BigDecimal(usuariosSelect.size), MathContext.DECIMAL128)
            } catch (e: ArithmeticException) {
                gastoTotal.divide(BigDecimal(usuariosSelect.size), 10, RoundingMode.HALF_UP)
            }

            val nuevosmiembros = miembrosContribuciones.map { miembro ->
                miembro.copy(montoCorrespondiente = montoPorPersona)
            }
            onMiembrosContribucionesChange(nuevosmiembros)
        }
    }

    LaunchedEffect(miembrosContribuciones.map { "${it.idUsuario}-${it.aportacion}-${it.montoCorrespondiente}-${it.idAcreedorDeuda}" }.joinToString()) {
        val nuevosmiembros = miembrosContribuciones.map { miembro ->
            val diferencia = miembro.aportacion - miembro.montoCorrespondiente
            val nuevoMontoDeuda = if (diferencia < BigDecimal.ZERO && miembro.idAcreedorDeuda != null) {
                diferencia.abs()
            } else {
                BigDecimal.ZERO
            }

            if (miembro.montoDeuda != nuevoMontoDeuda) {
                miembro.copy(montoDeuda = nuevoMontoDeuda)
            } else {
                miembro
            }
        }

        if (nuevosmiembros != miembrosContribuciones) {
            onMiembrosContribucionesChange(nuevosmiembros)
        }
    }

    Column (
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 20.dp)
    ){
        Titulo("Contribuciones")
        TextoPeq("Define cuánto pagó cada persona")
        Spacer(modifier = Modifier.size(25.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row {
                SubTitulo("Contribuciones y Pagos", false)
            }
            Row {
                SubTitulo("Gasto Total: $${gastoTotal.formatearParaUI()}", true)
            }
        }

        if (mostrarError) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                Icon(imageVector = Icons.Default.Info, "informacion", tint = MaterialTheme.colorScheme.error)
                TextoPeq(
                    "Los montos adeudados no coinciden con el total pagado. Diferencia: ${diferencia.abs().formatearParaUI()}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.size(8.dp))
        Column (
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 6.dp)
        ) {
            miembrosContribuciones.forEachIndexed { index, miembro ->
                val nombreUsuario = nombreUsuSelect.getOrNull(index) ?: "Usuario ${miembro.idUsuario}"

                CardContribuciones(
                    miembro = miembro,
                    nombreUsuario = nombreUsuario,
                    divisionIgual = divisionIgual,
                    todosLosUsuarios = todosLosUsuarios,
                    idsUsuarios = todosLosIds,
                    onMiembroChange = { nuevoMiembro ->
                        val nuevaLista = miembrosContribuciones.toMutableList()
                        nuevaLista[index] = nuevoMiembro
                        onMiembrosContribucionesChange(nuevaLista)
                    }
                )
            }
        }
    }
}

@Composable
fun CardContribuciones(
    miembro: DatosMiembrosNuevaActividad,
    nombreUsuario: String,
    divisionIgual: Boolean,
    todosLosUsuarios: List<String>,
    idsUsuarios: List<Int>,
    onMiembroChange: (DatosMiembrosNuevaActividad) -> Unit
){
    // Calcula el estado de deuda con formateo
    val diferencia = miembro.aportacion - miembro.montoCorrespondiente
    val (textoDeuda, colorDeuda) = when {
        diferencia > BigDecimal.ZERO -> "Le deben $${diferencia.formatearParaUI()}" to Color(0xFF4CAF50)
        diferencia < BigDecimal.ZERO -> "Debe $${diferencia.abs().formatearParaUI()}" to Color(0xFFF44336)
        else -> "Sin deudas" to Color(0xFF9E9E9E)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Imagen("placeholder", 50)
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Texto(nombreUsuario)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colorDeuda.copy(alpha = 0.3f),
                        tonalElevation = 1.dp,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Box(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                            TextoPeq(textoDeuda)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(10.dp))

            Row(Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    TextoPeq("Cantidad pagada")

                    SelectorDeNumeros(
                        valor = miembro.aportacion,
                        rangoMin = BigDecimal.ZERO,
                        onValorCambiado = { nuevoValor ->
                            onMiembroChange(
                                miembro.copy(aportacion = nuevoValor)
                            )
                        }
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    TextoPeq("Debe pagar")

                    SelectorDeNumeros(
                        valor = miembro.montoCorrespondiente,
                        rangoMin = BigDecimal.ZERO,
                        enabled = !divisionIgual,
                        onValorCambiado = { nuevoValor ->
                            if (!divisionIgual) {
                                onMiembroChange(
                                    miembro.copy(montoCorrespondiente = nuevoValor)
                                )
                            }
                        }
                    )
                }
            }

            TextoPeq("¿A quién le debe?")
            // Determinar el índice seleccionado basado en idAcreedorDeuda
            val indiceAcreedorSeleccionado = miembro.idAcreedorDeuda?.let { idAcreedor ->
                idsUsuarios.indexOf(idAcreedor).takeIf { it >= 0 }
            }

            val dropActivado = diferencia < BigDecimal.ZERO
            SingleUserDropdown(
                dropActivado = dropActivado,
                opciones = todosLosUsuarios,
                seleccionado = indiceAcreedorSeleccionado,
                placeholder = "No aplica",
                onSeleccionCambio = { indiceSeleccionado ->
                    val nuevoIdAcreedor = indiceSeleccionado?.let { indice ->
                        val idSeleccionado = idsUsuarios[indice]
                        // Si el usuario se selecciona a sí mismo, guardar null
                        if (idSeleccionado == miembro.idUsuario) null else idSeleccionado
                    }

                    onMiembroChange(
                        miembro.copy(idAcreedorDeuda = nuevoIdAcreedor)
                    )
                }
            )
        }
    }
}

@Composable
fun TabResumenDeudas(
    titulo: String,
    gastoTotal: BigDecimal,
    usuariosSelect: List<Int>,
    miembrosContribuciones: List<DatosMiembrosNuevaActividad>,
    datosMiembrosPlan: List<DatosUsuarioEnPlan>,
){
    data class DeudaConNombres(
        val nombreDeudor: String,
        val nombreAcreedor: String,
        val deudaCantidad: String
    )

    fun crearListaDeudas(
        miembrosContribuciones: List<DatosMiembrosNuevaActividad>,
        datosMiembrosPlan: List<DatosUsuarioEnPlan>
    ): List<DeudaConNombres> {
        // Crear un mapa para buscar nombres por ID de usuario
        val mapaNombres = datosMiembrosPlan.associate { miembro ->
            miembro.idUsuario to "${miembro.nombreUsuario} ${miembro.apellidosUsuario}"
        }

        // Filtrar solo los que tienen idAcreedorDeuda diferente de null y montoDeuda > 0
        return miembrosContribuciones
            .filter { it.idAcreedorDeuda != null && it.montoDeuda > BigDecimal.ZERO }
            .mapNotNull { contribucion ->
                val nombreDeudor = mapaNombres[contribucion.idUsuario]
                val nombreAcreedor = mapaNombres[contribucion.idAcreedorDeuda]

                if (nombreDeudor != null && nombreAcreedor != null) {
                    DeudaConNombres(
                        nombreDeudor = nombreDeudor,
                        nombreAcreedor = nombreAcreedor,
                        deudaCantidad = "$${contribucion.montoDeuda.setScale(2, RoundingMode.HALF_UP).toString()}"
                    )
                } else null
            }
    }

    val participantes = usuariosSelect.size
    val deudasGeneradas = crearListaDeudas(miembrosContribuciones, datosMiembrosPlan)
    val cantidadDeudas = deudasGeneradas.size

    // Verificar si hay contribuciones configuradas
    val hayContribuciones = miembrosContribuciones.any { it.aportacion > BigDecimal.ZERO }

    Column (
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 20.dp)
    ){
        Titulo("Resumen de Deudas")
        TextoPeq("Deudas generadas de la actividad")
        Spacer(modifier = Modifier.size(20.dp))

        // Mostrar diferentes mensajes según el estado
        when {
            deudasGeneradas.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No hay deudas",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "No hay pendientes por saldar.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            else -> {
                Column (
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 6.dp)
                ) {
                    deudasGeneradas.forEach { deuda ->
                        CardResumenDeudas(
                            deudor = deuda.nombreDeudor,
                            acreedor = deuda.nombreAcreedor,
                            monto = deuda.deudaCantidad
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        CardResumenActividad(
            titulo = titulo,
            gastoTotal = gastoTotal,
            participantes = participantes,
            cantidadDeudas = cantidadDeudas
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardResumenDeudas(
    deudor: String,
    acreedor: String,
    monto: String
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                // Deudor
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        TextoPeq(
                            deudor.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString(""),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Column {
                        Texto(deudor)
                        TextoPeq("Deudor")
                    }
                }

                // Flecha y monto
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Texto("${monto}")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "debe a",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                val estaVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

                if(estaVertical){
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Texto(acreedor)
                                TextoPeq("Acreedor")
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary),
                                contentAlignment = Alignment.Center
                            ) {
                                TextoPeq(
                                    acreedor.split(" ")
                                        .mapNotNull { it.firstOrNull()?.toString() }
                                        .take(2)
                                        .joinToString(""),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }else{
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Texto(acreedor)
                            TextoPeq("Acreedor")
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            TextoPeq(
                                acreedor.split(" ")
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .take(2)
                                    .joinToString(""),
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardSeleccionMetodo(
    texto: String,
    descripcion: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textColor = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Imagen("placeholder", 25)
            Texto(texto, false, textColor, Modifier.fillMaxWidth(), TextAlign.Center)
            TextoPeq(descripcion, false,textColor, Modifier.fillMaxWidth(), TextAlign.Center)
        }
    }
}

@Composable
fun CardResumenActividad(
    titulo: String,
    gastoTotal: BigDecimal,
    participantes: Int,
    cantidadDeudas: Int
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SubTitulo("Reumen de la Actividad", true)
            Row {
                Column(modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Texto("Titulo")
                    Texto(titulo.trim(), true)
                }
                Column (modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)){
                    Texto("Gasto Total")
                    Texto("$${gastoTotal}", true)
                }
            }
            Row {
                Column (modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)){
                    Texto("Participantes")
                    if(participantes == 1)
                        Texto("${participantes} persona", true)
                    else
                        Texto("${participantes} personas", true)
                }
                Column (modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)){
                    Texto("Deudas generadas")
                    if(cantidadDeudas == 1)
                        Texto("${cantidadDeudas} transacción", true)
                    else
                        Texto("${cantidadDeudas} transacciones", true)
                }
            }
        }
    }
}
