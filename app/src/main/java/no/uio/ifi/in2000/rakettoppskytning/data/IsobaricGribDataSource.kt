package no.uio.ifi.in2000.rakettoppskytning.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ucar.nc2.grib.grib2.Grib2Gds
import ucar.nc2.grib.grib2.Grib2RecordScanner
import ucar.unidata.io.RandomAccessFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.atan2
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


@Serializable
data class LatestUri(
    val uri: String
)


suspend fun getGrib(){
    val apiKey = API_KEY().getKey()

    val client = HttpClient(CIO){

        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            header("X-Gravitee-API-Key", apiKey)
        }

        install(ContentNegotiation){
            json(Json{
                ignoreUnknownKeys = true
            }
            )

        }
    }

    val urlAvailable: String = "weatherapi/isobaricgrib/1.0/available.json?type=grib2"
    val latestUri: List<LatestUri> = client.get(urlAvailable).body()?: throw Exception("Could not find the latest uri for the grib files")
    val inputStream: InputStream = client.get(latestUri.first().uri).body()?: throw Exception("Could not access the grib file")

    val file = File.createTempFile("temp", ".grib2") // Creates a temporary file
    FileOutputStream(file).use { outputStream ->
        inputStream.copyTo(outputStream)
    }


    /*
    val gribFile = StreamedGribFile("testdata", "test")
    gribFile.prepareImportFromStream(inputStream, 0);
    val s7 = gribFile.section4

    Log.d("Date: " , s7.sectionlength.toString())
     */

    parseMedNetCdf(file)

}


fun parseMedNetCdf(file: File){
    val m = VerticalProfile(60.15, 11.95, file)
    Log.d("as", m.toString())
}



/** Temperature, windspeed and winddirection for a given isobaric layer*/
class LevelData(val pressurePa: Double){

    var tempValueKelvin = 0.0
    var uComponentValue = 0.0
    var vComponentValue = 0.0
    var pressurePascal = pressurePa

    fun convertKelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    fun getLevelHeightInMeters(): Double{
        val R = 8.31432 // Universal gas constant in N⋅m/(mol⋅K)
        val M = 0.0289644 // Molar mass of Earth's air in kg/mol
        val g = 9.80665 // Acceleration due to gravity in m/s^2
        val pressureSeaLevel = 101325
        val term = (R * getTemperatureCelsius()) / (g * M)
        val altitude = ln(pressurePascal / pressureSeaLevel) * (-R * getTemperatureCelsius()) / (g * M)
        val referenceAltitude = 0.0 // Assuming reference level is sea level

        return referenceAltitude + altitude

    }
    fun calculateWindSpeed(uComponent: Double, vComponent: Double): Double {
        return sqrt(uComponent.pow(2) + vComponent.pow(2))
    }

    fun calculateWindDirection(uComponent: Double, vComponent: Double): Double {
        var windDirInDegrees = Math.toDegrees(atan2(uComponent, vComponent))
        if (windDirInDegrees < 0) {
            windDirInDegrees += 360  // Ensure the direction is in the range [0, 360)
        }
        return windDirInDegrees
    }

    fun getTemperatureCelsius(): Double {
        return convertKelvinToCelsius(tempValueKelvin)
    }
    /** Returns wind-speed in m/s for the isobaric layer*/
    fun getWindSpeed(): Double {
        return calculateWindSpeed(uComponentValue, vComponentValue)
    }
    /** Returns wind-direction in degrees (0 is north) for the isobaric layer*/
    fun getWindDir(): Double {
        return calculateWindDirection(uComponentValue, vComponentValue)
    }

    enum class ParameterName(val displayName: String) {
        TEMPERATURE("Temperature in Kelvin"),
        U_COMPONENT("U Component of Wind"),
        V_COMPONENT("V Component of Wind"),
    }

    private fun getParameterString(parameterNumber: Int): String{
        return when (parameterNumber) {
            2 -> "TEMPERATURE"
            3 -> "U_COMPONENT"
            0 -> "V_COMPONENT"
            else -> "Unknown parameter name"
        }
    }

