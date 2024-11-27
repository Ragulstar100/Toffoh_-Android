package com.manway.toffoh.admin.ui

import kotlinx.datetime.LocalDateTime
import java.sql.Timestamp

import java.util.*

fun String.toDate(): Date {
    return Date(Timestamp.valueOf(this).time)
}

//Indian Time
fun LocalDateTime.toDate(): Date {
    val d=Date(date.year,date.monthNumber,date.dayOfMonth,time.hour,time.minute,time.second)
    return Date(d.time+(9*60*1000))
}
