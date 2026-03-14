package com.example.collegeportal.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    fun getCurrentDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val dayFormat = SimpleDateFormat("EEEE", Locale("ru"))
        return dayFormat.format(date).uppercase()
    }
}
