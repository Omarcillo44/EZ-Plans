package com.ezsoftware.ezplans.ui.components

import android.view.Menu
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.viewmodel.AutenticacionViewModel
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel

data class Credencial(
    val tipo: String,
    val celular: String,
    val password: String
)

@Composable
fun LoginScreen(viewModel: AutenticacionViewModel, onLoginSuccess: () -> Unit) {
    var celular by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var errorPass by rememberSaveable { mutableStateOf(false) }
    var errorCel by rememberSaveable { mutableStateOf(false) }
    var mostrarAyuda by remember { mutableStateOf(false) }
    var mostrarTema by remember { mutableStateOf(false) }
    var mostrarCredenciales by remember { mutableStateOf(true) }

    // Credenciales de prueba
    val credencialesTest = listOf(
        Credencial("Erick Huerta Valdepeña", "0123456789", "contraseña"),   // Erick Huerta Valdepeña
        Credencial("Omar", "5561397608", "contraseña"),  // Omar Lorenzo
        Credencial("Mauricio", "5535027625", "contraseña"),  // Mauricio Teodoro Rosales
        Credencial("Jimena", "5663516239", "contraseña"),  // Jimena Garrido Reyes
        Credencial("Administrador", "1234567890", "admin"),  // Juan el Caballo
        //Credencial("Tester", "5550430908", "contraseña"),  // Dulce Delgado Vázquez
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Titulo("¡Bienvenido!")
                Spacer(modifier = Modifier.size(15.dp))
                Texto("Coloca tus datos para poder ingresar", true)
            }
            Imagen("plan_torre", 80)
        }

        Spacer(modifier = Modifier.size(40.dp))
        OutlinedTextForms(
            valor = celular,
            label = "Celular",
            hayError = errorCel,
            ancho = 280.dp,
            tipoTeclado = KeyboardType.Phone,
            singleLine = true,
            onValorChange = {
                if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                    celular = it
                }
                errorCel = false
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextForms(
            valor = pass,
            label = "Contraseña",
            hayError = errorPass,
            ancho = 280.dp,
            singleLine = true,
            tipoTeclado = KeyboardType.Password,
            capitalizacion = KeyboardCapitalization.None,
            transfVisual = PasswordVisualTransformation(),
            onValorChange = {
                pass = it
                errorPass = false
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (celular.isBlank() || celular.length != 10) {
                errorCel = true
                error = null
            } else if (pass.isBlank()) {
                errorPass = true
                error = null
            } else {
                viewModel.login(celular, pass,
                    onSuccess = { onLoginSuccess() },
                    onError = { error = it }
                )
            }
        }) {
            Text("Iniciar sesión")
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        if (errorCel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Número de celular inválido",
                color = MaterialTheme.colorScheme.error,
            )
        }

        if (errorPass) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "La contraseña no puede estar vacía",
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (mostrarCredenciales) {
            CredencialesTest(
                credenciales = credencialesTest,
                onCredentialSelected = { credencial ->
                    celular = credencial.celular
                    pass = credencial.password
                }
            )
        }
    }
}

@Composable
fun CredencialesTest(
    credenciales: List<Credencial>,
    onCredentialSelected: (Credencial) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Credenciales guardadas",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (expanded) "Ocultar credenciales" else "Mostrar credenciales"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    credenciales.forEach { credencial ->
                        CredencialItem(
                            credencial = credencial,
                            onClick = { onCredentialSelected(credencial) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CredencialItem(
    credencial: Credencial,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = credencial.tipo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row {
                Text(
                    text = "Celular: ${credencial.celular}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Contraseña: ${credencial.password}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}