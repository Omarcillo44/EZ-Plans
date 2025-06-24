package com.ezsoftware.ezplans.ui.components.NuevoPlan

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.ezsoftware.ezplans.models.NuevoPlan.DatosMiembrosNuevoPlan
import com.ezsoftware.ezplans.models.NuevoPlan.DatosNuevoPlan
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import com.ezsoftware.ezplans.preferences.UsuarioRegistrado
import com.ezsoftware.ezplans.preferences.obtenerUsuariosDefault
import com.ezsoftware.ezplans.ui.components.ButtonForms
import com.ezsoftware.ezplans.ui.components.MenuConfiguration
import com.ezsoftware.ezplans.ui.components.MenuFab
import com.ezsoftware.ezplans.ui.components.MenuOption
import com.ezsoftware.ezplans.ui.components.Texto
import com.ezsoftware.ezplans.ui.components.Titulo
import com.ezsoftware.ezplans.viewmodel.NuevoPlanViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CrearNuevoPlan(
    navControlador: NavController,
    themeViewModel: ThemeViewModel,
    nuevoPlanViewModel: NuevoPlanViewModel
){
    BackHandler {
        navControlador.navigate("UIPrincipal") {
            //popUpTo(0) { inclusive = true }
        }
    }
    val context = LocalContext.current
    val usuariosDefault = remember { obtenerUsuariosDefault(context) }
    val idUsuario = PreferenceHelper(context).leerIDUsuario()

    var errorTitulo by rememberSaveable { mutableStateOf(false) }
    var titulo by rememberSaveable { mutableStateOf("") }
    var detalles by rememberSaveable { mutableStateOf("") }
    var errorFecha by rememberSaveable { mutableStateOf(false) }
    var fecha by rememberSaveable { mutableStateOf("") }
    var usuariosSelect by rememberSaveable { mutableStateOf(listOf<Int>()) }

    var selectedTab = rememberSaveable { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {

            item { TituloNuevoPlan(navControlador) }

            item {
                TabsNuevoPlan(
                    usuariosDefault = usuariosDefault,
                    titulo = titulo,
                    errorTitulo = errorTitulo,
                    fecha = fecha,
                    errorFecha = errorFecha,
                    detalles = detalles,
                    usuariosSelect = usuariosSelect,
                    selectedTab = selectedTab,
                    onTituloCambio = { titulo = it },
                    onFechaCambio = { fecha = it },
                    onDetalleCambio = { detalles = it },
                    onUsuariosSelectChange = { usuariosSelect = it },
                    onErrorTituloCambio = { errorTitulo = it },
                    onErrorFechaCambio = { errorFecha = it },
                )
            }

            item {
                idUsuario?.let {
                    BotonesNuevoPlan(
                        navControlador = navControlador,
                        nuevoPlanViewModel = nuevoPlanViewModel,
                        idUsuario = it,
                        titulo = titulo,
                        fecha = fecha,
                        detalles = detalles,
                        usuariosSelect = usuariosSelect,
                        selectedTab = selectedTab,
                        onErrorTituloCambio = { errorTitulo = it },
                        onErrorFechaCambio = { errorFecha = it },
                    )
                }
            }
        }
    }

    MenuFab(
        navController = navControlador,
        themeViewModel = themeViewModel,
        menuConfig = NuevoPlanMenuConfig()
    )
}

class NuevoPlanMenuConfig : MenuConfiguration() {
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
                "Crea un nuevo plan.\n" +
                        "\nYa sabes: elige un título cool, ponle fecha (porque sí, eso importa), y si te sientes creativo, escribe detalles.\n" +
                        "\nLuego recluta a tus víctimas… digo, participantes.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TituloNuevoPlan(navControlador: NavController){
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
            Titulo("Nuevo plan")
            IconButton(
                onClick = { navControlador.navigate("UIPrincipal") },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, "Cerrar")
            }
        }
        Texto("Completa la información para crear un nuevo plan." +
                "Podrás añadir actividades después.", false)
    }
    HorizontalDivider(
        thickness = 2.dp,
        color =  MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .padding(vertical = 10.dp)
    )
}

