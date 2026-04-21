package com.fic.biobitacora.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fic.biobitacora.data.local.entities.AvistamientoEntity
import com.fic.biobitacora.data.local.entities.CategoriaEntity

// Esto evita que el alumno tenga que hacer JOINs manuales complejos
data class AvistamientoConCategoria(
    @Embedded val avistamiento: AvistamientoEntity,
    @Relation(
        parentColumn = "categoriaId",
        entityColumn = "id"
    )
    val categoria: CategoriaEntity
)