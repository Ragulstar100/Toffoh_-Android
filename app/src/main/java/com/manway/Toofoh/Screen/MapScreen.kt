package com.manway.Toofoh.Screen

import Ui.data.GeoLocation
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.manway.Toofoh.ViewModel.SharedViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    sharedViewModel: SharedViewModel,
    geoLocation: GeoLocation?,
    modifier: Modifier,
    onMapClick: (LatLng) -> Unit
) {
    var currentLocation by remember {
        mutableStateOf<LatLng?>(
            LatLng(
                geoLocation?.latitude ?: 37.7749, geoLocation?.longitude ?: -122.4194
            )
        )
    }
    var selectedLocation by remember {
        mutableStateOf<LatLng?>(
            LatLng(
                geoLocation?.latitude ?: 37.7749, geoLocation?.longitude ?: -122.4194
            )
        )
    }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                geoLocation?.latitude ?: 37.7749,
                geoLocation?.longitude ?: -122.4194
            ), 12f
        )
    }
    val mapProperties = MapProperties(isMyLocationEnabled = true)
    val googleMapOptions = GoogleMapOptions()
    googleMapOptions.zoomGesturesEnabled(false)



    sharedViewModel.activity?.let { context ->

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        scope.launch {
            val location = fusedLocationClient.lastLocation.await()
        }

        LaunchedEffect(key1 = Unit) {

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val location = fusedLocationClient.lastLocation.await()
                currentLocation = LatLng(location.latitude, location.longitude)
                if (geoLocation == null) {
                    selectedLocation = currentLocation
                    onMapClick(selectedLocation!!)
                    cameraPositionState = cameraPositionState.apply {
                        position = CameraPosition.fromLatLngZoom(selectedLocation!!, 15f)
                    }
                } else {
                    selectedLocation = LatLng(geoLocation.latitude, geoLocation.longitude)
                    onMapClick(selectedLocation!!)
                    cameraPositionState = cameraPositionState.apply {
                        position = CameraPosition.fromLatLngZoom(selectedLocation!!, 15f)
                    }
                }

            }
        }

        currentLocation?.let {
            cameraPositionState = cameraPositionState.apply {
                position = CameraPosition.fromLatLngZoom(it, 15f)
            }
        }


        Column {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = modifier,
                properties = mapProperties,
                googleMapOptionsFactory = { googleMapOptions },
                onMapClick = { latLng ->
                    onMapClick(latLng)
                    selectedLocation = latLng
                    showBottomSheet = true
                    scope.launch { sheetState.show() }
                }) {

                selectedLocation?.let {

                    Marker(
                        state = MarkerState(position = it),
                        title = "Selected Location",
                        snippet = "You picked this spot!"
                    )
                }
            }
            Log.d("MapScreen", "Selected Location: $currentLocation")
        }
    }
}