package com.fic.biobitacora.data.remote

import com.fic.biobitacora.data.local.entities.AvistamientoEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BioApiService {

    // Para traer el catálogo de la nube (Unidad 5)
    @GET("avistamientos")
    suspend fun obtenerAvistamientosRemote(): List<AvistamientoEntity>

    // Para subir un nuevo hallazgo
    @POST("avistamientos")
    suspend fun subirAvistamiento(@Body avistamiento: AvistamientoEntity): Response<Unit>
}