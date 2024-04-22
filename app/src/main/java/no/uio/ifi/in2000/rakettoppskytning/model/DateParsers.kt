package no.uio.ifi.in2000.rakettoppskytning.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.joda.time.Hours
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale


fun calculateHoursBetweenDates(vpDate: String, fcDate: String): Int {
    return try {
        val dateTime1 = ZonedDateTime.parse(vpDate)
        val dateTime2 = ZonedDateTime.parse(fcDate)

        ChronoUnit.HOURS.between(dateTime1, dateTime2).toInt()
    } catch (e: Exception) {
        -1
    }
}

fun getHourFromDate(date: String): Int {
    return try {
        ZonedDateTime.parse(date).hour
    } catch (e: Exception) {
        return -1
    }
}

fun getNumberOfDaysAhead(dateString: String): Int {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val currentDate = LocalDate.now()
    val givenDate = LocalDate.parse(dateString, formatter)
    val period = Period.between(currentDate, givenDate)
    return period.days
}

fun getDayAndMonth(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    val parsedDateTime = LocalDateTime.parse(dateString, formatter)

    val day = parsedDateTime.dayOfMonth
    val month = parsedDateTime.monthValue

    return parsedDateTime.format(DateTimeFormatter.ofPattern("dd.MM"))
}

fun getDayName(dateString: String, dayOffset: Int): String{
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val date = sdf.parse(dateString)
    val calendar = Calendar.getInstance()
    if (date != null) {
        calendar.time = date
    }

    val dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + dayOffset - 1) % 7
    val weekdays = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    return weekdays[if (dayOfWeek < 0) dayOfWeek + 7 else dayOfWeek]
}

/** Returns an appropriate display of the date*/

fun formatDate(date: String): String {
    return try {
        val daysAhead = getNumberOfDaysAhead(date)
        val formattedDate: String = when{
            daysAhead <= 6-> getDayName(date, 0)
            daysAhead > 6 -> getDayAndMonth(date)
            else -> ""
        }

        formattedDate
    }catch (e: Exception){
        date
    }

}

