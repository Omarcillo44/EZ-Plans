package com.ezsoftware.ezplans.ui.components.Dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ezsoftware.ezplans.ui.components.Imagen
import com.ezsoftware.ezplans.ui.components.Texto
import com.ezsoftware.ezplans.ui.components.TextoPeq


@Composable
fun PlanesCard(
    titulo: String,
    gasto: String,
    fecha: String,
    miembros: String,
    estado: String,
    rol: String,
    iconoEstado: String = "placeholder",  // Puedes cambiar el valor por defecto
    iconoRol: String = "placeholder"      // Puedes cambiar el valor por defecto
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Imagen("plan4", 50)
                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    Texto(titulo, true)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Imagen("gasto", 20)
                        Spacer(modifier = Modifier.size(5.dp))
                        TextoPeq(gasto)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Imagen("calendario", 20)
                        Spacer(modifier = Modifier.size(5.dp))
                        TextoPeq(fecha)
                    }
                }
            }

            Spacer(modifier = Modifier.size(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Imagen("miembros", 20)
                    Spacer(modifier = Modifier.size(10.dp))
                    TextoPeq(miembros)
                }
                Row {
                    Imagen(
                        when (estado) {
                            "Completo" -> "check"
                            "Pendiente" -> "pendiente"
                            else -> "placeholder"  // Opcional: valor por defecto
                        },
                        20
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    TextoPeq(estado)
                }
                Row {
                    Imagen(
                        when (rol) {
                            "Admin." -> "administrador"
                            "Miembro" -> "miembro"
                            else -> "placeholder"  // Opcional: valor por defecto
                        },
                        20
                    )
                }
            }
        }
    }
}
