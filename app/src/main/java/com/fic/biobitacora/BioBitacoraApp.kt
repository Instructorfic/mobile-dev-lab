package com.fic.biobitacora

import android.app.Application
import com.fic.biobitacora.data.local.BioDatabase
import com.fic.biobitacora.data.remote.BioApiService
import com.fic.biobitacora.data.repository.BioRepository
import com.fic.biobitacora.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BioBitacoraApp : Application() {

    // 1. Instancia de la Base de Datos (Singleton)
    private val database by lazy { BioDatabase.getDatabase(this) }

    // 2. Configuración de Retrofit para la API Remota
    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BioApiService::class.java)
    }

    // 3. El Repositorio ahora recibe AMBAS fuentes de datos (Local y Remota)
    val repository by lazy {
        BioRepository(database.avistamientoDao(), apiService)
    }
}