@Composable
fun TabsNuevoPlan(
    usuariosDefault: List<UsuarioRegistrado>,
    titulo: String,
    errorTitulo: Boolean,
    fecha: String,
    errorFecha: Boolean,
    detalles: String,
    usuariosSelect: List<Int>,
    selectedTab: MutableState<Int>,
    onTituloCambio: (String) -> Unit,
    onFechaCambio: (String) -> Unit,
    onDetalleCambio: (String) -> Unit,
    onUsuariosSelectChange: (List<Int>) -> Unit,
    onErrorTituloCambio: (Boolean) -> Unit,
    onErrorFechaCambio: (Boolean) -> Unit
) {
    val tabs = listOf("Información básica", "Participantes")

    Column {
        TabRow(selectedTabIndex = selectedTab.value) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab.value == index,
                    onClick = { selectedTab.value = index },
                    enabled = false,
                    text = { Text(text = title) },
                )
            }
        }
        // Contenido para cada tab
        when (selectedTab.value) {
            0 -> TabInfoBasica(
                titulo = titulo,
                errorTitulo = errorTitulo,
                fecha = fecha,
                errorFecha = errorFecha,
                detalles = detalles,
                onTituloCambio = { onTituloCambio(it) },
                onFechaCambio = { onFechaCambio(it) },
                onDetalleCambio = { onDetalleCambio(it) },
                onErrorTituloCambio = { onErrorTituloCambio(it) },
                onErrorFechaCambio = { onErrorFechaCambio(it) }
            )
            1 -> TabParticipantes(
                usuariosDefault = usuariosDefault,
                unUariosSelect = usuariosSelect,
                onUsuariosSelectChange = onUsuariosSelectChange
            )
        }
    }
}

@Composable
fun BotonesNuevoPlan(
    navControlador: NavController,
    nuevoPlanViewModel: NuevoPlanViewModel,
    idUsuario: Int,
    titulo: String,
    fecha: String,
    detalles: String,
    usuariosSelect: List<Int>,
    selectedTab: MutableState<Int>,
    onErrorTituloCambio: (Boolean) -> Unit,
    onErrorFechaCambio: (Boolean) -> Unit
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
        val textoBotonIzq = if (selectedTab.value == 1) "Anterior" else "Cancelar"
        val textoBotonDer = if (selectedTab.value == 1) "Crear Plan" else "Continuar"


        var habilitado by rememberSaveable { mutableStateOf(true) }


        ButtonForms(
            texto = textoBotonIzq,
            habilitado = habilitado,
            onClick = {
                if (selectedTab.value == 1) { // tal de participantes (ultima)
                    selectedTab.value -= 1
                } else { // tab de informacion basica (primera)
                    navControlador.navigate("UIPrincipal")
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
                            onErrorTituloCambio(true)
                        } else if (fecha.isBlank()) {
                            Toast.makeText(context, "Debe seleccionar una fecha", Toast.LENGTH_SHORT).show()
                            onErrorFechaCambio(true)
                        } else {
                            selectedTab.value += 1
                        }
                    }
                    1 -> {
                        if (usuariosSelect.isEmpty()) {
                            Toast.makeText(context, "Debe seleccionar al menos un participante", Toast.LENGTH_SHORT).show()
                        } else {
                            habilitado = false
                            showDialogo.value = true
                            scope.launch {
                                delay(2000)
                                // TODO: SE HACE el trabajo pesado xd
                                val miembros = buildList {
                                    add(DatosMiembrosNuevoPlan(idUsuario = idUsuario, administrador = true)) // agregas al admin
                                    addAll(usuariosSelect.map { id ->
                                        DatosMiembrosNuevoPlan(idUsuario = id, administrador = false)
                                    })
                                }
                                val datosNuevoPlan = DatosNuevoPlan(
                                    titulo = titulo.trim(),
                                    fechaPlan = fecha,
                                    detallesPlan = detalles.trim(),
                                    miembros = miembros
                                )
                                nuevoPlanViewModel.limpiarEstados()
                                nuevoPlanViewModel.validarDatosPlan(datosNuevoPlan)
                                nuevoPlanViewModel.crearNuevoPlan(
                                    datosPlan = datosNuevoPlan,
                                    onSuccess = { mensaje ->
                                        Log.d("Plan", "Éxito: $mensaje")
                                        // desspues de el proceso de crear el plan se regresa la ventana principal
                                        Toast.makeText(context, "Plan creado correctamente", Toast.LENGTH_SHORT).show()
                                        navControlador.navigate("UIPrincipal")
                                        showDialogo.value = false
                                    },
                                    onError = { error ->
                                        Log.e("Plan", "Error: $error")
                                        Toast.makeText(context, "Plan creado incorrectamente. Intentelo nuevamente.", Toast.LENGTH_SHORT).show()
                                        navControlador.navigate("UIPrincipal")
                                        showDialogo.value = false
                                    }
                                )
                                //habilitado = true
                            }
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
                        Text("Creando Plan")
                    }
                },
                confirmButton = { /*TODO*/ }
            )
        }
    }
}