    fun addValue(parameterNumber: Int, value: Double){
        val parameterName = when (parameterNumber) {
            0 -> ParameterName.TEMPERATURE
            2-> ParameterName.U_COMPONENT
            3 -> ParameterName.V_COMPONENT
            else -> throw IllegalArgumentException("Unknown parameter number: $parameterNumber")
        }

        when (parameterName) {
            ParameterName.TEMPERATURE -> tempValueKelvin = value
            ParameterName.U_COMPONENT -> uComponentValue = value
            ParameterName.V_COMPONENT -> vComponentValue = value
        }
    }
}

class VerticalProfile(lat: Double, lon: Double, file: File){
    val verticalProfileMap = getVerticalProfileMap(lat, lon, file)
    val lat = lat
    val lon = lon
    /** Gets all the levels of the profile in Pascal */
    fun getAllLevels(): DoubleArray {
        return verticalProfileMap.keys.sortedDescending().toDoubleArray()
    }

    override fun toString(): String {

        var r = "---    Vertical profile for lat: $lat, lon: $lon     ---\n"

        val sortedEntries = verticalProfileMap.entries.sortedByDescending { it.key }

        for((key, value) in sortedEntries){
            val windDir = value.getWindDir()
            val windSpeed = value.getWindSpeed()
            val temp = value.getTemperatureCelsius()

            r += "\n$key Pressure(Pa):"
            r += "\n    - Temperature: $temp"
            r += "\n    - WindSpeed: $windSpeed"
            r += "\n    - WindDirection: $windDir"
        }

        return r
    }

}

/** Makes a new isobaric layer (if needed) and gives values to the LevelData-object*/
fun addLevelToMap(verticalMap: HashMap<Double, LevelData>, value: Double, level: Double, parameterNumber: Int){
    if(verticalMap.containsKey(level)) {
        verticalMap[level]?.addValue(parameterNumber, value)
        return
    }

    val levelData = LevelData(level)
    levelData.addValue(parameterNumber, value)
    verticalMap[level] = levelData
}

/** Makes a hashmap with key: Isobaric layer (in Pascal), and a LevelData-object based on lon and lat*/
fun getVerticalProfileMap(lat: Double, lon: Double, file: File): HashMap<Double, LevelData>{

    val raf = RandomAccessFile(file.absolutePath, "r")
    val scan = Grib2RecordScanner(raf)
    val verticalMap = HashMap<Double, LevelData>()

    while (scan.hasNext()) {
        val gr2 = scan.next()
        val levelPa = (gr2.pds.levelValue1)
        val parameterNumber = gr2.pds.parameterNumber
        val drs = gr2.dataRepresentationSection
        val data = gr2.readData(raf, drs.startingPosition)
        val index: Int = getDataIndexFromLatLon(lat, lon, gr2.gds?: throw Exception("Grib Definition Section not found"))
        val value = data[index].toDouble()
        addLevelToMap(verticalMap, value, levelPa, parameterNumber)
    }
    raf.close()
    return verticalMap
}


fun normalizeLon(lon: Double): Double{
    if (lon > 180) { return lon - 360 }
    if (lon < -180) { return lon + 360 }
    return lon
}

fun normalizeLat(lat: Double): Double{
    if (lat > 90) { return lat - 180 }
    if (lat < -90) { return lat + 180 }
    return lat
}
fun getDataIndexFromLatLon(lat: Double, lon: Double, gds: Grib2Gds): Int {
    // X = LON
    // Y = LAT
    val gridDef = gds.makeHorizCoordSys() // skaffer meta-data for grib-filens struktur
    if (gridDef.scanMode != 64) {
        throw Exception("Scan-mode ${gridDef.scanMode} not supported")
    }
    val numGridPoints = gridDef.gdsNumberPoints  // antall grid-celler totalt (samme som nx * ny)
    val startX = normalizeLon(gridDef.startX)    // hvilken lon/lat grib filen starter på
    val startY = normalizeLat(gridDef.startY)
    val dx = gridDef.dx // avstand (i lat/lon) mellom hver grib-celle
    val dy = gridDef.dy
    val nx = gridDef.nx // totalt antall celler horizontalt/vertikalt
    //val ny = gridDef.ny

    val ix: Int = ((lon - startX) / dx).roundToInt()
    val iy: Int = ((lat - startY) / dy).roundToInt()

    val index = ix + (iy * nx)

    if(index >= numGridPoints){
        throw IndexOutOfBoundsException("You have to be inside of lat 55.350 lon -1.450 and lat 64.250 lon 14.450")
    }

    return ix + (iy * nx) // returnerer riktig index
}
