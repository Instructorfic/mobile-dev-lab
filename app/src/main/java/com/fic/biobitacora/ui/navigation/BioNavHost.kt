package com.fic.biobitacora.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fic.biobitacora.data.location.DefaultLocationClient
import com.fic.biobitacora.data.repository.BioRepository
import com.fic.biobitacora.ui.screens.dashboard.DashboardScreen
import com.fic.biobitacora.ui.screens.form.FormContent
import com.fic.biobitacora.ui.screens.form.FormViewModel
import com.fic.biobitacora.ui.screens.form.FormViewModelFactory
import com.fic.biobitacora.ui.screens.list.ListScreen
import com.fic.biobitacora.ui.screens.list.ListViewModel
import com.fic.biobitacora.ui.screens.list.ListViewModelFactory
import com.google.android.gms.location.LocationServices

@Composable
fun BioNavHost(
    navController: NavHostController,
    repository: BioRepository,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationClient = remember {
        DefaultLocationClient(context, LocationServices.getFusedLocationProviderClient(context))
    }

    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToForm = { navController.navigate("formulario") },
                onNavigateToList = { navController.navigate("lista") },
                onToggleTheme = onToggleTheme,
                isDarkMode = isDarkMode
            )
        }

        composable(
            route = "formulario?id={id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            val viewModel: FormViewModel = viewModel(factory = FormViewModelFactory(repository, locationClient))

            LaunchedEffect(id) {
                if (id != -1) viewModel.cargarAvistamientoParaEdicion(id)
            }
            FormContent(viewModel = viewModel)
        }

        composable("lista") {
            val viewModel: ListViewModel = viewModel(factory = ListViewModelFactory(repository))
            ListScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onItemClick = { id -> navController.navigate("formulario?id=$id") }
            )
        }
    }
}