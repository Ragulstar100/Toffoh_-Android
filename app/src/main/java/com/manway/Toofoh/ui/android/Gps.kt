package com.manway.Toofoh.ui.android

import Ui.data.Address
import Ui.data.GeoLocation
import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.manway.Toofoh.ViewModel.SharedViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@Composable
fun LocationPermissionHandler(
    sharedViewModel: SharedViewModel,
    onPermissionGranted: () -> Unit = {}
) {

    var permissionGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (permissionGranted) {
                onPermissionGranted()
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        sharedViewModel.activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                permissionGranted = true
                onPermissionGranted()
            }
        }
    }
}


@Composable
fun GetLocation(context: ComponentActivity) {
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var permissionGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (permissionGranted) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        currentLocation = location
                    }
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        context.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                permissionGranted = true
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        currentLocation = location
                    }
            }
        }
    }


    if (currentLocation != null) {
        Text("Latitude: ${currentLocation?.latitude}, Longitude: ${currentLocation?.longitude}")
    } else {
        Text("Location not available")
    }
}

fun Context.locationPermissionCheck(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.locationGpsOnCheck(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return !(!isGpsEnabled && !isNetworkEnabled)
}

interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String) : Exception()
}

class LocationApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if (!context.locationPermissionCheck()) {
                throw LocationClient.LocationException("Missing location permission")
            }

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            val request = LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(interval)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}

infix fun Address.distance(address: Address): Float {
    val locationA = Location("Location A")
    locationA.latitude = this.geoLocation.latitude
    locationA.longitude = this.geoLocation.longitude
    val locationB = Location("Location B")
    locationB.latitude = address.geoLocation.latitude
    locationB.longitude = address.geoLocation.longitude

    return locationB.distanceTo(locationA)
}

infix fun GeoLocation.distance(geoLocation: GeoLocation): Float {
    val locationA = Location("Location A")
    locationA.latitude = geoLocation.latitude
    locationA.longitude = geoLocation.longitude
    val locationB = Location("Location B")
    locationB.latitude = latitude
    locationB.longitude = longitude
    Log.d("LocationError", locationB.distanceTo(locationA).toString())

    return locationB.distanceTo(locationA)
}