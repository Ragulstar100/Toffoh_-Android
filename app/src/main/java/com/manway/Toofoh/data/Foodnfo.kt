package com.manway.toffoh.admin.data

import Ui.enums.Availability
import Ui.data.ImageUrl
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.manway.Toofoh.data.FoodCategory
import kotlinx.datetime.LocalDateTime

import kotlinx.serialization.Serializable

@Serializable
data class FoodInfo(
    val id: Int? = null,
    val created_at: LocalDateTime? = null,
    val updated_at: LocalDateTime? = null,
    val restaurantChannelId: String,//Foreign Key Action On Restaurant Table
    val imageUrl: ImageUrl? = null,
    val name: String,
    val price: Double,
    val dishCategory: String? = null,
    val foodCategory: FoodCategory,//Veg or Non Veg
    val foodType: List<String>, //Desert or Main Course,Dairy product
    val available_qty: Int = 0,
    val isAvailable: Availability,
    val rating: Double,
    val numberOfRatings: Int,
    val description: String = "",
    val others: HashMap<String, String>? = hashMapOf(),
){


    companion object{
        val initialFoodInfo=FoodInfo(null, null, null,"",
            ImageUrl("","",null),"",100.00,"",
            FoodCategory.VEG, listOf(),0, Availability.NotAvailable, 0.0, 0,)
    }

    @Composable
    fun itemView() {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(15.dp),Arrangement.SpaceBetween) {

        }
    }

    @Composable
    fun orderItemView(list:List<FoodInfo>) {
        Row(
            Modifier
                .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
                .fillMaxWidth(0.70f)
                .padding(15.dp),Arrangement.SpaceBetween){
            Text(name)
            val count= list.filter { it.id==id }.size
            Text(count.toString())
            Text(price.toString())
            Text((price*count).toString())
        }
    }

    @Composable
    fun profileInfo(colseAction:(Boolean)->Unit) {

    }
    
    
}



@Composable
fun MutableState<Pair<FoodInfo,Int>>.orderItemView(list:List<FoodInfo>,orderQuntityChanageListner:(Pair<FoodInfo,Int>)->Unit){
    Row(
        Modifier
            .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
            .fillMaxWidth(0.70f)
            .height(50.dp)
            .padding(5.dp),Arrangement.SpaceBetween,Alignment.CenterVertically){
        Text(value.first.name)
        Row(Modifier.width(100.dp),Arrangement.SpaceBetween) {

            IconButton(onClick = {
                if(value.second>1) value=value.copy(second = value.second-1)
               // else value=value.copy(second = 1)
                orderQuntityChanageListner(value)
            }){
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Reduce")
            }
            Text(value.second.toString())
            IconButton(onClick = {
                value=value.copy(second = value.second+1)
                orderQuntityChanageListner(value)
            }){
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Add")
            }
        }

        Text(value.first.price.toString())
        Text((value.first.price*value.second).toString())
    }
}

@Composable
fun FoodInfoScope(foodInfo: FoodInfo,scope: @Composable FoodInfo.()->Unit){
    scope(foodInfo)
}