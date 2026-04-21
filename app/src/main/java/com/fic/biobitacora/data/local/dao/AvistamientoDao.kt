package com.fic.biobitacora.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fic.biobitacora.data.local.entities.AvistamientoEntity
import com.fic.biobitacora.data.local.entities.CategoriaEntity
import com.fic.biobitacora.data.local.relations.AvistamientoConCategoria
import kotlinx.coroutines.flow.Flow

@Dao
interface AvistamientoDao {
    // Usamos Flow para que la UI se actualice SOLA cuando haya cambios
    // Consulta todo y lo une con su categoría automáticamente
    @Transaction
    @Query("SELECT * FROM avistamientos ORDER BY fecha DESC")
    fun getAllAvistamientos(): Flow<List<AvistamientoConCategoria>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvistamiento(avistamiento: AvistamientoEntity): Long

    @Delete
    suspend fun deleteAvistamiento(avistamiento: AvistamientoEntity)

    @Query("SELECT * FROM avistamientos WHERE id = :id")
    fun getAvistamientoById(id: Int): Flow<AvistamientoEntity>

    @Query("UPDATE avistamientos SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarComoSincronizado(id: Int)

    // Para llenar el Dropdown del formulario
    @Query("SELECT * FROM categorias")
    fun getAllCategorias(): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategorias(categorias: List<CategoriaEntity>)
}