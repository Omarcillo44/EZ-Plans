package com.ezsoftware.ezplans

import com.ezsoftware.ezplans.ui.components.Dashboard.DashboardComponent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import com.ezsoftware.ezplans.ui.components.CrearNuevaActividad
import com.ezsoftware.ezplans.ui.components.NuevoPlan.CrearNuevoPlan
import com.ezsoftware.ezplans.ui.components.EditarPlan
import com.ezsoftware.ezplans.ui.components.LoginScreen
import com.ezsoftware.ezplans.viewmodel.PagosViewModel
import com.ezsoftware.ezplans.ui.components.RegistrarPago
import com.ezsoftware.ezplans.ui.components.VistaDetallada.VistaDetalladaPlan
import com.ezsoftware.ezplans.ui.components.VistaPago
import com.ezsoftware.ezplans.ui.theme.EZplansTheme
import com.ezsoftware.ezplans.viewmodel.AutenticacionViewModel
import com.ezsoftware.ezplans.viewmodel.DashboardViewModel
import com.ezsoftware.ezplans.viewmodel.EditarPlanViewModel
import com.ezsoftware.ezplans.viewmodel.EliminarPlanViewModel
import com.ezsoftware.ezplans.viewmodel.MiembrosPlanViewModel
import com.ezsoftware.ezplans.viewmodel.NuevaActividadViewModel
import com.ezsoftware.ezplans.viewmodel.NuevoPagoViewModel
import com.ezsoftware.ezplans.viewmodel.NuevoPlanViewModel
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel
import com.ezsoftware.ezplans.viewmodel.VistaDetalladaViewModel
import com.ezsoftware.ezplans.viewmodel.VistaEditarPlanViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        val prefs = PreferenceHelper(applicationContext)
        val isDarkTheme = prefs.leerEstadoModoOscuro()

        val themeViewModel: ThemeViewModel by viewModels()

        themeViewModel.setDarkTheme(isDarkTheme)

        setContent {
            val currentThemeState = themeViewModel.themeState.value

            EZplansTheme (themeState = currentThemeState){
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //AppNavegacion(themeViewModel)
                    AppRoot(themeViewModel)
                }
            }
        }
    }
}

@Composable
fun AppRoot(themeViewModel: ThemeViewModel) {
    val viewModel: AutenticacionViewModel = viewModel()
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }

    if (isLoggedIn) {
        AppNavegacion(themeViewModel)
    } else {
        LoginScreen(viewModel) { isLoggedIn = true }
    }
}

@Composable
fun AppNavegacion(themeViewModel: ThemeViewModel) {
    val navControlador = rememberNavController()
    val dashboardViewModel: DashboardViewModel = viewModel()
    val vistaDetalladaViewModel: VistaDetalladaViewModel = viewModel()
    val nuevoPlanViewModel: NuevoPlanViewModel = viewModel()
    val eliminarPlanViewModel: EliminarPlanViewModel = viewModel()
    val miembrosPlanViewModel: MiembrosPlanViewModel = viewModel()
    val nuevaActividadViewModel: NuevaActividadViewModel = viewModel()
    val editarPlanViewModel: EditarPlanViewModel = viewModel()
    val vistaEditarPlanViewModel: VistaEditarPlanViewModel = viewModel()
    val nuevoPagoViewModel: NuevoPagoViewModel = viewModel()
    val pagosViewModel: PagosViewModel = viewModel()

    /*Avísame mañana que hay que cambiar el start destination, ya iba a matar toda esta parte*/
    NavHost(navController = navControlador, startDestination = "UIPrincipal") {
        composable("UIPrincipal") {
            DashboardComponent(navControlador, themeViewModel, dashboardViewModel, vistaDetalladaViewModel)
        }
        composable("VistaDetalladaPlan/{idPlan}") { backStackEntry ->
            val idPlan = backStackEntry.arguments?.getString("idPlan")?.toIntOrNull() ?: -1
            VistaDetalladaPlan(navControlador, themeViewModel, vistaDetalladaViewModel, eliminarPlanViewModel, idPlan)
        }
        composable("CrearNuevoPlan") { backStackEntry ->
            CrearNuevoPlan(navControlador, themeViewModel, nuevoPlanViewModel)
        }
        composable("CrearNuevaActividad/{idPlan}") { backStackEntry ->
            val idPlan = backStackEntry.arguments?.getString("idPlan")?.toIntOrNull() ?: -1
            CrearNuevaActividad(navControlador, themeViewModel, miembrosPlanViewModel, nuevaActividadViewModel, idPlan)
        }
        composable("EditarPlan/{idPlan}") { backStackEntry ->
            val idPlan = backStackEntry.arguments?.getString("idPlan")?.toIntOrNull() ?: -1
            EditarPlan(navControlador, themeViewModel, editarPlanViewModel, vistaEditarPlanViewModel, idPlan)
        }
        composable("RegistrarPago/{idPlan}/{idDeuda}/{monto}/{deudor}/{acreedor}/{motivo}/{plan}") { backStackEntry ->
            val idPlan = backStackEntry.arguments?.getString("idPlan")?.toIntOrNull() ?: -1
            val idDeuda = backStackEntry.arguments?.getString("idDeuda")?.toIntOrNull() ?: -1
            val deudor = backStackEntry.arguments?.getString("deudor")?.takeIf { it.isNotBlank() } ?: "desconocido"
            val acreedor = backStackEntry.arguments?.getString("acreedor")?.takeIf { it.isNotBlank() } ?: "desconocido"
            val motivo = backStackEntry.arguments?.getString("motivo")?.takeIf { it.isNotBlank() } ?: "desconocido"
            val monto = backStackEntry.arguments?.getString("monto")?.takeIf { it.isNotBlank() } ?: "desconocido"
            val plan = backStackEntry.arguments?.getString("plan")?.takeIf { it.isNotBlank() } ?: "desconocido"
            RegistrarPago(navControlador, themeViewModel, nuevoPagoViewModel, idPlan, idDeuda, monto, deudor, acreedor, motivo, plan)
        }
        composable("VistaPago") { backStackEntry ->
            VistaPago(navControlador, themeViewModel, pagosViewModel)
        }
    }
}