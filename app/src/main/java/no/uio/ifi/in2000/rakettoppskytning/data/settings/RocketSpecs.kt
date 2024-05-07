package no.uio.ifi.in2000.rakettoppskytning.data.settings

import kotlinx.coroutines.flow.firstOrNull
import no.uio.ifi.in2000.rakettoppskytning.data.database.RocketSpecsDao
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecs
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecValues

fun getDefaultRocketSpecs(): RocketSpecValues {
    val map = hashMapOf(
        RocketSpecType.APOGEE.name to 3500.0,
        RocketSpecType.LAUNCH_DIRECTION.name to 90.0,
        RocketSpecType.LAUNCH_ANGLE.name to 80.0,
        RocketSpecType.THRUST_NEWTONS.name to 4500.0,
        RocketSpecType.BURN_TIME.name to 12.0,
        RocketSpecType.DRY_WEIGHT.name to 100.0,
        RocketSpecType.WET_WEIGHT.name to 130.0,
        RocketSpecType.RESOLUTION.name to 1.0,
    )

    return RocketSpecValues(map)
}

fun mapToDatabaseObject(values: RocketSpecValues): RocketSpecs {
    val map = values.valueMap

    return RocketSpecs(
        apogee = map[RocketSpecType.APOGEE.name].toString(),
        launchAngle = map[RocketSpecType.LAUNCH_ANGLE.name].toString(),
        launchDirection = map[RocketSpecType.LAUNCH_DIRECTION.name].toString(),
        thrust = map[RocketSpecType.THRUST_NEWTONS.name].toString(),
        burntime = map[RocketSpecType.BURN_TIME.name].toString(),
        dryWeight = map[RocketSpecType.DRY_WEIGHT.name].toString(),
        wetWeight = map[RocketSpecType.WET_WEIGHT.name].toString(),
        resolution = map[RocketSpecType.RESOLUTION.name].toString(),
        id = 1
    )
}


suspend fun getRocketSpecValues(rocketSpecsDao: RocketSpecsDao): RocketSpecValues {
    val rocketSpecs = rocketSpecsDao.getRocketSpecsById(1).firstOrNull() // Retrieve the threshold with ID 1

    return if (rocketSpecs != null) {
        // If threshold with ID 1 exists, create ThresholdValues from the retrieved data
        RocketSpecValues(
            hashMapOf(
                RocketSpecType.APOGEE.name to rocketSpecs.apogee.toDouble(),
                RocketSpecType.THRUST_NEWTONS.name to rocketSpecs.thrust.toDouble(),
                RocketSpecType.LAUNCH_ANGLE.name to rocketSpecs.launchAngle.toDouble(),
                RocketSpecType.LAUNCH_DIRECTION.name to rocketSpecs.launchDirection.toDouble(),
                RocketSpecType.BURN_TIME.name to rocketSpecs.burntime.toDouble(),
                RocketSpecType.DRY_WEIGHT.name to rocketSpecs.dryWeight.toDouble(),
                RocketSpecType.WET_WEIGHT.name to rocketSpecs.wetWeight.toDouble(),
                RocketSpecType.RESOLUTION.name to rocketSpecs.resolution.toDouble()
            )
        )
    } else {
        // If threshold with ID 1 doesn't exist, return default values
        rocketSpecsDao.insertRocketSpecs(
            mapToDatabaseObject(getDefaultRocketSpecs())
        )

        return getDefaultRocketSpecs()

    }
}