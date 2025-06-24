package com.ezsoftware.ezplans.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.ByteArrayOutputStream

@Composable
fun Imagen(
    nombre: String,
    size: Int
) {
    val context = LocalContext.current
    val resourceId = remember(nombre) {
        context.resources.getIdentifier(nombre, "drawable", context.packageName)
    }
    if (resourceId != 0) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                    RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    } else {
        Text("Imagen no encontrada")
    }
}

// metodo para mostrar la imagen
// Recibe imagen en bitmap
@Composable
fun Imagen(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
    )
}

// Metodo para decodificar la imagen en base64
fun base64ToBitmap(base64Str: String): Bitmap? {
    try {
        val cleanBase64 = base64Str.substringAfter(",")
        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        return null
    }
}

// Metodo mejorado para convertir Bitmap a Base64 con compresión
fun bitmapToBase64(bitmap: Bitmap, quality: Int = 100): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

// Función para redimensionar la imagen
fun redimensionarImagen(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    // Calcular el factor de escala manteniendo las proporciones
    val scale = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)

    // Si la imagen ya es menor que el tamaño máximo, no redimensionar
    if (scale >= 1.0f) return bitmap

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

@Composable
fun VisorImagen(
    base64Image: String,
    onDismiss: () -> Unit
) {
    val bitmap = remember(base64Image) {
        base64ToBitmap(base64Image)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
        ) {
            // Botón cerrar
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White
                )
            }

            // Área clickeable para cerrar (solo en los bordes)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null, // Sin efecto visual
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() }
            )

            // Imagen (NO clickeable para evitar conflicto)
            bitmap?.let { bmp ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Comprobante ampliado",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}