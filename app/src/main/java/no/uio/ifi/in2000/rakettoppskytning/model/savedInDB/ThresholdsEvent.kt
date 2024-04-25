package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB


sealed interface ThresholdsEvent {
    object SaveThreshold: ThresholdsEvent
    data class SetPercipitation(val percipitation: String): ThresholdsEvent
    data class SetHumidity(val humidity: String): ThresholdsEvent
    data class SetWind(val wind: String): ThresholdsEvent
    data class SetShearWind(val shearWind: String): ThresholdsEvent
    data class SetDewpoint(val dewpoint: String): ThresholdsEvent
}

