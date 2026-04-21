package com.fic.biobitacora.ui.screens.form

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.fic.biobitacora.R
import com.fic.biobitacora.data.local.entities.CategoriaEntity
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormContent(viewModel: FormViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 1. Recursos de dimensiones y estilo
    val paddingMedium = dimensionResource(id = R.dimen.padding_medium)
    val cornerRadius = dimensionResource(id = R.dimen.corner_radius)
    val scrollState = rememberScrollState()

    // 2. Lógica de Cámara: Configuración del archivo temporal
    val tempFile = remember { File(context.externalCacheDir, "temp_photo.jpg") }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.onFotoCapturada(Uri.fromFile(tempFile),context)
        }
    }

    // 3. Lógica de GPS: Lanzador de permisos en tiempo real
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            viewModel.obtenerUbicacionActual()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingMedium)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(paddingMedium)
    ) {
        // --- SECCIÓN 1: CAPTURA DE FOTOGRAFÍA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.image_height_form))
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        tempFile
                    )
                    cameraLauncher.launch(uri)
                },
            contentAlignment = Alignment.Center
        ) {
            if (state.fotoUri != null) {
                AsyncImage(
                    model = state.fotoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(text = stringResource(id = R.string.btn_take_photo))
            }
        }

        // --- SECCIÓN 2: NOMBRE DE LA ESPECIE ---
        OutlinedTextField(
            value = state.titulo,
            onValueChange = { viewModel.onTituloChanged(it) },
            label = { Text(text = stringResource(id = R.string.hint_title)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // --- SECCIÓN 3: SELECTOR DE CATEGORÍA ---
        CategoriaDropDown(
            categorias = state.categoriasDisponibles,
            seleccionada = state.categoriaSeleccionada,
            onSelect = { viewModel.onCategoriaSelected(it) }
        )

        // --- SECCIÓN 4: OBSERVACIONES ---
        OutlinedTextField(
            value = state.descripcion,
            onValueChange = { viewModel.onDescripcionChanged(it) },
            label = { Text(text = stringResource(id = R.string.hint_description)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // --- SECCIÓN 5: UBICACIÓN GPS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.lbl_location),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(text = "Lat: ${state.latitud ?: "---"}")
                    Text(text = "Long: ${state.longitud ?: "---"}")
                }

                Button(onClick = {
                    val fineLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    val coarseLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

                    if (fineLoc == PackageManager.PERMISSION_GRANTED && coarseLoc == PackageManager.PERMISSION_GRANTED) {
                        viewModel.obtenerUbicacionActual()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }) {
                    Text(text = stringResource(id = R.string.btn_gps))
                }
            }
        }

        // --- SECCIÓN 6: ACCIÓN DE GUARDAR ---
        Button(
            onClick = { viewModel.guardarAvistamiento() },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.button_height)),
            shape = RoundedCornerShape(cornerRadius),
            enabled = !state.estaGuardando
        ) {
            if (state.estaGuardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.padding_large)),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = dimensionResource(id = R.dimen.padding_small)
                )
            } else {
                Text(text = stringResource(id = R.string.btn_save).uppercase())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaDropDown(
    categorias: List<CategoriaEntity>,
    seleccionada: CategoriaEntity?,
    onSelect: (CategoriaEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = seleccionada?.nombre ?: "Selecciona categoría",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.lbl_category)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categorias.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(text = categoria.nombre) },
                    onClick = {
                        onSelect(categoria)
                        expanded = false
                    }
                )
            }
        }
    }
}