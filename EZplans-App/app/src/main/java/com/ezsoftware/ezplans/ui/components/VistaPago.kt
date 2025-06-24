package com.ezsoftware.ezplans.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.models.DatosVistaPago
import com.ezsoftware.ezplans.preferences.PreferenceHelper
import com.ezsoftware.ezplans.viewmodel.PagosViewModel
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel

@Composable
fun VistaPago(
    navControlador: NavController,
    themeViewModel: ThemeViewModel,
    pagosViewModel: PagosViewModel
) {
    var imagenParaMostrar by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val idUsuario = PreferenceHelper(context).leerIDUsuario()

    var datosVistaPago by rememberSaveable { mutableStateOf<List<DatosVistaPago>>(emptyList()) }

    // Función para cargar datos
    fun cargarDatos() {
        idUsuario?.let {
            pagosViewModel.obtenerPagosUsuario(
                idUsuario = idUsuario,
            ) { exito ->
                if (exito) {
                    datosVistaPago = pagosViewModel.pagos.value
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            datosVistaPago.let { pagos ->

                item { TituloVistaPago() }

                item {
                    Column {
                        if (pagos.isNotEmpty()) {
                            pagos.forEach { pago ->
                                CardVistaPagos(
                                    pago = pago,
                                    onComprobanteClick = { base64Image ->
                                        imagenParaMostrar = base64Image
                                    }
                                )
                                imagenParaMostrar?.let { imagen ->
                                    VisorImagen(
                                        base64Image = imagen,
                                        onDismiss = { imagenParaMostrar = null }
                                    )
                                }
                            }
                        }else {
                            CardSinPagos()
                        }
                    }
                }
            }
        }
    }

    MenuFab(
        navController = navControlador,
        themeViewModel = themeViewModel,
        menuConfig = VistaPagoMenuConfig()
    )
}

class VistaPagoMenuConfig : MenuConfiguration() {
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
                "Aquí ves a quién le pagaste, cuánto soltaste y lo que aún debes (porque claro, nunca es suficiente). También puedes ver la actividad y el plan, y si hay comprobante, tócalo para agrandarlo... como tu deuda.\n" +
                        "El botón flotante, fiel como siempre: cambiar tema y revivir esta épica ayuda.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardVistaPagos(
    pago: DatosVistaPago,
    onComprobanteClick: (String) -> Unit = {}
) {
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
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Texto(
                            pago.nombreAcreedor.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString(""),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.size(15.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Texto(pago.nombreAcreedor)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    TextoPeq("Pagado")
                                    Texto(pago.montoPago, true)
                                }
                            }
                            Row {
                                Column {
                                    TextoPeq("Restante")
                                    Texto(pago.montoRestanteDeuda, true)
                                }
                            }
                        }
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
                ) {
                    Row {
                        Column {
                            TextoPeq("Plan")
                            Texto(pago.tituloPlan)
                        }
                    }
                    Row {
                        Column {
                            TextoPeq("Actividad")
                            Texto(pago.tituloActividad)
                        }
                    }
                }
            }

            // Comprobante de pago (si existe)
            pago.comprobantePago?.let { comprobante ->
                Column(
                    modifier = Modifier
                        .width(150.dp) // Ancho fijo para el área del comprobante
                        .padding(16.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //TextoAviso("Comprobante")
                    //Spacer(modifier = Modifier.height(4.dp))

                    val bitmap = remember(pago.comprobantePago) {
                        base64ToBitmap(pago.comprobantePago)
                    }

                    bitmap?.let { bmp ->
                        Card(
                            modifier = Modifier
                                .size(120.dp, 120.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    onComprobanteClick(comprobante)
                                },
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Comprobante de pago",
                                modifier = Modifier.fillMaxSize(), // SIN .clickable aquí
                                contentScale = ContentScale.Crop
                            )
                        }
                    } ?: run {
                        Card(
                            modifier = Modifier
                                .size(100.dp, 80.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                TextoPeq(
                                    "Error al cargar imagen",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TituloVistaPago(){
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
            Titulo("Historial de pagos realizados")
            /*IconButton(
                onClick = { navControlador.navigate("UIPrincipal") },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, "Cerrar")
            }*/
        }
        Texto("Revisa los pagos que has registrado para saldar tus deudas.", false)
    }
    HorizontalDivider(
        thickness = 2.dp,
        color =  MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .padding(vertical = 10.dp)
    )
}

@Composable
fun CardSinPagos() {
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
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No hay pagos",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Todavía no has registrado ningún pago.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
