package com.ezsoftware.ezplans.ui.components

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.ezsoftware.ezplans.models.NuevaActividad.DatosMiembrosNuevaActividad
import com.ezsoftware.ezplans.models.NuevaActividad.DatosNuevaActividad
import com.ezsoftware.ezplans.models.NuevaActividad.Miembros.DatosUsuarioEnPlan
import com.ezsoftware.ezplans.viewmodel.MiembrosPlanViewModel
import com.ezsoftware.ezplans.viewmodel.NuevaActividadViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import androidx.activity.compose.BackHandler

fun BigDecimal.formatearParaUI(): String {
    // Si es un número entero, mostrar sin decimales
    return if (this.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
        this.toBigInteger().toString()
    } else {
        this.setScale(2, RoundingMode.HALF_UP).toPlainString()
    }
}

@Composable
fun CrearNuevaActividad(
    navControlador: NavController,
    themeViewModel: ThemeViewModel,
    miembrosPlanViewModel: MiembrosPlanViewModel,
    nuevaActividadViewModel: NuevaActividadViewModel,
    idPlan: Int
){
    BackHandler {
        navControlador.navigate("VistaDetalladaPlan/${idPlan}") {
            //popUpTo(0) { inclusive = true }
        }
    }

    var errorTitulo by rememberSaveable { mutableStateOf(false) }
    var titulo by rememberSaveable { mutableStateOf("") }
    var detalles by rememberSaveable { mutableStateOf("") }
    var usuariosSelect by rememberSaveable { mutableStateOf(listOf<Int>()) }
    var nombreUsuSelect by rememberSaveable { mutableStateOf(listOf<String>()) }
    var divisionIgual by rememberSaveable { mutableStateOf(true) }

    var selectedTab = rememberSaveable { mutableStateOf(0) }
    var datosMiembrosPlan by rememberSaveable { mutableStateOf(listOf<DatosUsuarioEnPlan>()) }

    var miembrosContribuciones by rememberSaveable {
        mutableStateOf(listOf<DatosMiembrosNuevaActividad>())
    }

    LaunchedEffect(datosMiembrosPlan) {
        if (datosMiembrosPlan.isNotEmpty() && usuariosSelect.isEmpty()) {
            usuariosSelect = datosMiembrosPlan.map { it.idUsuario }
            nombreUsuSelect = datosMiembrosPlan.map { "${it.nombreUsuario} ${it.apellidosUsuario}" }
        }
    }

    LaunchedEffect(usuariosSelect, datosMiembrosPlan) {
        if (usuariosSelect.isNotEmpty()) {
            // Si no hay contribuciones, crear nuevas
            if (miembrosContribuciones.isEmpty()) {
                miembrosContribuciones = usuariosSelect.map { idUsuario ->
                    DatosMiembrosNuevaActividad(
                        idUsuario = idUsuario,
                        aportacion = BigDecimal.ZERO,
                        idAcreedorDeuda = null,
                        montoDeuda = BigDecimal.ZERO,
                        montoCorrespondiente = BigDecimal.ZERO
                    )
                }
            } else {
                // Sincronizar cambios manteniendo datos existentes
                val usuariosActuales = miembrosContribuciones.map { it.idUsuario }.toSet()
                val usuariosNuevosSet = usuariosSelect.toSet()

                // Solo actualizar si hay cambios reales
                if (usuariosActuales != usuariosNuevosSet) {
                    val usuariosNuevos = usuariosNuevosSet - usuariosActuales

                    val contribucionesActualizadas = miembrosContribuciones
                        .filter { it.idUsuario in usuariosSelect } + // Mantener existentes
                            usuariosNuevos.map { idUsuario -> // Agregar nuevos
                                DatosMiembrosNuevaActividad(
                                    idUsuario = idUsuario,
                                    aportacion = BigDecimal.ZERO,
                                    idAcreedorDeuda = null,
                                    montoDeuda = BigDecimal.ZERO,
                                    montoCorrespondiente = BigDecimal.ZERO
                                )
                            }

                    miembrosContribuciones = contribucionesActualizadas
                }
            }
        }
    }

    LaunchedEffect(usuariosSelect, miembrosContribuciones) {
        if (miembrosContribuciones.isNotEmpty()) {
            val usuariosSelectSet = usuariosSelect.toSet()
            val contribucionesLimpias = miembrosContribuciones.map { contribucion ->
                // Si el acreedor ya no está en la lista de usuarios seleccionados, limpiarlo
                if (contribucion.idAcreedorDeuda != null &&
                    contribucion.idAcreedorDeuda !in usuariosSelectSet) {
                    contribucion.copy(
                        idAcreedorDeuda = null,
                        montoDeuda = BigDecimal.ZERO
                    )
                } else {
                    contribucion
                }
            }

            // Solo actualizar si hay cambios
            if (contribucionesLimpias != miembrosContribuciones) {
                miembrosContribuciones = contribucionesLimpias
            }
        }
    }

    val gastoTotal = miembrosContribuciones.sumOf { it.aportacion }
    val totalPagado = miembrosContribuciones.sumOf { it.aportacion }
    val totalDebePagar = if (gastoTotal == BigDecimal.ZERO)
        BigDecimal.ZERO
    else
        miembrosContribuciones.sumOf { it.montoCorrespondiente }

    LaunchedEffect(gastoTotal) {
        if (gastoTotal == BigDecimal.ZERO && miembrosContribuciones.isNotEmpty()) {
            val contribucionesLimpias = miembrosContribuciones.map {
                it.copy(
                    montoCorrespondiente = BigDecimal.ZERO,
                    idAcreedorDeuda = null,
                    montoDeuda = BigDecimal.ZERO
                )
            }
            miembrosContribuciones = contribucionesLimpias
        }
    }

    val diferencia = totalPagado.subtract(totalDebePagar).setScale(2, RoundingMode.HALF_UP)

    val epsilon = BigDecimal("0.01")
    val hayErrorDiferencia = diferencia.abs() > epsilon
    var mostrarError by remember { mutableStateOf(false) }
    var ultimoTiempoValidacion by remember { mutableStateOf(0L) }

    LaunchedEffect(totalPagado, totalDebePagar) {
        ultimoTiempoValidacion = System.currentTimeMillis()
        delay(500)

        if (System.currentTimeMillis() - ultimoTiempoValidacion >= 500) {
            val redondeadoPagado = totalPagado.setScale(2, RoundingMode.HALF_UP)
            val redondeadoDebe = totalDebePagar.setScale(2, RoundingMode.HALF_UP)
            mostrarError = redondeadoPagado != redondeadoDebe
        }
    }

    // Función para cargar datos
    fun cargarDatos() {
        idPlan.let {
            miembrosPlanViewModel.obtenerMiembrosPlan(
                idPlan = it,
                onSuccess = { datosMiembrosPlan = it },
                //onError = { errorMessage = it }
            )
        }
    }
    LaunchedEffect(Unit) {
        cargarDatos()
    }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {

            item { TituloNuevaActividad(navControlador, idPlan) }

            item {
                TabsNuevaActividad(
                    usuariosPlan = datosMiembrosPlan,
                    titulo = titulo,
                    errorTitulo = errorTitulo,
                    detalles = detalles,
                    usuariosSelect = usuariosSelect,
                    nombreUsuSelect = nombreUsuSelect,
                    divisionIgual = divisionIgual,
                    gastoTotal = gastoTotal,
                    selectedTab = selectedTab,
                    onTituloCambio = { titulo = it },
                    onDetalleCambio = { detalles = it },
                    onUsuariosSelectChange = { usuariosSelect = it },
                    onErrorTituloCambio = { errorTitulo = it },
                    onDivicionIgual = { divisionIgual = it },
                    onNombreUsuSelect = { nombreUsuSelect = it },
                    miembrosContribuciones = miembrosContribuciones,
                    onMiembrosContribucionesChange = { miembrosContribuciones = it },
                    diferencia = diferencia,
                    hayErrorDiferencia = hayErrorDiferencia,
                    mostrarError = mostrarError,
                    datosMiembrosPlan = datosMiembrosPlan,
                )
            }

            item {
                BotonesNuevaActividad(
                    navControlador = navControlador,
                    idPlan = idPlan,
                    titulo = titulo,
                    detalles = detalles,
                    usuariosSelect = usuariosSelect,
                    divicionIgual = divisionIgual,
                    selectedTab = selectedTab,
                    nombreUsuSelect = nombreUsuSelect,
                    onErrorTitulo = { errorTitulo = it },
                    miembrosContribuciones = miembrosContribuciones,
                    gastoTotal = gastoTotal,
                    datosMiembrosPlan = datosMiembrosPlan,
                    diferencia = diferencia,
                    hayErrorDiferencia = hayErrorDiferencia,
                    nuevaActividadViewModel = nuevaActividadViewModel
                )
            }
        }
    }

    MenuFab(
        navController = navControlador,
        themeViewModel = themeViewModel,
        menuConfig = NuevaActivMenuConfig()
    )
}

