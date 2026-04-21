package com.fic.biobitacora.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "avistamientos",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT // No permite borrar una categoría si tiene avistamientos
        )
    ],
    indices = [Index(value = ["categoriaId"])] // Mejora la velocidad de búsqueda
)
data class AvistamientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val categoriaId: Int, // <--- Relación por ID numérico
    val fotoPath: String?,
    val latitud: Double?,
    val longitud: Double?,
    val fecha: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = false
)