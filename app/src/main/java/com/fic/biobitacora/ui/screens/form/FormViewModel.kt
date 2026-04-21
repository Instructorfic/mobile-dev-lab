package com.fic.biobitacora.ui.screens.form

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fic.biobitacora.data.local.entities.AvistamientoEntity
import com.fic.biobitacora.data.local.entities.CategoriaEntity
import com.fic.biobitacora.data.repository.BioRepository
import com.fic.biobitacora.domain.location.LocationClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class FormViewModel(
    private val repository: BioRepository,
    private val locationClient: LocationClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    init {
        // Cargamos las categorías desde la DB para el Dropdown
        viewModelScope.launch {
            repository.categorias.collect { lista ->
                _uiState.update { it.copy(categoriasDisponibles = lista) }
            }
        }
    }

    fun onTituloChanged(nuevo: String) = _uiState.update { it.copy(titulo = nuevo) }

    fun onCategoriaSelected(cat: CategoriaEntity) = _uiState.update { it.copy(categoriaSeleccionada = cat) }


    fun obtenerUbicacionActual() {
        viewModelScope.launch {
            locationClient.getLocationUpdates(3000L)
                .catch { e -> Log.e("GPS", e.message ?: "Error") }
                .collect { location ->
                    _uiState.update { it.copy(
                        latitud = location.latitude,
                        longitud = location.longitude
                    )}
                }
        }
    }

    fun guardarAvistamiento() {
        val estado = _uiState.value
        if (estado.titulo.isBlank() || estado.categoriaSeleccionada == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(estaGuardando = true) }

            val nuevoAvistamiento = AvistamientoEntity(
                id = estado.id,
                titulo = estado.titulo,
                descripcion = estado.descripcion,
                categoriaId = estado.categoriaSeleccionada.id, // Usamos el ID de la relación
                fotoPath = estado.fotoPath,
                latitud = estado.latitud,
                longitud = estado.longitud
            )

            repository.guardarAvistamiento(nuevoAvistamiento)
            _uiState.update { it.copy(estaGuardando = false, guardadoExitoso = true) }
        }
    }


    // LA PIEZA QUE FALTABA: Función para la descripción
    fun onDescripcionChanged(nueva: String) {
        _uiState.update { it.copy(descripcion = nueva) }
    }

    // Función para capturar la URI de la foto
    fun onFotoCapturada(uri: Uri, context: Context) { // <--- Agregamos context aquí
        viewModelScope.launch(Dispatchers.IO) {
            val nombreUnico = "foto_${System.currentTimeMillis()}.jpg"
            val carpetaDestino = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "avistamientos")

            if (!carpetaDestino.exists()) carpetaDestino.mkdirs()

            val archivoFinal = File(carpetaDestino, nombreUnico)

            context.contentResolver.openInputStream(uri)?.use { input ->
                archivoFinal.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            _uiState.update { it.copy(
                fotoUri = Uri.fromFile(archivoFinal),
                fotoPath = archivoFinal.absolutePath
            )}
        }
    }

    fun cargarAvistamientoParaEdicion(id: Int) {
        viewModelScope.launch {
            // take(1) es vital para que solo cargue los datos una vez al abrir
            repository.obtenerAvistamientoPorId(id).take(1).collect { avistamiento ->
                _uiState.update { it.copy(
                    id = avistamiento.id,
                    titulo = avistamiento.titulo,
                    descripcion = avistamiento.descripcion,
                    latitud = avistamiento.latitud,
                    longitud = avistamiento.longitud,
                    fotoPath = avistamiento.fotoPath,
                    // Si hay fotoPath, creamos el Uri para mostrarla en la UI
                    fotoUri = avistamiento.fotoPath?.let { Uri.fromFile(java.io.File(it)) }
                )}
            }
        }
    }
}