class NuevaActivMenuConfig : MenuConfiguration() {
    override fun getMenuOptions(
        navController: NavController,
        onClose: () -> Unit,
        parameters: Map<String, Any?>
    ): List<MenuOption> {
        return listOf(
            MenuOption(
                id = "ayuda",
                texto = "Ayuda",
                icono = Icons.Default.Info,
                onClick = { /* Se maneja en el componente principal */ }
            )
        )
    }

    override fun getHelpContent(): @Composable () -> Unit = {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                "Hora de repartir culpas. \n" +
                        "\nDefine qué se hizo, quién puso cuánto, y quién quedó debiendo.\n" +
                        "\nSi los números no cuadran, no insistas: no eres Hacienda.\n" +
                        "\nAsegúrate de que todo tenga sentido antes de continuar.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TituloNuevaActividad(
    navControlador: NavController,
    idPlan: Int
){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Titulo("Nueva Actividad")
            IconButton(
                onClick = { navControlador.navigate("VistaDetalladaPlan/${idPlan}") },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, "Cerrar")
            }
        }
        Texto("Completa la información para crear una nueva actividad", false)
    }
    HorizontalDivider(
        thickness = 2.dp,
        color =  MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .padding(vertical = 10.dp)
    )
}

@Composable
fun TabsNuevaActividad(
    usuariosPlan: List<DatosUsuarioEnPlan>,
    titulo: String,
    errorTitulo: Boolean,
    detalles: String,
    usuariosSelect: List<Int>,
    nombreUsuSelect: List<String>,
    divisionIgual: Boolean,
    gastoTotal: BigDecimal,
    selectedTab: MutableState<Int>,
    onTituloCambio: (String) -> Unit,
    onDetalleCambio: (String) -> Unit,
    onUsuariosSelectChange: (List<Int>) -> Unit,
    onErrorTituloCambio: (Boolean) -> Unit,
    onDivicionIgual: (Boolean) -> Unit,
    onNombreUsuSelect: (List<String>) -> Unit,
    miembrosContribuciones: List<DatosMiembrosNuevaActividad>,
    onMiembrosContribucionesChange: (List<DatosMiembrosNuevaActividad>) -> Unit,
    hayErrorDiferencia: Boolean,
    diferencia: BigDecimal,
    mostrarError: Boolean,
    datosMiembrosPlan: List<DatosUsuarioEnPlan>

    ) {
    val tabs = listOf("Información básica", "Participantes", "Contribuciones", "Resumen de deudas")

    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        val configuration = LocalConfiguration.current
        val esHorizontal = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if(esHorizontal){
            TabRow(
                selectedTabIndex = selectedTab.value,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab.value == index,
                        onClick = { selectedTab.value = index },
                        enabled = false,
                        text = { Text(text = title) }
                    )
                }
            }
        }else{
            ScrollableTabRow(
                selectedTabIndex = selectedTab.value,
                edgePadding = 0.dp,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab.value == index,
                        onClick = { selectedTab.value = index },
                        enabled = false,
                        text = { Text(text = title) }
                    )
                }
            }
        }
        when (selectedTab.value) {
            0 -> TabInfoBasicaActiv(
                titulo = titulo,
                errorTitulo = errorTitulo,
                detalles = detalles,
                onTituloCambio = { onTituloCambio(it) },
                onDetalleCambio = { onDetalleCambio(it) },
                onErrorTituloCambio = { onErrorTituloCambio(it) }
            )
            1 -> TabParticipantesActiv(
                usuariosPlan = usuariosPlan,
                usuariosSelect = usuariosSelect,
                onUsuariosSelectChange = onUsuariosSelectChange,
                divisionIgual = divisionIgual,
                onDivicionIgual = onDivicionIgual,
                nombreUsuSelect = nombreUsuSelect,
                onNombreUsuSelect = onNombreUsuSelect
            )
            2 -> TabContribuciones(
                usuariosSelect = usuariosSelect,
                nombreUsuSelect = nombreUsuSelect,
                divisionIgual = divisionIgual,
                miembrosContribuciones = miembrosContribuciones,
                onMiembrosContribucionesChange = onMiembrosContribucionesChange,
                gastoTotal = gastoTotal,
                todosLosUsuarios = usuariosPlan.map { "${it.nombreUsuario} ${it.apellidosUsuario}" },
                todosLosIds = usuariosPlan.map { it.idUsuario },
                hayErrorDiferencia = hayErrorDiferencia,
                diferencia = diferencia,
                mostrarError = mostrarError
            )
            3 -> TabResumenDeudas(
                titulo = titulo,
                gastoTotal = gastoTotal,
                usuariosSelect = usuariosSelect,
                miembrosContribuciones = miembrosContribuciones,
                datosMiembrosPlan = datosMiembrosPlan,
            )
        }
    }
}


