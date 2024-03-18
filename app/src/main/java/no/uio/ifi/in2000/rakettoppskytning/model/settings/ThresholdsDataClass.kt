package no.uio.ifi.in2000.rakettoppskytning.model.settings

data class ThresholdValues(
    /**Max nedbør i mm*/
    val maxPrecipitation: Double,
    /**Max luftfuktighet i %*/
    val maxHumidity: Double,
    /**Max vind i m/s*/
    val maxWind: Double,
    /**Max shearwind i m/s*/
    val maxShearWind: Double
)
