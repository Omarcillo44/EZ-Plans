package com.ezsoftware.ezplans.ui.components.VistaDetallada

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ezsoftware.ezplans.models.dashboard.datosResumen
import com.ezsoftware.ezplans.models.vistaDetallada.DatosActividadPlan
import com.ezsoftware.ezplans.models.vistaDetallada.DatosDeudasPorPlan
import com.ezsoftware.ezplans.models.vistaDetallada.DatosResumenMiembrosPlan
import com.ezsoftware.ezplans.ui.components.Imagen
import com.ezsoftware.ezplans.ui.components.Texto
import com.ezsoftware.ezplans.ui.components.TextoPeq

@Composable
fun TabActividades(
    datos: List<DatosActividadPlan>
) {
    val filas = datos.chunked(2) // se ordenan en pares

    Column (verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 12.dp)
    ){
        filas.forEach { fila ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min), // fuerza que todos los hijos tengan la altura mínima necesaria, igual para todos
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CardTabActividad(
                    titulo = fila[0].tituloActividad,
                    gasto = fila[0].montoActividad,
                    miembros = fila[0].numeroDeudasPendientesActividad,
                    estado = fila[0].estadoActividad,
                    modifier = Modifier.weight(1f).fillMaxHeight() // se estira para ocupar la altura del Row
                )
                if (fila.size > 1) {
                    CardTabActividad(
                        titulo = fila[1].tituloActividad,
                        gasto = fila[1].montoActividad,
                        miembros = fila[1].numeroDeudasPendientesActividad,
                        estado = fila[1].estadoActividad,
                        modifier = Modifier.weight(1f).fillMaxHeight() // igual
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

        }
    }
}

@Composable
fun TabMiembros(datos: List<DatosResumenMiembrosPlan>) {
    Column (verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 12.dp)
    ){
        datos.forEach { dato ->
            CardTabMiembros(nombre = dato.nombreMiembro,
                telefono = dato.celularMiembro,
                debe = dato.montoDeuda,
                haber = dato.montoAportacion
            )
        }
    }
}

@Composable
fun TabDeudas(
    navControlador: NavController,
    idPlan: Int,
    nombrePlan: String,
    datos: List<DatosDeudasPorPlan>
) {
    Column (verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 12.dp)
    ){
        datos.forEach{ dato ->
            CardTabDeudas(
                navControlador = navControlador,
                idPlan = idPlan,
                idDeuda = dato.idDeuda,
                monto = dato.montoDeuda,
                deudor = dato.nombreDeudor,
                acreedor = dato.nombreAcreedor,
                motivo = dato.tituloActividad,
                plan = nombrePlan
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardTabActividad(
    titulo: String,
    gasto: String,
    miembros: String,
    estado: String,
    modifier: Modifier = Modifier, // Parámetro para que no se suicide el ancho
) {
    Card(
        modifier = modifier // Usa el modifier recibido primero, sino cómo xdxdxd
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Texto(titulo, false)
            Spacer(modifier = Modifier.height(8.dp))
            Texto(gasto, true)
            Spacer(modifier = Modifier.size(10.dp))

            FlowRow( // si no caben en la misma fila los acomoda verticalmente
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
            ) {
                Row {
                    Imagen("miembros", 20)
                    Spacer(modifier = Modifier.size(5.dp))
                    TextoPeq(miembros)
                }
//                Row {
//                    Imagen(
//                        when (estado) {
//                            "Completa" -> "check"
//                            "Pendiente" -> "pendiente"
//                            else -> "placeholder"  // Opcional: valor por defecto
//                        },
//                        20
//                    )
//                    Spacer(modifier = Modifier.size(5.dp))
//                    TextoPeq(estado)
//                }
            }
        }
    }
}

@Composable
fun CardTabMiembros(
    nombre: String,
    telefono: String,
    debe: String,
    haber: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                //horizontalArrangement = Arrangement.SpaceBetween,
                //verticalAlignment = Alignment.CenterVertically
            ) {
                Imagen("miembro", 50)
                Spacer(modifier = Modifier.size(5.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Texto(nombre, true)
                    TextoPeq(telefono)
                }
                Box(modifier = Modifier
                    .fillMaxSize(),
                    contentAlignment = Alignment.TopEnd
                ){
                    Imagen(
                        when (debe) {
                            "$0" -> "check"
                            else -> "pendiente"  // Opcional: valor por defecto
                        },
                        20
                    )
                }
            }
            Spacer(modifier = Modifier.size(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row {
                    TextoPeq("Debe:")
                    Spacer(modifier = Modifier.size(10.dp))
                    Imagen("flecha_deuda", 20)
                    Spacer(modifier = Modifier.size(5.dp))
                    TextoPeq(debe)
                }
                Row {
                    TextoPeq("Aportó:")
                    Spacer(modifier = Modifier.size(10.dp))
                    Imagen("flecha_aportacion", 20)
                    Spacer(modifier = Modifier.size(5.dp))
                    TextoPeq(haber)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardTabDeudas(
    navControlador: NavController,
    idPlan: Int,
    idDeuda: Int,
    monto: String,
    deudor: String,
    acreedor: String,
    motivo: String,
    plan: String
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        TextoPeq(
                            deudor.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .joinToString(""),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    TextoPeq("debe a")
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        TextoPeq(
                            acreedor.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .joinToString(""),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Column {
                        Row {
                            Texto(deudor, false)
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                            Texto(acreedor, false)
                        }
                        Row {
                            TextoPeq("Por: ")
                            TextoPeq(motivo)
                        }
                    }
                }
            }
            Column (
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ){
                Row {
                    Texto(monto,true, MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    Button(
                        onClick = { navControlador.navigate("RegistrarPago/${idPlan}/${idDeuda}/${monto}/${deudor}/${acreedor}/${motivo}/${plan}") },
                        modifier = Modifier.wrapContentSize().height(35.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        //Imagen("placeholder", 20)
                        //Spacer(modifier = Modifier.size(5.dp))
                        Text("Registrar Pago", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}