@Composable
fun BotonesNuevaActividad(
    navControlador: NavController,
    idPlan: Int,
    titulo: String,
    detalles: String,
    usuariosSelect: List<Int>,
    divicionIgual: Boolean,
    selectedTab: MutableState<Int>,
    nombreUsuSelect: List<String>,
    onErrorTitulo: (Boolean) -> Unit,
    miembrosContribuciones: List<DatosMiembrosNuevaActividad>,
    gastoTotal: BigDecimal,
    datosMiembrosPlan: List<DatosUsuarioEnPlan>,
    diferencia: BigDecimal,
    hayErrorDiferencia: Boolean,
    nuevaActividadViewModel: NuevaActividadViewModel
) {
    HorizontalDivider(
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(vertical = 10.dp)
    )
    Spacer(modifier = Modifier.size(20.dp))

    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val textoBotonIzq = if (selectedTab.value == 0) "Cancelar" else "Anterior"
        val textoBotonDer = if (selectedTab.value == 3) "Crear Actividad" else "Continuar"

        var habilitado by rememberSaveable { mutableStateOf(true) }

        ButtonForms(
            texto = textoBotonIzq,
            habilitado = habilitado,
            onClick = {
                if (selectedTab.value != 0) {
                    selectedTab.value -= 1
                } else if (selectedTab.value == 0){
                    navControlador.navigate("VistaDetalladaPlan/${idPlan}")
                }
            }
        )
        Spacer(modifier = Modifier.size(20.dp))

        val showDialogo = remember {mutableStateOf(false)}
        val scope = rememberCoroutineScope()
        ButtonForms(
            texto = textoBotonDer,
            habilitado = habilitado,
            onClick = {
                when (selectedTab.value) {
                    0 -> {
                        if (titulo.isBlank()) {
                            Toast.makeText(context, "Debe ingresar un título", Toast.LENGTH_SHORT).show()
                            onErrorTitulo(true)
                        } else {
                            selectedTab.value += 1
                        }
                    }
                    1 -> {
                        if (usuariosSelect.isEmpty()) {
                            Toast.makeText(context, "Debe seleccionar al menos un participante", Toast.LENGTH_SHORT).show()
                        } else {
                            selectedTab.value += 1
                        }
                    }
                    2 -> {
                        if (gastoTotal.compareTo(BigDecimal.ZERO) <= 0) {
                            Toast.makeText(context, "Debe registrar al menos una cantidad pagada", Toast.LENGTH_SHORT).show()
                        } else if (hayErrorDiferencia){
                            Toast.makeText(context, "Hay errores con los montos", Toast.LENGTH_SHORT).show()
                        } else {
                            selectedTab.value += 1
                        }
                        println("=== DEBUG CONTRIBUCIONES ===")
                        println("Usuarios seleccionados: $usuariosSelect")
                        println("Nombres usuarios: $nombreUsuSelect")
                        println("Todos los miembros del plan: ${datosMiembrosPlan.map { "${it.idUsuario}: ${it.nombreUsuario} ${it.apellidosUsuario}" }}")
                        println("Miembros contribuciones: ${miembrosContribuciones.map { "ID: ${it.idUsuario}, Aportación: ${it.aportacion}, Debe: ${it.montoCorrespondiente}, Acreedor: ${it.idAcreedorDeuda}, Monto deuda: ${it.montoDeuda}" }}")
                        println("Gasto total: $gastoTotal")
                        println("Diferencia: $diferencia")
                        println("Hay error diferencia: $hayErrorDiferencia")
                        println("División igual: $divicionIgual")
                        println("Título: $titulo")
                        println("Detalles: $detalles")
                        println("Selected tab: ${selectedTab.value}")
                        println("=========================")
                    }
                    3 -> {
                        habilitado = false
                        showDialogo.value = true
                        scope.launch {
                            delay(2000)
                            // TODO: SE HACE el trabajo pesado xd
                            val datosNuevaActividad = DatosNuevaActividad(
                                idPlan = idPlan,
                                titulo = titulo.trim(),
                                montoTotal = gastoTotal,
                                detalles = detalles.trim(),
                                miembros = miembrosContribuciones
                            )
                            nuevaActividadViewModel.crearNuevaActividad(
                                datosActividad = datosNuevaActividad,
                                onSuccess = { mensaje ->

                                    Log.d("Actividad", "Éxito: $mensaje")
                                    // desspues de el proceso de crear el plan se regresa la ventana principal
                                    Toast.makeText(context, "Actividad creada correctamente", Toast.LENGTH_SHORT).show()
                                    navControlador.navigate("VistaDetalladaPlan/${idPlan}")
                                    showDialogo.value = false
                                },
                                onError = { error ->
                                    Log.e("Actividad", "Error: $error")
                                    Toast.makeText(context, "Actividad creada incorrectamente. Intentelo nuevamente.", Toast.LENGTH_SHORT).show()
                                    navControlador.navigate("VistaDetalladaPlan/${idPlan}")
                                    showDialogo.value = false
                                }
                            )
                            //habilitado = true
                        }
                    }
                }
            }
        )
        if (showDialogo.value) {
            AlertDialog(
                onDismissRequest = { /*TODO*/},
                title = { Text("Por favor espere...") },
                text = {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Creando Actividad")
                    }
                },
                confirmButton = { /*TODO*/ }
            )
        }
    }
}
