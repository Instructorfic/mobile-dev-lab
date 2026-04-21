package com.fic.biobitacora

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fic.biobitacora.ui.navigation.BioNavHost
import com.fic.biobitacora.ui.theme.BioBitacoraTheme

class MainActivity : FragmentActivity() { // Cambiado a FragmentActivity para Biometría
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (application as BioBitacoraApp).repository

        enableEdgeToEdge()
        setContent {
            // Estado global del tema (Idealmente persistido con DataStore luego)
            var isDarkMode by remember { mutableStateOf(false) }

            BioBitacoraTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            // INICIO
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                label = { Text("Inicio") },
                                selected = currentDestination?.hierarchy?.any { it.route == "dashboard" } == true,
                                onClick = { navController.navigate("dashboard") {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }}
                            )

                            // NUEVO
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                                label = { Text("Nuevo") },
                                selected = currentDestination?.hierarchy?.any { it.route?.startsWith("formulario") == true } == true,
                                onClick = { navController.navigate("formulario") {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }}
                            )

                            // HISTORIAL (CON BIOMETRÍA)
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                                label = { Text("Historial") },
                                selected = currentDestination?.hierarchy?.any { it.route == "lista" } == true,
                                onClick = {
                                    showBiometricPrompt {
                                        navController.navigate("lista") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    BioNavHost(
                        navController = navController,
                        repository = repository,
                        isDarkMode = isDarkMode,
                        onToggleTheme = { isDarkMode = !isDarkMode },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun showBiometricPrompt(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso Protegido")
            .setSubtitle("Usa tu huella para ver el historial")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}