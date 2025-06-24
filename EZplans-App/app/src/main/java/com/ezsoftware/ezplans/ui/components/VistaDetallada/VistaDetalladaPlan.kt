package com.ezsoftware.ezplans.ui.components.VistaDetallada

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.models.vistaDetallada.DatosActividadPlan
import com.ezsoftware.ezplans.models.vistaDetallada.DatosDeudasPorPlan
import com.ezsoftware.ezplans.models.vistaDetallada.DatosResumenMiembrosPlan
import com.ezsoftware.ezplans.models.vistaDetallada.DatosResumenPlan
import com.ezsoftware.ezplans.models.vistaDetallada.DatosVistaDetalladaPlan
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import com.ezsoftware.ezplans.ui.components.Dashboard.ResumenCard
import com.ezsoftware.ezplans.ui.components.DialogoSiNo
import com.ezsoftware.ezplans.ui.components.Imagen
import com.ezsoftware.ezplans.ui.components.MenuConfiguration
import com.ezsoftware.ezplans.ui.components.MenuFab
import com.ezsoftware.ezplans.ui.components.MenuOption
import com.ezsoftware.ezplans.ui.components.SubTitulo
import com.ezsoftware.ezplans.ui.components.TextoPeq
import com.ezsoftware.ezplans.viewmodel.EliminarPlanViewModel
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel
import com.ezsoftware.ezplans.viewmodel.VistaDetalladaViewModel


@Composable
fun VistaDetalladaPlan(
    navControlador: NavController,
    themeViewModel: ThemeViewModel,
    vistaDetalladaViewModel: VistaDetalladaViewModel,
    eliminarPlanViewModel: EliminarPlanViewModel,
    idPlan: Int
){
    BackHandler {
        navControlador.navigate("UIPrincipal") {
            //popUpTo(0) { inclusive = true }
        }
    }

    val context = LocalContext.current
    val idUsuario = PreferenceHelper(context).leerIDUsuario()

    var datosVistaDetallada by remember { mutableStateOf<DatosVistaDetalladaPlan?>(null) }

    // Función para cargar datos
    fun cargarDatos() {
        idPlan.let {
            vistaDetalladaViewModel.obtenerDetallesPlan(
                idPlan = it,
                onSuccess = { datosVistaDetallada = it },
                //onError = { errorMessage = it }
            )
        }
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            datosVistaDetallada?.let { datos ->

                item { ResumenPlan(datos.resumenPlan) }

                item { TabsPlan(
                    navControlador = navControlador,
                    datos.resumenPlan.idPlan,
                    datos.resumenPlan.tituloPlan,
                    datos.actividades,
                    datos.miembros,
                    datos.deudas)
                }
            }
        }
    }

    MenuFab(
        navController = navControlador,
        themeViewModel = themeViewModel,
        menuConfig = VistaDetPlanMenuConfig(),
        parameters = mapOf(
            "planId" to idPlan,
            "userId" to idUsuario,
            "adminId" to (datosVistaDetallada?.resumenPlan?.idAdministrador ?: 0),
            "viewmodel" to eliminarPlanViewModel
        )
    )
}

class VistaDetPlanMenuConfig : MenuConfiguration() {
    private val mostrarDialogo = mutableStateOf(false)
    // Variables que se actualizarán al entrar a getMenuOptions
    private var planId: Int = 0
    private var navControlador: NavController? = null
    private var viewmodelEliminar: EliminarPlanViewModel? = null

