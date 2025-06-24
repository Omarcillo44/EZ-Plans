package com.ezsoftware.ezplans.ui.components

import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel

// Clase de datos para definir opciones del menú
data class MenuOption(
    val id: String,
    val texto: String,
    val icono: ImageVector,
    val onClick: () -> Unit
)

// Clase abstracta para configurar diferentes tipos de menú
abstract class MenuConfiguration {
    open fun getConfirmDialog(): (@Composable () -> Unit)? = null

    abstract fun getMenuOptions(
        navController: NavController,
        onClose: () -> Unit,
        parameters: Map<String, Any?> = emptyMap()
    ): List<MenuOption>

    abstract fun getHelpContent(): @Composable () -> Unit

    // Método que siempre incluye la opción de tema
    fun getCompleteMenuOptions(
        navController: NavController,
        onClose: () -> Unit,
        onTemaClick: () -> Unit,
        parameters: Map<String, Any?> = emptyMap()
    ): List<MenuOption> {
        val customOptions = getMenuOptions(navController, onClose, parameters)
        val temaOption = MenuOption(
            id = "tema",
            texto = "Tema",
            icono = Icons.Default.Build,
            onClick = {
                onTemaClick()
                onClose()
            }
        )
        return customOptions + temaOption
    }
}

// Componente principal del menú FAB refactorizado
@Composable
fun MenuFab(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    menuConfig: MenuConfiguration,
    parameters: Map<String, Any?> = emptyMap()
) {
    var mostrarAyuda by remember { mutableStateOf(false) }
    var mostrarTema by remember { mutableStateOf(false) }

    MenuFabCirculo { onClose ->
        MenuOpciones(
            menuConfig = menuConfig,
            navController = navController,
            onClose = onClose,
            onAyudaClick = { mostrarAyuda = true },
            onTemaClick = { mostrarTema = true },
            parameters = parameters
        )
    }

    if (mostrarAyuda) {
        DialogoAyuda(
            onClose = { mostrarAyuda = false },
            content = menuConfig.getHelpContent()
        )
    }

    if (mostrarTema) {
        DialogoTema(
            onClose = { mostrarTema = false },
            themeViewModel = themeViewModel
        )
    }

    menuConfig.getConfirmDialog()?.let { dialogo ->
        dialogo() // lo llama como composable
    }
}

@Composable
fun MenuFabCirculo(
    content: @Composable (onClose: () -> Unit) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 90.dp, end = 35.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(horizontalAlignment = Alignment.End) {
            if (isExpanded) {
                content { isExpanded = false }
            }
            FloatingActionButton(
                onClick = { isExpanded = !isExpanded },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = if (isExpanded) "Cerrar menú" else "Abrir menú"
                )
            }
        }
    }
}

@Composable
fun MenuOpciones(
    menuConfig: MenuConfiguration,
    navController: NavController,
    onClose: () -> Unit,
    onAyudaClick: () -> Unit,
    onTemaClick: () -> Unit,
    parameters: Map<String, Any?> = emptyMap()
) {
    val opciones = menuConfig.getCompleteMenuOptions(navController, onClose, onTemaClick, parameters)

    Column {
        opciones.forEach { opcion ->
            OpcionMenu(
                texto = opcion.texto,
                icono = opcion.icono
            ) {
                if (opcion.id == "ayuda") {
                    onAyudaClick()
                    onClose()
                } else {
                    opcion.onClick()
                }
            }
        }
    }
}

@Composable
fun OpcionMenu(texto: String, icono: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = texto)
            Spacer(Modifier.width(8.dp))
            Text(texto)
        }
    }
}
