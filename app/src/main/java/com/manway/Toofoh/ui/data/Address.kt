package Ui.data

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
//import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class Address(val address:String,val pincode:String,val geoLocation:String){
    override fun toString(): String {
        return "address:$address\n" + "pincode:$pincode\n" + "geolocation:$geoLocation"
    }
}

fun MutableState<ArrayList<Address>>.add(address: Address){
    val addressList= arrayListOf<Address>().apply {
        addAll(value)
    }
    addressList.add(address)
    value=addressList
}

fun MutableState<ArrayList<Address>>.update(index:Int, address: Address){
    val addressList= arrayListOf<Address>().apply {
        addAll(value)
    }
    addressList[index]=address
    value=addressList
}

fun MutableState<ArrayList<Address>>.delete(index:Int){
    val addressList= arrayListOf<Address>().apply {
        addAll(value)
    }
    addressList.removeAt(index)
    value=addressList
}


@Composable
fun AddressField(address: MutableState<Address>, errorList:List<String>, errorIndex:Int, modifier: Modifier = Modifier, trailIconAddress:@Composable ()->Unit={}, trailIconPincode:@Composable ()->Unit={}, trailIconGeolocation:@Composable ()->Unit={}) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = address.value.address,
            onValueChange = { address.value = address.value.copy(address = it) },
            label = { Text("Address") },
            trailingIcon = trailIconAddress,
            singleLine = false
        )
        OutlinedTextField(
            value = address.value.pincode,
            onValueChange = { address.value = address.value.copy(pincode = it) },
            label = { Text("Pin code") },
            trailingIcon = trailIconPincode,
            singleLine = true
        )
        OutlinedTextField(
            value = address.value.geoLocation,
            onValueChange = { address.value = address.value.copy(geoLocation = it) },
            label = { Text("Geolocation") },
            trailingIcon =trailIconGeolocation ,
            singleLine = true
        )
        Text(errorList[errorIndex], color = Color.Red )
    }
}






