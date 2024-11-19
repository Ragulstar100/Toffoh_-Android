package com.manway.toffoh.admin.ui

import java.sql.Timestamp

import java.util.*

fun String.toDate(): Date {
    return Date(Timestamp.valueOf(this).time)
}


