package com.ezsoftware.ezplans.ui.components

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.models.Pagos.DatosNuevoPago
import com.ezsoftware.ezplans.viewmodel.NuevoPagoViewModel
import com.ezsoftware.ezplans.viewmodel.ThemeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun RegistrarPago(
    navControlador: NavController,
    themeViewModel: ThemeViewModel,
    nuevoPagoViewModel: NuevoPagoViewModel,
    idPlan: Int,
    idDeuda: Int,
    monto: String,
    deudor: String,
    acreedor: String,
    motivo: String,
    plan: String
){
    val deudaOriginal = monto.replace("$", "").replace(",", "").toDouble()
    var montoPago by rememberSaveable { mutableStateOf(BigDecimal.ZERO) }
    var seleccionFormaPago by rememberSaveable { mutableStateOf(true) }
    var imagenBase64 by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {

            item { TituloRegistrarPago() }

            item {
                ContextoDeuda(
                    monto = monto,
                    deudor = deudor,
                    acreedor = acreedor,
                    motivo = motivo,
                    plan = plan
                )
            }

            item {
                DetallesPago(
                    montoPago = montoPago,
                    onValorChange = { nuevoValor -> montoPago = nuevoValor },
                    seleccionFormaPago = seleccionFormaPago,
                    onSeleccionFormaPagoChange = { nuevaSeleccion -> seleccionFormaPago = nuevaSeleccion },
                    imagenBase64 = imagenBase64,
                    onImagenBase64Change = { nuevaImagen -> imagenBase64 = nuevaImagen }
                )
            }

            item {
                BotonesRegistrarPago(
                    navControlador = navControlador,
                    nuevoPagoViewModel = nuevoPagoViewModel,
                    idPlan = idPlan,
                    idDeuda = idDeuda,
                    montoPago = montoPago,
                    formaPago = seleccionFormaPago,
                    comprobantePago = imagenBase64,
                    deudaOriginal = deudaOriginal
                )
            }
        }
    }
    MenuFab(
        navController = navControlador,
        themeViewModel = themeViewModel,
        menuConfig = RegistrarPagoMenuConfig()
    )
}

class RegistrarPagoMenuConfig : MenuConfiguration() {
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
                "¿Te dignaste a pagar? Milagro (Espero no seas el administrador haciendo obras de caridad).\n" +
                        "\nRegistra cuánto se pagó, cómo se pagó (aunque sea con buenas intenciones), y si quieres, hasta puedes subir una fotito del comprobante.\n" +
                        "\nRecuerda: esto es solo un registro, no esperes que el dinero llegue solo.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DetallesPago(
    montoPago: BigDecimal,
    onValorChange: (BigDecimal) -> Unit,
    seleccionFormaPago: Boolean,
    onSeleccionFormaPagoChange: (Boolean) -> Unit,
    imagenBase64: String,
    onImagenBase64Change: (String) -> Unit
){
    var imagen by rememberSaveable { mutableStateOf<Bitmap?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
            )
            .padding(8.dp)
    ) {
        Column {
            SubTitulo("Detalles del pago", true)
            Spacer(modifier = Modifier.size(2.dp))
            Texto("Ingresa la información del pago que deseas registrar")
            Spacer(modifier = Modifier.size(20.dp))

            Texto("Monto del pago")
            SelectorDeNumeros(
                valor = montoPago,
                rangoMin = BigDecimal.ZERO,
                onValorCambiado = onValorChange,
                mostrarControlesLaterales = true
            )
            Spacer(modifier = Modifier.size(5.dp))

            HorizontalDivider(
                thickness = 2.dp,
                color =  MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            )

            Texto("Forma de pago")
            SelectorBoolean(
                tituloOpcion1 = "Efectivo",
                detalle1 = "Pago en efectivo",
                tituloOpcion2 = "Electrónico",
                detalle2 = "Transferencia, tarjeta, etc.",
                valorInicial = seleccionFormaPago,
                onCambio = onSeleccionFormaPagoChange
            )
            Spacer(modifier = Modifier.size(5.dp))

            HorizontalDivider(
                thickness = 2.dp,
                color =  MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            )

            TextoPeq("Comprobante de pago (Opcional)")
            Spacer(modifier = Modifier.size(8.dp))
            SelectorImagen(imagen) { nuevaImagen, base64 ->
                imagen = nuevaImagen
                onImagenBase64Change(base64)
                println(base64)
            }
            Spacer(modifier = Modifier.size(5.dp))

            Spacer(modifier = Modifier.size(18.dp))
            Row (horizontalArrangement = Arrangement.spacedBy(8.dp)){
                Icon(imageVector = Icons.Default.Info,
                    "informacion",
                    modifier = Modifier.size(16.dp))
                TextoAviso("Una vez registrado el pago, se actualizará automáticamente el saldo de la deuda." +
                        "Si el comprobante es requerido, asegúrate de subirlo antes de continuar.")
            }
        }
    }
}

