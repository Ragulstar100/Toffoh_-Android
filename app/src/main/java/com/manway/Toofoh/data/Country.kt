package com.manway.toffoh.admin.data

import kotlinx.serialization.Serializable

@Serializable
data class Country(val id:Int?=null,val imageUrl:String?=null,val countryName:String,val countryCode:String,val pattern: String)
