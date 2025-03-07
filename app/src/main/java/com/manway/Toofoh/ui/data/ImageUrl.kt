package Ui.data

import kotlinx.serialization.Serializable

@Serializable
data class ImageUrl(val bucketName:String,val filePath:String,val imageUrl:String?=null)