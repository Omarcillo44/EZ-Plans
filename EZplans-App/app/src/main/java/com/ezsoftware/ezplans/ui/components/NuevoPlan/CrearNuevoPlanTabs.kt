package com.ezsoftware.ezplans.ui.components.NuevoPlan

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ezsoftware.ezplans.preferences.UsuarioRegistrado
import com.ezsoftware.ezplans.ui.components.DatePickerField
import com.ezsoftware.ezplans.ui.components.Imagen
import com.ezsoftware.ezplans.ui.components.OutlinedTextForms
import com.ezsoftware.ezplans.ui.components.SubTitulo
import com.ezsoftware.ezplans.ui.components.Texto
import com.ezsoftware.ezplans.ui.components.TextoPeq
import com.ezsoftware.ezplans.ui.components.Titulo

@Composable
fun TabInfoBasica(
    titulo: String,
    errorTitulo: Boolean,
    fecha: String,
    errorFecha: Boolean,
    detalles: String,
    onTituloCambio: (String) -> Unit,
    onFechaCambio: (String) -> Unit,
    onDetalleCambio: (String) -> Unit,
    onErrorTituloCambio: (Boolean) -> Unit,
    onErrorFechaCambio: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 20.dp)
    ) {
        Titulo("Información Básica")
        TextoPeq("Información básica del plan")
        Spacer(modifier = Modifier.size(25.dp))

        SubTitulo("Título del plan", false)
        OutlinedTextForms(
            valor = titulo,
            label = "Ej: Viaje a la playa",
            hayError = errorTitulo,
            singleLine = true,
            onValorChange = {
                onTituloCambio(it)
                onErrorTituloCambio(false)
            }
        )

        Spacer(modifier = Modifier.size(15.dp))
        SubTitulo("Fecha", false)
        DatePickerField(
            fechaTexto = fecha,
            hayError = errorFecha,
            onDateSelected = {
                onFechaCambio(it)
                onErrorFechaCambio(false)
            }
        )

        Spacer(modifier = Modifier.size(15.dp))
        SubTitulo("Detalles (Opcional)", false)
        OutlinedTextForms(
            valor = detalles,
            label = "",
            alto = 120.dp,
            singleLine = false,
            maxLines = 5,
            //hayError = errorDetalles,
            onValorChange = {
                onDetalleCambio(it)
                //errorDetalles = false
            }
        )
    }
}

@Composable
fun TabParticipantes(
    usuariosDefault: List<UsuarioRegistrado>,
    unUariosSelect: List<Int>,
    onUsuariosSelectChange: (List<Int>) -> Unit
) {
    Column (
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 20.dp)
    ){
        Titulo("Participantes")
        TextoPeq("Selecciona los participantes de este plan")
        Spacer(modifier = Modifier.size(25.dp))

        SubTitulo("Seleccionar Participantes", false)
        Spacer(modifier = Modifier.size(10.dp))
        Column (
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 6.dp)
        ) {
            usuariosDefault.forEach { usuario ->
                CardUsuariosDisp(
                    nombre = "${usuario.nombre_usuario} ${usuario.apellidos_usuario}",
                    telefono = usuario.celular_usuario,
                    checked = usuario.id_usuario in unUariosSelect,
                    onCheckedChange = { checked ->
                        onUsuariosSelectChange(
                            if (checked) {
                                unUariosSelect + usuario.id_usuario
                            } else {
                                unUariosSelect - usuario.id_usuario
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CardUsuariosDisp(
    nombre: String,
    telefono: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
            Imagen("placeholder", 50)
            Spacer(modifier = Modifier.size(5.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Texto(nombre, true)
                TextoPeq(telefono)
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}