package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB


sealed interface ThresholdsEvent {
    object SaveThreshold: ThresholdsEvent
    data class SetNedbor(val nedbor: String): ThresholdsEvent
    data class SetLuftfuktighet(val luftfuktighet: String): ThresholdsEvent
    data class SetVind(val vind: String): ThresholdsEvent
    data class SetShearWind(val shearWind: String): ThresholdsEvent
    data class SetDuggpunkt(val duggpunkt: String): ThresholdsEvent
}

