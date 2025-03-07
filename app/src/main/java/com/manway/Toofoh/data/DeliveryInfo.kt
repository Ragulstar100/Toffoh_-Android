package com.manway.Toofoh.data

import kotlinx.datetime.LocalDateTime

data class DeliveryInfo(
    val id:Int?=null,
    val created_at:LocalDateTime?=null,
    val orderId: String,
    val employeeId:String?=null,
    val deliveredTime: LocalDateTime?=null,
    val deliveryStatus: DeliveryStatus,
    val isPayed:Boolean
)

enum class DeliveryStatus {
    PENDING,
    IN_PROGRESS,
    DELIVERED,
    FAILED
}