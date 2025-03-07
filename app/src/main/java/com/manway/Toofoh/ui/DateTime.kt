package com.manway.toffoh.admin.ui

import android.annotation.SuppressLint
import kotlinx.datetime.LocalDateTime
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import java.util.*
import kotlin.text.format


@SuppressLint("NewApi")
fun String.toDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSZ")
    val dateTime = LocalDateTime.parse(this)
    return "${dateTime.dayOfWeek.name} ${dateTime.date.dayOfMonth} ${dateTime.hour}: ${dateTime.minute}"
}

//Indian Time
fun LocalDateTime.toDate(): Date {
    val d=Date(date.year,date.monthNumber,date.dayOfMonth,time.hour,time.minute,time.second)
    return Date(d.time+(9*60*1000))
}

fun Date.formatDate(): String {
    val formatter = SimpleDateFormat("EEE dd MMMM yyyy hh:mm:ss a", Locale.getDefault())
    return formatter.format(this)
}

/** not implimented if you only get api form google map time get absalute**/
fun getOnlineTimeNow(): Long {
    return Date().time
}
