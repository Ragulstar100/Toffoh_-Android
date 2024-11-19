package com.manway.toffoh.admin.data

import kotlinx.serialization.Serializable

@Serializable
data class ServiceArea(val id:Int?=null,val locationName:String,val pincode:Int,val googleLocation:String?=null)