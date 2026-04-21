package com.fic.biobitacora.ui.screens.form

import android.net.Uri
import com.fic.biobitacora.data.local.entities.CategoriaEntity

data class FormUiState(
    val id: Int = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val categoriaSeleccionada: CategoriaEntity? = null,
    val categoriasDisponibles: List<CategoriaEntity> = emptyList(),
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fotoPath: String? = null,
    val fotoUri: Uri? = null,
    val estaGuardando: Boolean = false,
    val guardadoExitoso: Boolean = false
)
