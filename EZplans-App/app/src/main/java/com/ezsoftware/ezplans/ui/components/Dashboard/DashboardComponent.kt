package com.ezsoftware.ezplans.ui.components.Dashboard

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.models.dashboard.datosDashboard
import com.ezsoftware.ezplans.models.dashboard.datosPlan
import com.ezsoftware.ezplans.models.dashboard.datosResumen
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import com.ezsoftware.ezplans.ui.components.MenuConfiguration
import com.ezsoftware.ezplans.ui.components.MenuFab
import com.ezsoftware.ezplans.ui.components.MenuOption
import com.ezsoftware.ezplans.ui.components.Titulo
import com.ezsoftware.ezplans.viewmodel.DashboardViewModel
import com.ezsoftware.ezplans.viewmodel.VistaDetalladaViewModel
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel

@Composable
fun DashboardComponent(
    navControlador: NavController,
    themeViewModel: ThemeViewModel,
    dashboardViewModel: DashboardViewModel,
    vistaDetalladaViewModel: VistaDetalladaViewModel) {
    val context = LocalContext.current

    // Recuperar datos desde el ViewModel
    val idUsuario = PreferenceHelper(context).leerIDUsuario()
    var datosDashboard by remember { mutableStateOf<datosDashboard?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var soloCompletos by remember { mutableStateOf<Boolean?>(null) }

    var mostrarAyuda by remember { mutableStateOf(false) }
    var mostrarTema by remember { mutableStateOf(false) }

    // Función para cargar datos
    fun cargarDatos() {
        idUsuario?.let {
            dashboardViewModel.obtenerDatosDashboard(
                idUsuario = it,
                soloCompletos = soloCompletos,
                esAdmin = null,
                onSuccess = { datosDashboard = it },
                onError = { errorMessage = it }
            )
        }
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    // Recargar cuando cambie el filtro
    LaunchedEffect(soloCompletos) {
        if (datosDashboard != null) { // Solo recargar si ya hay datos iniciales
            cargarDatos()
        }
    }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            item {
                Titulo("Resumen")
                Spacer(modifier = Modifier.size(5.dp))
            }

            item {
                errorMessage?.let {
                    Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
                }
            }

            datosDashboard?.let { datos ->
                item { SeccionResumen(datos.datosResumen) }
                item { Spacer(modifier = Modifier.height(25.dp)) }

                // Título "Planes" con combo
                item {
                    SeccionTituloConFiltro(
                        filtroSeleccionado = soloCompletos,
                        onFiltroChange = { soloCompletos = it }
                    )
                }

                item { Spacer(modifier = Modifier.height(15.dp)) }
                item { PlanesLista(datos.datosPlanes, vistaDetalladaViewModel, navControlador) }
            }
        }
    }

    MenuFab(
        navController = navControlador,
        themeViewModel = themeViewModel,
        menuConfig = DashboardMenuConfig()
    )
}