    override fun getMenuOptions(
        navController: NavController,
        onClose: () -> Unit,
        parameters: Map<String, Any?>
    ): List<MenuOption> {

        this.planId = parameters["planId"] as? Int ?: 0
        val userId = parameters["userId"] as? Int ?: 0
        val adminId = parameters["adminId"] as? Int ?: 0
        this.viewmodelEliminar = parameters["viewmodel"] as? EliminarPlanViewModel
        this.navControlador = navController

        return if(userId == adminId) { // solo le aparece al admin
            listOf(
                MenuOption(
                    id = "añadir_actividad",
                    texto = "Añadir actividad",
                    icono = Icons.Default.Add,
                    onClick = {
                        navController.navigate("CrearNuevaActividad/${planId}")
                        onClose()
                    }
                ),
                MenuOption(
                    id = "eliminar_plan",
                    texto = "Eliminar plan",
                    icono = Icons.Default.Delete,
                    onClick = {
                        mostrarDialogo.value = true
                        onClose()
                    }
                ),
                MenuOption(
                    id = "editar_plan",
                    texto = "Editar plan",
                    icono = Icons.Default.Edit,
                    onClick = {
                        navController.navigate("EditarPlan/${planId}")
                        onClose()
                    }
                )
            )
        } else {
            emptyList()
        } + listOf(
            MenuOption(
                id = "ayuda",
                texto = "Ayuda",
                icono = Icons.Default.Info,
                onClick = { /* Se maneja en el componente principal */ }
            )
        )
    }
    override fun getConfirmDialog(): (@Composable () -> Unit)? {
        return if (mostrarDialogo.value) {
            {
                DialogoSiNo(
                    mensaje = "¿Estás seguro de eliminar el plan?",
                    onConfirmar = {

                        viewmodelEliminar?.eliminarPlan(planId)
                        mostrarDialogo.value = false
                        navControlador?.navigate("UIPrincipal")
                    },
                    onCancelar = {
                        mostrarDialogo.value = false
                    }
                )
            }
        } else null
    }
    // mensaje de ayuda mostrado
    override fun getHelpContent(): @Composable () -> Unit = {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                "Aquí está el chismecito completo del plan:\n" +
                        "\nquién hizo qué, cuánto se debe y si el plan sigue vivo o ya murió.\n" +
                        "\nSi eres admin, el botón flotante te deja añadir actividades, editar o eliminar el plan.\n" +
                        "\nSi no lo eres... solo puedes cambiar el tema y llorar con la ayuda.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ResumenPlan(resumenPlan: DatosResumenPlan){
    TituloVistaDet(
        titulo = resumenPlan.tituloPlan,
        fecha = resumenPlan.fechaPlan,
        estado = resumenPlan.estadoPlan
    )
    ProgressBar(
        avance = resumenPlan.avancePlan
    )
    ResumenDet(
        gasto = resumenPlan.gastoPlan,
        miembros = resumenPlan.cantidadMiembrosPlan,
        actividades = resumenPlan.actividadesCompletadasPlan,
        deudas = resumenPlan.cantidadDeudasPendientesPlan
    )
}

@Composable
fun TituloVistaDet(
    titulo: String,
    fecha: String,
    estado: String
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(top = 8.dp, bottom = 8.dp), // adapta la altura al maximo que ocupan los elementos hijos
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f)) {
            SubTitulo(titulo, true)
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Row {
                Imagen("calendario", 20)
                Spacer(modifier = Modifier.size(5.dp))
                TextoPeq(fecha)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row {
                Imagen(
                    when (estado) {
                        "Completo" -> "check"
                        "Pendiente" -> "pendiente"
                        else -> "placeholder"  // Opcional: valor por defecto
                    },
                    20
                )
                Spacer(modifier = Modifier.size(5.dp))
                TextoPeq(estado)
            }
        }
    }

    HorizontalDivider(
        thickness = 2.dp,
        color =  MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .padding(vertical = 10.dp)
    )
}

@Composable
fun ProgressBar(
    avance: Double
){
    SubTitulo("$avance% completado", false)
    Spacer(modifier = Modifier.height(8.dp))
    LinearProgressIndicator(
        progress = { avance.toFloat()/100f },
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp),
    )
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun ResumenDet(
    gasto: String,
    miembros: String,
    actividades: String,
    deudas: Int
){
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResumenCard(
                titulo = "Gasto Total",
                cantidad = gasto,
                modifier = Modifier.weight(1f)
            )
            ResumenCard(
                titulo = "Miembros",
                cantidad = miembros,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResumenCard(
                titulo = "Actividades",
                cantidad = actividades,
                modifier = Modifier.weight(1f)
            )
            ResumenCard(
                titulo = "Deudas pendientes",
                cantidad = deudas.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun TabsPlan(
    navControlador: NavController,
    idPlan: Int,
    nombrePlan: String,
    actividades: List<DatosActividadPlan>,
    miembros: List<DatosResumenMiembrosPlan>,
    deudas : List<DatosDeudasPorPlan>
) {
    val tabs = listOf("Actividades", "Miembros", "Deudas")
    val selectedTab = rememberSaveable { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab.value) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab.value == index,
                    onClick = { selectedTab.value = index },
                    text = { Text(text = title) },
                )
            }
        }
        // Contenido para cada tab
        when (selectedTab.value) {
            0 -> TabActividades(actividades)
            1 -> TabMiembros(miembros)
            2 -> TabDeudas(navControlador, idPlan, nombrePlan, deudas)
        }
    }
}
