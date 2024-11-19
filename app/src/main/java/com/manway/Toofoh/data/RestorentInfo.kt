package com.manway.toffoh.admin.data

import Ui.data.Address
import Ui.enums.Availability
import Ui.data.ImageUrl
import Ui.data.PhoneNumber
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import kotlinx.datetime.LocalDateTime

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantInfo private constructor(
    val id: Int?=null,
    val created_at: LocalDateTime?=null,
    val updated_at: LocalDateTime?=null,//filter
    val channel_id: String?=null,
    val owner_id: String,
    val fssaiNumber: String,
    val name: String,//filter
    val address: Address,//filter
    val phoneNumber: PhoneNumber,//
    val imageUrl: ImageUrl?=null,
    val cuisine: List<String>,//filter
    val upi_id: String,
    val minOrderAmount: Double,
    val deliveryFee: Double,
    val estimatedDeliveryTime: Int,//filter
    val isAvailable: Availability,//filter
    val rating: Double,//filter
    val numberOfRatings: Int,
    val others: HashMap<String, String> =hashMapOf()
){


    companion object{
        val initialRestaurantInfo=RestaurantInfo(null,null,null,null,"","","", Address("","",""), PhoneNumber("",""),null, listOf(),"",100.00,15.00,45, Availability.NotAvailable,0.0,0)
        val bucketName="restorentImages"
    }


    @Composable
    fun itemView(){
        Row(Modifier.fillMaxWidth()) {
            val modifier= Modifier.background(Color.Blue, MaterialTheme.shapes.medium).padding(10.dp)
            Spacer(Modifier.width(10.dp))
           Text(id.toString(), modifier)
            Spacer(Modifier.width(10.dp))
            Text(channel_id ?: "", modifier)
            Spacer(Modifier.width(10.dp))
            Text(name, modifier)
//            Spacer(Modifier.width(10.dp))
//            Text(email?:"",modifier)
            Spacer(Modifier.width(10.dp))
            Text(phoneNumber.toString(), modifier)
//            Spacer(Modifier.width(10.dp))
//            Text(foodCategory,modifier)
            Spacer(Modifier.width(10.dp))
           Text(others.toString(), modifier)
        }
    }




        }

@Composable
fun RestaurantInfoScope(RestaurantInfo: RestaurantInfo,scope: @Composable RestaurantInfo.()->Unit){
    scope(RestaurantInfo)
}

