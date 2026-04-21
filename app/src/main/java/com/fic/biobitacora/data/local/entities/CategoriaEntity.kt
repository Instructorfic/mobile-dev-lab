package com.fic.biobitacora.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String, // Ejemplo: "Ave", "Planta"
    val codigoIcono: String? = null // Útil para mostrar un icono específico en la UI
)