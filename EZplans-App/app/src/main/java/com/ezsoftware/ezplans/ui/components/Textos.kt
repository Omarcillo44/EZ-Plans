package com.ezsoftware.ezplans.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier

@Composable
fun Titulo(
    titulo: String
){
    Text(
        text = titulo,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun SubTitulo(
    titulo: String,
    bold: Boolean
){
    Text(
        text = titulo,
        fontSize = 20.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun Texto(
    texto: String,
    bold: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
) {
    Text(
        text = texto,
        fontSize = 18.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun TextoPeq(
    texto: String,
    bold: Boolean? = null,
    color: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
) {
    Text(
        text = texto,
        fontSize = 14.sp,
        fontWeight = when (bold) {
            true -> FontWeight.Bold
            false -> FontWeight.Normal
            null -> null
        },
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun TextoAviso(
    texto: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
){
    Text(
        text = texto,
        fontSize = 13.sp,
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}




