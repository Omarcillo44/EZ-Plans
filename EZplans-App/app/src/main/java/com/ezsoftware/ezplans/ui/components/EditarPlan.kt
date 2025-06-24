package com.ezsoftware.ezplans.ui.components

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.models.planes.DatosEditarPlan
import com.ezsoftware.ezplans.models.planes.DatosVistaEditarPlan
import com.ezsoftware.ezplans.ui.components.NuevoPlan.TabInfoBasica
import com.ezsoftware.ezplans.viewmodel.EditarPlanViewModel
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel
import com.ezsoftware.ezplans.viewmodel.VistaEditarPlanViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EditarPlan(
    navControlador: NavController,
    themeViewModel: ThemeViewModel,
    editarPlanViewModel: EditarPlanViewModel,
    vistaEditarPlanViewModel: VistaEditarPlanViewModel,
    idPlan: Int
){
    BackHandler {
        navControlador.navigate("VistaDetalladaPlan/${idPlan}") {
            //popUpTo(0) { inclusive = true }
        }
    }

    var datosVistaDetallada by remember { mutableStateOf<DatosVistaEditarPlan?>(null) }

    var errorTitulo by rememberSaveable { mutableStateOf(false) }
    var titulo by rememberSaveable { mutableStateOf("") }
    var detalles by rememberSaveable { mutableStateOf("") }
    var errorFecha by rememberSaveable { mutableStateOf(false) }
    var fecha by rememberSaveable { mutableStateOf("") }

    titulo = datosVistaDetallada?.titulo ?: "Título nuevo"
    detalles = datosVistaDetallada?.detalles ?: "Detalles nuevos"
    fecha = datosVistaDetallada?.fecha ?: "2000-01-01"

    fun cargarDatos() {
        idPlan.let {
            vistaEditarPlanViewModel.obtenerDatosPlan(
                idPlan = it,
                onComplete = { datosVistaDetallada = it },
                //onError = { errorMessage = it }
            )
        }
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {

            item { TituloEditarPlan() }

            item {
                println(datosVistaDetallada)

                    TabInfoBasica(
                        titulo = titulo,
                        errorTitulo = errorTitulo,
                        fecha = fecha,
                        errorFecha = errorFecha,
                        detalles = detalles,
                        onTituloCambio = { titulo = it },
                        onFechaCambio = { fecha = it },
                        onDetalleCambio = { detalles = it },
                        onErrorTituloCambio = { errorTitulo = it },
                        onErrorFechaCambio = { errorFecha = it }
                    )

            }

            item {
                BotonesEditarPlan(
                    navControlador = navControlador,
                    editarPlanViewModel = editarPlanViewModel,
                    idPlan = idPlan,
                    titulo = titulo,
                    fecha = fecha,
                    detalles = detalles,
                    onErrorTituloCambio = { errorTitulo = it },
                    onErrorFechaCambio = { errorFecha = it }
                )
            }
        }
    }

    MenuFab(
        navController = navControlador,
        themeViewModel = themeViewModel,
        menuConfig = EditarPlanMenuConfig()
    )
}

class EditarPlanMenuConfig : MenuConfiguration() {
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
                "Sí, puedes cambiar el título, la fecha y los detalles del plan.\n"+
                        "\nPero no, no puedes tocar miembros ni deudas." +
                    "\n¿Por qué? Porque no somos valientes como para resolver ese caos.\n" +
                        "\n¿El botón flotante? Solo sirve para cambiar el tema y pedir esta joyita de ayuda.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TituloEditarPlan(){
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
            Titulo("Editar plan")
            /*
            IconButton(
                onClick = { navControlador.navigate("UIPrincipal") },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, "Cerrar")
            }*/
        }
        Texto("Actualiza los datos del plan.", false)
    }
    HorizontalDivider(
        thickness = 2.dp,
        color =  MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .padding(vertical = 10.dp)
    )
}

@Composable
fun BotonesEditarPlan(
    navControlador: NavController,
    editarPlanViewModel: EditarPlanViewModel,
    idPlan: Int,
    titulo: String,
    fecha: String,
    detalles: String,
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
        var habilitado by rememberSaveable { mutableStateOf(true) }

        ButtonForms(
            texto = "Cancelar",
            habilitado = habilitado,
            onClick = {
                navControlador.navigate("UIPrincipal")
            }
        )
        Spacer(modifier = Modifier.size(20.dp))

        val showDialogo = remember {mutableStateOf(false)}
        val scope = rememberCoroutineScope()
        ButtonForms(
            texto = "Actualizar",
            habilitado = habilitado,
            onClick = {
                if (titulo.isBlank()) {
                    Toast.makeText(context, "Debe ingresar un título", Toast.LENGTH_SHORT).show()
                    onErrorTituloCambio(true)
                } else if (fecha.isBlank()) {
                    Toast.makeText(context, "Debe seleccionar una fecha", Toast.LENGTH_SHORT).show()
                    onErrorFechaCambio(true)
                } else {
                    habilitado = false
                    showDialogo.value = true
                    scope.launch {
                        delay(2000)
                        // TODO: SE HACE el trabajo pesado xd
                        val datosEditarPlan = DatosEditarPlan(
                            idPlan = idPlan,
                            titulo = titulo.trim(),
                            detalles = detalles.trim(),
                            fecha = fecha
                        )
                        editarPlanViewModel.limpiarEstados()
                        editarPlanViewModel.actualizarPlan(
                            datosPlan = datosEditarPlan,
                            onComplete = { exito ->
                                if (exito) {
                                    Toast.makeText(context, "Plan actualizado correctamente", Toast.LENGTH_SHORT).show()
                                    navControlador.navigate("VistaDetalladaPlan/${idPlan}")
                                    showDialogo.value = false
                                } else {
                                    Toast.makeText(context, "Error al actualizar el plan", Toast.LENGTH_LONG).show()
                                    navControlador.navigate("VistaDetalladaPlan/${idPlan}")
                                    showDialogo.value = false
                                }
                            }
                        )
                        //habilitado = true
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