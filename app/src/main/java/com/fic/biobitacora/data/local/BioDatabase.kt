package com.fic.biobitacora.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fic.biobitacora.data.local.dao.AvistamientoDao
import com.fic.biobitacora.data.local.entities.AvistamientoEntity
import com.fic.biobitacora.data.local.entities.CategoriaEntity
import com.fic.biobitacora.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [AvistamientoEntity::class, CategoriaEntity::class], version = 1, exportSchema = false)
abstract class BioDatabase : RoomDatabase() {

    abstract fun avistamientoDao(): AvistamientoDao

    companion object {
        @Volatile
        private var INSTANCE: BioDatabase? = null

        fun getDatabase(context: Context): BioDatabase {
            // Si la instancia ya existe la retorna, si no, la crea (Singleton)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BioDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .addCallback(BioDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class BioDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insertamos categorías por defecto al crear la DB por primera vez
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = database.avistamientoDao()
                        // Aquí puedes usar tus strings de recursos o constantes
                        dao.insertCategorias(listOf(
                            CategoriaEntity(nombre = "Ave"),
                            CategoriaEntity(nombre = "Planta"),
                            CategoriaEntity(nombre = "Insecto"),
                            CategoriaEntity(nombre = "Mamífero")
                        ))
                    }
                }
            }
        }

    }


}