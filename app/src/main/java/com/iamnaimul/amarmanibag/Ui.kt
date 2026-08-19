package com.iamnaimul.amarmanibag

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

fun money(v: Double): String = "৳ " + NumberFormat.getNumberInstance(Locale.US).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}.format(v)

@Composable
fun MoneyText(value: Double, positive: Boolean = false, negative: Boolean = false) {
    Text(
        text = money(value),
        color = when {
            positive -> MaterialTheme.colorScheme.primary
            negative -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
    )
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 10.dp))
}

@Composable
fun ConfirmDialog(title: String, text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("নিশ্চিত") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("বাতিল") } }
    )
}
