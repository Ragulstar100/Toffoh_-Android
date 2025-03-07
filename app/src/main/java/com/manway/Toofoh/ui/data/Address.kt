package Ui.data

import android.location.Location
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
//import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Suppress("UNREACHABLE_CODE")
@Serializable
data class Address(
    val address: ExtendedAddress,
    val pincode: String,
    val geoLocation: GeoLocation
) {
    companion object {
        val intial = Address(ExtendedAddress.intial, "", GeoLocation(0.0, 0.0))
    }

    override fun toString(): String {
        val extendedAddress = address
        return "${if (extendedAddress.name.isNotEmpty()) (extendedAddress.name + ',') else ""}${if (extendedAddress.doorNumber.isNotEmpty()) extendedAddress.doorNumber + ',' else ""}${if (extendedAddress.location.isNotEmpty()) extendedAddress.location + ',' else ""}${extendedAddress.phoneNumber.countryCode} ${if (extendedAddress.phoneNumber.phoneNumber.isNotEmpty()) extendedAddress.phoneNumber.phoneNumber + ',' else ""}${if (extendedAddress.locationType.isNotEmpty()) extendedAddress.locationType + ',' else ""}${if (extendedAddress.landmark.isNotEmpty()) extendedAddress.landmark + ',' else ""}${if (pincode.isNotEmpty()) "pincode:$pincode," else ""}${if (geoLocation != GeoLocation.intial) "geoLocation:$geoLocation" else ""}"

    }

}


enum class LocationType {
    Home, Work, Office, Other
}

@Serializable
data class ExtendedAddress(
    val name: String,
    val doorNumber: String,
    val location: String,
    val locationType: String,
    val phoneNumber: PhoneNumber,
    val landmark: String = "",
    val others: String = ""
) {
    companion object{
        val intial =
            ExtendedAddress("", "", "", LocationType.Home.name, PhoneNumber("", ""), "", "")
    }

    override fun toString(): String {
        return "$doorNumber,$location, "+
                ",$phoneNumber"+ ",LandMark:$landmark"+
                " Others:$others"

    }

}

@Serializable
data class GeoLocation(val latitude: Double, val longitude: Double) {
    companion object {
        val intial = GeoLocation(0.0, 0.0)
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

fun MutableState<ArrayList<Address>>.add(address: Address) {
    val addressList = arrayListOf<Address>().apply {
        addAll(value)
    }
    addressList.add(address)
    value = addressList
}

fun MutableState<ArrayList<Address>>.update(index: Int, address: Address) {
    val addressList = arrayListOf<Address>().apply {
        addAll(value)
    }
    addressList[index] = address
    value = addressList
}

fun MutableState<ArrayList<Address>>.delete(index: Int) {
    val addressList = arrayListOf<Address>().apply {
        addAll(value)
    }
    addressList.removeAt(index)
    value = addressList
}




