package no.uio.ifi.in2000.rakettoppskytning.data.settings

import kotlinx.coroutines.flow.firstOrNull
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Thresholds
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdValues

/**
 * Converts database values into a hashMap
 * */
suspend fun getThresholdValues(thresholdsDao: ThresholdsDao): ThresholdValues {
    val threshold = thresholdsDao.getThresholdById(1).firstOrNull() // Retrieve the threshold with ID 1

    return if (threshold != null) {
        // If threshold with ID 1 exists, create ThresholdValues from the retrieved data
        ThresholdValues(
            hashMapOf(
                ThresholdType.MAX_PRECIPITATION.name to threshold.percipitation.toDouble(),
                ThresholdType.MAX_HUMIDITY.name to threshold.humidity.toDouble(),
                ThresholdType.MAX_WIND.name to threshold.wind.toDouble(),
                ThresholdType.MAX_SHEAR_WIND.name to threshold.shearWind.toDouble(),
                ThresholdType.MAX_DEW_POINT.name to threshold.dewpoint.toDouble()
            )
        )
    } else {
        // If threshold with ID 1 doesn't exist, return default values
        thresholdsDao.insertThresholds(
            mapToDatabaseObject(getDefaultThresholdValues())
        )

        return getDefaultThresholdValues()

    }
}

/**
This function generates and returns default threshold values using a predefined set of values.*/
fun getDefaultThresholdValues(): ThresholdValues {
    val map = hashMapOf(
        ThresholdType.MAX_PRECIPITATION.name to 0.0,
        ThresholdType.MAX_HUMIDITY.name to 90.0,
        ThresholdType.MAX_WIND.name to 20.0,
        ThresholdType.MAX_SHEAR_WIND.name to 25.0,
        ThresholdType.MAX_DEW_POINT.name to 5.0
    )

    return ThresholdValues(map)
}

/** This function converts a ThresholdValues object to a Thresholds database object using a map of values.*/
fun mapToDatabaseObject(values: ThresholdValues): Thresholds {
    val map = values.valueMap

    return Thresholds(
        percipitation = map[ThresholdType.MAX_PRECIPITATION.name].toString(),
        humidity = map[ThresholdType.MAX_HUMIDITY.name].toString(),
        wind = map[ThresholdType.MAX_WIND.name].toString(),
        shearWind = map[ThresholdType.MAX_SHEAR_WIND.name].toString(),
        dewpoint = map[ThresholdType.MAX_DEW_POINT.name].toString(),
        id = 1
    )
}