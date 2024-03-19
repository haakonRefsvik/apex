package no.uio.ifi.in2000.rakettoppskytning.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


@RequiresApi(Build.VERSION_CODES.O)
fun calculateHoursBetweenDates(vpDate: String, fcDate: String): Int {
    val dateTime1 = ZonedDateTime.parse(vpDate)?: return -1
    val dateTime2 = ZonedDateTime.parse(fcDate)?: return -1

    return ChronoUnit.HOURS.between(dateTime1, dateTime2).toInt()
}

@RequiresApi(Build.VERSION_CODES.O)
fun getHourFromDate(date: String): Int {
    return ZonedDateTime.parse(date).hour
}