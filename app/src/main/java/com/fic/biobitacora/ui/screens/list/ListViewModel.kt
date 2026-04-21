package com.fic.biobitacora.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fic.biobitacora.data.local.relations.AvistamientoConCategoria
import com.fic.biobitacora.data.repository.BioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ListViewModel(private val repository: BioRepository) : ViewModel() {

    // Convertimos el Flow de la base de datos en un StateFlow para Compose
    // Esto hace que la lista se actualice sola si se borra o agrega algo
    val avistamientos: StateFlow<List<AvistamientoConCategoria>> = repository.avistamientos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}