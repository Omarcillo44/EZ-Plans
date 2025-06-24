package com.ezsoftware.ezplans.ui.components

import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel

@Composable
fun DialogoAyuda(onClose: () -> Unit, content: @Composable () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text("Ayuda", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            content()
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text("Entendido")
            }
        }
    )
}

@Composable
fun DialogoTema(
    onClose: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val context: Context = LocalContext.current
    val preferenciaModoOscuro = remember { PreferenceHelper(context) }

    var estadoModoOscuro by remember { mutableStateOf(preferenciaModoOscuro.leerEstadoModoOscuro()) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text("Seleccionar tema", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Modo claro
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            themeViewModel.setDarkTheme(false)
                            estadoModoOscuro = false
                            preferenciaModoOscuro.guardarEstadoModoOscuro(false)
                        }
                        .padding(vertical = 6.dp)
                ) {
                    RadioButton(selected = !estadoModoOscuro, onClick = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Modo claro", style = MaterialTheme.typography.bodyLarge)
                }

                // Modo oscuro
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            themeViewModel.setDarkTheme(true)
                            estadoModoOscuro = true
                            preferenciaModoOscuro.guardarEstadoModoOscuro(true)
                        }
                        .padding(vertical = 6.dp)
                ) {
                    RadioButton(selected = estadoModoOscuro, onClick = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Modo oscuro", style = MaterialTheme.typography.bodyLarge)
                }

// Colores dinámicos (Android 12+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val nuevoValor = !themeViewModel.themeState.value.useDynamicColor
                                themeViewModel.setDynamicColor(nuevoValor)
                            }
                            .padding(vertical = 6.dp)
                    ) {
                        Checkbox(
                            checked = themeViewModel.themeState.value.useDynamicColor,
                            onCheckedChange = { nuevoValor ->
                                themeViewModel.setDynamicColor(nuevoValor)
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Colores dinámicos", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text("Aceptar", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
fun DialogoSiNo(
    mensaje: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Confirmación") },
        text = { Text(mensaje) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}


