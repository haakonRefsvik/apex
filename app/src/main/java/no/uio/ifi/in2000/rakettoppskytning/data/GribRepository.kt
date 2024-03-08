package no.uio.ifi.in2000.rakettoppskytning.data

import java.io.File

class GribRepository {
    fun getVerticalProfile(file: File, lat: Double, lon: Double): VerticalProfile {
        return VerticalProfile(lat, lon, file)
    }



}