@Composable
fun ContextoDeuda(
    monto: String,
    deudor: String,
    acreedor: String,
    motivo: String,
    plan: String
){
    Column {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Texto("Total de deuda: ")
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF44336).copy(alpha = 0.3f),
                tonalElevation = 1.dp,
                modifier = Modifier.padding(4.dp)
            ) {
                Box(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Texto(monto, true)
                }
            }
        }

        CardDeudorAcreedor(
            deudor = deudor,
            acreedor = acreedor
        )
        Spacer(modifier = Modifier.size(5.dp))

        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            Row (modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ){
                Imagen("placeholder", 20)
                Texto(motivo)
            }
            Row (modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ){
                Imagen("placeholder", 20)
                Texto(plan)
            }
        }
        Spacer(modifier = Modifier.size(25.dp))
    }
}

@Composable
fun TituloRegistrarPago(){
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
            Titulo("Registrar pago de deuda")
            /*IconButton(
                onClick = { navControlador.navigate("UIPrincipal") },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, "Cerrar")
            }*/
        }
        Texto("Registra un pago para liquidar esta deuda pendiente.", false)
    }
    HorizontalDivider(
        thickness = 2.dp,
        color =  MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .padding(vertical = 10.dp)
    )
}

@Composable
fun CardDeudorAcreedor(
    deudor: String,
    acreedor: String,
){
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        TextoPeq(
                            deudor.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString(""),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Column {
                        Texto(deudor)
                        TextoPeq("Deudor")
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "debe a",
                    tint = MaterialTheme.colorScheme.primary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Texto(acreedor)
                        TextoPeq("Acreedor")
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        TextoPeq(
                            acreedor.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString(""),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BotonesRegistrarPago(
    navControlador: NavController,
    nuevoPagoViewModel: NuevoPagoViewModel,
    idPlan: Int,
    idDeuda: Int,
    montoPago: BigDecimal,
    formaPago: Boolean,
    comprobantePago: String,
    deudaOriginal: Double
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

        val showDialogo = remember {mutableStateOf(false)}
        val scope = rememberCoroutineScope()
        ButtonForms(
            texto = "Registrar Pago",
            habilitado = habilitado,
            onClick = {
                if (montoPago.toDouble() > deudaOriginal) {
                    Toast.makeText(context, "No puedes registrar un pago mayor que tu deuda total", Toast.LENGTH_SHORT).show()
                }else if (montoPago <= BigDecimal.ZERO) {
                    Toast.makeText(context, "Debe registrar un monto válido", Toast.LENGTH_SHORT).show()
                }else {
                    println("Base a guardar: $comprobantePago")
                    val comprobantePagoFinal = if (comprobantePago.isBlank()) null else comprobantePago
                    habilitado = false
                    showDialogo.value = true
                    scope.launch {
                        delay(2000)
                        // TODO: SE HACE el trabajo pesado xd
                        val datosPagoDeuda = DatosNuevoPago(
                            idDeuda = idDeuda,
                            montoPago = montoPago,
                            formaPago = formaPago,
                            comprobantePago = comprobantePagoFinal
                        )
                        nuevoPagoViewModel.registrarNuevoPago(
                            datosPago = datosPagoDeuda,
                            onSuccess = { mensaje ->
                                Log.d("Actividad", "Éxito: $mensaje")
                                // desspues de el proceso de crear el plan se regresa la ventana principal
                                Toast.makeText(context, "Pago registrado correctamente", Toast.LENGTH_SHORT).show()
                                navControlador.navigate("VistaDetalladaPlan/${idPlan}")
                                showDialogo.value = false
                            },
                            onError = { error ->
                                Log.e("Actividad", "Error: $error")
                                Toast.makeText(context, "Pago registrado incorrectamente. Intentelo nuevamente.", Toast.LENGTH_SHORT).show()
                                navControlador.navigate("VistaDetalladaPlan/${idPlan}")
                                showDialogo.value = false
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
                        Text("Registrando Pago")
                    }
                },
                confirmButton = { /*TODO*/ }
            )
        }
    }
}
