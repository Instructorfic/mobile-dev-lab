package com.fic.biobitacora.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onNavigateToForm: () -> Unit,
    onNavigateToList: () -> Unit,
    onToggleTheme: () -> Unit,
    isDarkMode: Boolean
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("BioBitácora", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
            IconButton(onClick = onToggleTheme) {
                Icon(if (isDarkMode) Icons.Default.Done else Icons.Default.Close, contentDescription = null)
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onNavigateToForm,
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Default.Phone, null)
            Spacer(Modifier.width(12.dp))
            Text("Nuevo Registro", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToList,
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Default.Phone, null)
            Spacer(Modifier.width(12.dp))
            Text("Explorar Historial", style = MaterialTheme.typography.titleMedium)
        }
    }
}