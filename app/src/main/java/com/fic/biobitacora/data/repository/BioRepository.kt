package com.fic.biobitacora.data.repository

import android.util.Log
import com.fic.biobitacora.data.local.dao.AvistamientoDao
import com.fic.biobitacora.data.local.entities.AvistamientoEntity
import com.fic.biobitacora.data.local.entities.CategoriaEntity
import com.fic.biobitacora.data.remote.BioApiService
import com.fic.biobitacora.util.Constants
import kotlinx.coroutines.flow.Flow

class BioRepository(
    private val dao: AvistamientoDao,
    private val api: BioApiService // Por ahora puede ser null o una interfaz mock
) {
    val avistamientos = dao.getAllAvistamientos()
    val categorias = dao.getAllCategorias()

    suspend fun guardarAvistamiento(avistamiento: AvistamientoEntity) {
        // 1. Guardar en local (ID generado automáticamente por Room)
        val idGenerado = dao.insertAvistamiento(avistamiento)

        // 2. Crear una copia con el ID real para intentar subirlo
        val avistamientoParaSubir = avistamiento.copy(id = idGenerado.toInt())

        try {
            val response = api.subirAvistamiento(avistamientoParaSubir)
            if (response.isSuccessful) {
                // 3. Si la API responde OK, marcamos como sincronizado localmente
                dao.marcarComoSincronizado(idGenerado.toInt())
                Log.d(Constants.TAG_LOGS, "Sincronizado con éxito: ${avistamiento.titulo}")
            }
        } catch (e: Exception) {
            // Si no hay internet o falla el server, el registro se queda con 'sincronizado = false'
            Log.e(Constants.TAG_LOGS, "Fallo de sincronización: ${e.message}")
        }
    }

    fun obtenerAvistamientoPorId(id: Int): Flow<AvistamientoEntity> {
        return dao.getAvistamientoById(id)
    }

    suspend fun refrescarDesdeNube() {
        try {
            val remotos = api.obtenerAvistamientosRemote()
            // Al insertar con REPLACE en el DAO, actualizamos los que ya existan
            remotos.forEach { rem ->
                dao.insertAvistamiento(rem.copy(sincronizado = true))
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG_LOGS, "Error al refrescar catálogo: ${e.message}")
        }
    }

    suspend fun inicializarCategorias(categorias: List<CategoriaEntity>) {
        dao.insertCategorias(categorias)
    }
}