// Configuración para la pantalla principal
class DashboardMenuConfig : MenuConfiguration() {
    override fun getMenuOptions(
        navController: NavController,
        onClose: () -> Unit,
        parameters: Map<String, Any?>
    ): List<MenuOption> {
        return listOf(
            MenuOption(
                id = "ver_pagos",
                texto = "Ver pagos realizados",
                icono = Icons.Default.Payments,
                onClick = {
                    navController.navigate("VistaPago")
                    onClose()
                }
            ),
            MenuOption(
                id = "crear_plan",
                texto = "Crear nuevo plan",
                icono = Icons.Default.Add,
                onClick = {
                    navController.navigate("CrearNuevoPlan")
                    onClose()
                }
            ),
            MenuOption(
                id = "ayuda",
                texto = "Ayuda",
                icono = Icons.Default.Info,
                onClick = { /* Se maneja en el componente principal */ }
            )
        )
    }
    // mensaje de ayuda mostrado
    override fun getHelpContent(): @Composable () -> Unit = {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                "¿Confundido con tantos números? Tranquilo, este es solo tu resumen.\n" +
                        "\nAquí ves lo que debes, lo que te " +
                        "deben, y cuántos planes tienes.\n" +
                        "\nUsa los filtros si te quieres sentir organizado.\n" +
                        "\nPor cierto, ¿Ves ese botón flotante? Sirve para ver pagos, crear un nuevo plan, " +
                        "cambiar el tema y pedir esta gloriosa ayuda.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionTituloConFiltro(
    filtroSeleccionado: Boolean?,
    onFiltroChange: (Boolean?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val opciones = listOf(
        "Todos" to null,
        "Completos" to true,
        "Pendientes" to false
    )

    val opcionSeleccionada = opciones.find { it.second == filtroSeleccionado }?.first ?: "Todos"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Titulo("Planes")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = opcionSeleccionada,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .width(140.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { (texto, valor) ->
                    DropdownMenuItem(
                        text = { Text(texto) },
                        onClick = {
                            onFiltroChange(valor)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SeccionResumen(datosResumen: datosResumen) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResumenCard(
                titulo = "Planes administrados",
                cantidad = datosResumen.planesAdministrados.toString(),
                modifier = Modifier.weight(1f)
            )
            ResumenCard(
                titulo = "Planes participante",
                cantidad = datosResumen.planesMiembro.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResumenCard(
                titulo = "Deudas pendientes",
                cantidad = datosResumen.deudasPendientes,
                modifier = Modifier.weight(1f)
            )
            ResumenCard(
                titulo = "Por cobrar",
                cantidad = datosResumen.deudasPorCobrar,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
fun PlanesLista(
    planes: List<datosPlan>,
    vistaDetalladaViewModel: VistaDetalladaViewModel,
    navControlador: NavController
) {
    val context = LocalContext.current

    Column {
        planes.forEach { plan ->
            Box(
                modifier = Modifier.clickable {
                    // 1. Mostrar Toast (como ya lo tenías)
                    navControlador.navigate("VistaDetalladaPlan/${plan.idPlan}")

                    // 2. Nueva funcionalidad: Imprimir en consola
                    vistaDetalladaViewModel.obtenerDetallesPlan(plan.idPlan) { datos ->
                        println("═══════════════════════════════════")
                        println("DATOS DETALLADOS DEL PLAN ${plan.idPlan}")
                        println("ID: ${datos?.resumenPlan?.idPlan}")
                        println("Título: ${datos?.resumenPlan?.tituloPlan}")
                        println("Fecha: ${datos?.resumenPlan?.fechaPlan}")
                        println("Estado: ${datos?.resumenPlan?.estadoPlan}")
                        println("Avance: ${datos?.resumenPlan?.avancePlan}")
                        println("Gasto total: ${datos?.resumenPlan?.gastoPlan}")
                        println("Miembros: ${datos?.resumenPlan?.cantidadMiembrosPlan}")
                        println("Actividades completadas: ${datos?.resumenPlan?.actividadesCompletadasPlan}")
                        println("Deudas pendientes: ${datos?.resumenPlan?.cantidadDeudasPendientesPlan}")
                        println("\n🔄 Actividades:")
                        datos?.actividades?.forEach { actividad ->
                            println("   • ID: ${actividad.idActividad}")
                            println("   • Título: ${actividad.tituloActividad})")
                            println("   • Monto: ${actividad.montoActividad}")
                            println("   • Número de deudas: ${actividad.numeroDeudasPendientesActividad}")
                            println("   • Estado: ${actividad.estadoActividad}")
                        }
                        println("\n👤 Miembros con deudas:")
                        datos?.miembros?.forEach { miembro ->
                            println("   • ID: ${miembro.idMiembro}")
                            println("   • Nombre: ${miembro.nombreMiembro}")
                            println("   • Celular: ${miembro.celularMiembro}")
                            println("   • Monto de deuda: ${miembro.montoDeuda}")
                            println("   • Monto de aportación: ${miembro.montoAportacion}")
                            println("   • ¿Tiene deuda?: ${miembro.tieneDeuda}")
                        }
                        println("═══════════════════════════════════")
                    }
                }
            ) {
                PlanesCard(
                    titulo = plan.titulo,
                    fecha = plan.fecha,
                    gasto = plan.gastoTotal,
                    miembros = plan.numeroDeMiembros,
                    rol = plan.rolDelUsuario,
                    estado = plan.estadoDelPlan
                )
            }
            Spacer(modifier = Modifier.size(15.dp))
        }
    }
}