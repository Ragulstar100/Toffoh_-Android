package com.manway.Toofoh.data

import Ui.data.ImageUrl
import kotlinx.serialization.Serializable

@Serializable
data class CommonFoodInfo(val id:Int?=null, val name:String, val dishCategory:String, val foodCategory: FoodCategory, val foodType:List<String>?=null, val description:String?=null, val imageUrl: ImageUrl?=null)