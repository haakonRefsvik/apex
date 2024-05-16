package no.uio.ifi.in2000.rakettoppskytning.data.formatting

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale


/**
 * DATES ARE ON ISO-STANDARD FORMAT LIKE 2024-04-27T14:00:00Z
 * */

fun calculateHoursBetweenDates(vpDate: String, fcDate: String): Int {
    return try {
        val dateTime1 = ZonedDateTime.parse(vpDate)
        val dateTime2 = ZonedDateTime.parse(fcDate)

        ChronoUnit.HOURS.between(dateTime1, dateTime2).toInt()
    } catch (e: Exception) {
        -1
    }
}

val formatter: DateTimeFormatter? = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)


/** Returns current time to the nearest hour */
fun getCurrentDate(minusHours: Int = 0): String {
    val currentDateTime = LocalDateTime.now().minusHours(minusHours.toLong())
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val str = currentDateTime.format(formatter)
    return "${str.substring(0, 13)}:00:00Z"
}

/** Returns hour from the date */
fun getHourFromDate(date: String): Int {
    return try {
        ZonedDateTime.parse(date).hour
    } catch (e: Exception) {
        return -1
    }
}

/** This function calculates the number of days between the current date and a given date string. */
fun getNumberOfDaysAhead(dateString: String): Int {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val currentDate = LocalDate.now()
    val givenDate = LocalDate.parse(dateString, formatter)
    val period = Period.between(currentDate, givenDate)
    return period.days
}

/** Returns day and month from the date */
fun getDayAndMonth(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val parsedDateTime = LocalDateTime.parse(dateString, formatter)
    return parsedDateTime.format(DateTimeFormatter.ofPattern("dd.MM"))
}

/** Returns name of day from the date */
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

/** Returns hour and minute from the date */
fun extractHourAndMinutes(dateString: String): String {
    return try {
        return dateString.substring(11, 16)
    } catch (e: Exception) {
        dateString
    }
}
