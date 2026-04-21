package com.fic.biobitacora.domain.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    // Un flujo que nos da la ubicación actual
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception(message)
}