package com.fic.biobitacora.ui.screens.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fic.biobitacora.data.repository.BioRepository
import com.fic.biobitacora.domain.location.LocationClient

class FormViewModelFactory(
    private val repository: BioRepository,
    private val locationClient: LocationClient)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FormViewModel(repository,locationClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}