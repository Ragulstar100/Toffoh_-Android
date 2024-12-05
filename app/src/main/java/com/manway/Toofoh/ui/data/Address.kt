package Ui.data

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

@Serializable
data class Address(val address:String,val pincode:String,val geoLocation:String){

    constructor(extendedAddress:ExtendedAddress, pincode: String, geoLocation: String):this(Json.encodeToString(extendedAddress),pincode,geoLocation)

    fun getExtendedAddress():ExtendedAddress{
        return try {
            Json.decodeFromString(address)
        }catch (e:Exception){
            ExtendedAddress.intial
        }

    }

    override fun toString(): String {
        return "address:$address\n"+
                "pincode:$pincode\n"+
                "geolocation:$geoLocation"
    }

}

@Serializable
data class ExtendedAddress(val doorNumber:String, val location:String, val locationType:String,val phoneNumber: PhoneNumber, val landmark:String="" , val others:String="" ){
    companion object{
        val intial=ExtendedAddress("","",LocationType.Home.name,PhoneNumber("",""),"","")
    }

    override fun toString(): String {
        return "$doorNumber,$location, "+
                ",$phoneNumber"+ ",LandMark:$landmark"+
                " Others:$others"

    }

}

//@Serializable
//data class Address(val address:String,val pincode:String,val geoLocation:String,val location:String,val locationType:LocationType){
//    override fun toString(): String {
//        return "locationType:$locationType\n"+
//                "location:$location\n"+
//                "address:$address\n"+
//                "pincode:$pincode\n"+
//                "geolocation:$geoLocation"
//    }
//}

enum class LocationType{
    Home,Work,Office,Other
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

@Composable
fun AddressField(list:List<Address>,index: Int, onAddressChange:(List<Address>)->Unit){

    var address by remember {
        mutableStateOf(list[index])
    }
    LaunchedEffect(address){
        onAddressChange(list)
    }

    Column(Modifier.scale(0.85f).padding(10.dp).fillMaxWidth().border(1.dp,
        MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small).padding(10.dp),horizontalAlignment = Alignment.CenterHorizontally) {
//        Row(Modifier.scale(0.75f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//            FilterChip(address.locationType==LocationType.Home,{
//                address=address.copy(locationType = LocationType.Home)
//            },{
//                Text("Home",style = MaterialTheme.typography.bodySmall)
//            })
//            FilterChip(address.locationType==LocationType.Work,{
//                address=address.copy(locationType = LocationType.Work)
//            },{
//                Text("Work",style = MaterialTheme.typography.bodySmall)
//            })
//            FilterChip(address.locationType==LocationType.Office,{
//                address=address.copy(locationType = LocationType.Office)
//            },{
//                Text("Office",style = MaterialTheme.typography.bodySmall)
//            })
//            FilterChip(address.locationType==LocationType.Other,{
//                address=address.copy(locationType = LocationType.Other)
//            },{
//                Text("Other",style = MaterialTheme.typography.bodySmall)
//            })
//        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton({

                onAddressChange(list.apply {
                  //  removeAt(index)
                })
            }) {
                Icon(
                    Icons.Default.Delete,
                    "",
                    Modifier.size(35.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        OutlinedTextField(address.address,{address=address.copy(address = it)},label = { Text("Address") },shape = MaterialTheme.shapes.small)
        OutlinedTextField(address.pincode,{address=address.copy(pincode = it)},label = { Text("Pincode") },shape = MaterialTheme.shapes.small)

        //   OutlinedTextField(address.location,{address=address.copy(location = it)},label = { Text("Location") },shape = MaterialTheme.shapes.small)
    }
}





