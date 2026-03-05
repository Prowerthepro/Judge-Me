package com.socialscreencontrol.core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun todayKey(): String = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

fun Int.minutesAsReadable(): String {
    val h = this / 60
    val m = this % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
