package com.fic.biobitacora.data.location


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.fic.biobitacora.domain.location.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> = callbackFlow {
        // 1. Verificar si el GPS está encendido
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(!isGpsEnabled) {
            throw LocationClient.LocationException("GPS desactivado")
        }

        // 2. Definir qué hacer cuando llegue una ubicación
        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                super.onLocationResult(result)
                result.locations.lastOrNull()?.let { location ->
                    launch { send(location) }
                }
            }
        }

        // 3. Empezar a pedir ubicaciones
        val request = com.google.android.gms.location.LocationRequest.Builder(interval).build()
        client.requestLocationUpdates(request, locationCallback, context.mainLooper)

        // 4. Limpiar cuando se deje de usar
        awaitClose {
            client.removeLocationUpdates(locationCallback)
        }
    }
}