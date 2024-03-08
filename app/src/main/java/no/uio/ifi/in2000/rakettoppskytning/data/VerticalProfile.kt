package no.uio.ifi.in2000.rakettoppskytning.data

import android.util.Log
import ucar.nc2.grib.grib2.Grib2Gds
import ucar.nc2.grib.grib2.Grib2Record
import ucar.nc2.grib.grib2.Grib2RecordScanner
import ucar.unidata.io.RandomAccessFile
import java.io.File
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


/** Temperature, windspeed and wind-direction for a given isobaric layer*/

class VerticalProfile(val lat: Double, val lon: Double, file: File){
    private val verticalProfileMap = getVerticalProfileMap(lat, lon, file)
    private val raf = RandomAccessFile(file.absolutePath, "r")
    private val scan = Grib2RecordScanner(raf)
    private val record: Grib2Record = scan.next()

    val minute = record.id.minute
    val hour = record.id.hour
    val day = record.id.day
    val month = record.id.month
    val year = record.id.year

    /** Gets all the levels of the profile in Pascal */
    private fun getAllLevels(): DoubleArray {
        return verticalProfileMap.keys.sortedDescending().toDoubleArray()
    }

    private fun findLevel(level: Double): LevelData {
        return verticalProfileMap[level]?: throw Exception ("Level not found")
    }

    /**
     * Returns shear-wind (in m/s) from all isobaric layers.
     * index 0 is the shear-wind from the bottom two layers
     * */
    fun getAllSheerWinds(): List<ShearWind>{
        val levelList = getAllLevels()
        val shearWindList = mutableListOf<ShearWind>()

        for (i in 0 until levelList.size - 1) {
            val lowerLayer = findLevel(levelList[i])
            val upperLayer = findLevel(levelList[i + 1])
            val windSpeed = getShearWind(lowerLayer, upperLayer)
            val shearWind = ShearWind(lowerLayer, upperLayer, windSpeed)

            shearWindList.add(shearWind)
        }

        return shearWindList
    }

    fun getMaxSheerWind(): ShearWind {
        return getAllSheerWinds().maxBy { it.windSpeed }
    }
    /** Returns the map of the vertical profile */
    fun getMap(): HashMap<Double, LevelData> {
        return verticalProfileMap
    }

    /** Returns the position (lat, lon) of where the vertical profile is from*/
    fun getPosition(): Pair<Double, Double>{
        return Pair(lat, lon)
    }

    override fun toString(): String {

        var r = "---    Vertical profile for lat: $lat, lon: $lon     ---\n"
        r += "Date: ${getStandardDate(day, month, year)} ${getStandardTime(hour, minute, 0)}"


        val sortedEntries = verticalProfileMap.entries.sortedByDescending { it.key }

        for((key, value) in sortedEntries){
            val windDir = value.getWindDir()
            val windSpeed = value.getWindSpeed()
            val temp = value.getTemperatureCelsius()
            val h = value.getLevelHeightInMeters()

            /*
            r += "\n$key Pressure(Pa):"
            r += "\n    - Height (meters):  $h"
            r += "\n    - Temperature:      $temp"
            r += "\n    - WindSpeed:        $windSpeed"
            r += "\n    - WindDirection:    $windDir"


             */
            r += "\n$key Pressure(Pa):"
            r += "\n    - u:    ${value.uComponentValue}"
            r += "\n    - v:    ${value.vComponentValue}"
            r += "\n    - t:    ${value.tempValueKelvin}"

        }

        return r
    }
}

/** Makes a new isobaric layer in the map (if needed) and gives values to the LevelData-object*/
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
    val gridDef = gds.makeHorizCoordSys() // Gets the structure of the grids
    if (gridDef.scanMode != 64) {
        throw Exception("Scan-mode ${gridDef.scanMode} not supported")
    }
    val numGridPoints = gridDef.gdsNumberPoints  // amount of grid-cells (same as nx * ny)
    val startX = normalizeLon(gridDef.startX)    // starting position of the grid-cell
    val startY = normalizeLat(gridDef.startY)
    val dx = gridDef.dx // space between each cell (in lon/lat)
    val dy = gridDef.dy
    val nx = gridDef.nx // total amount of cells in the x-direction

    val ix: Int = ((lon - startX) / dx).roundToInt()
    val iy: Int = ((lat - startY) / dy).roundToInt()

    val index = ix + (iy * nx)

    if(index >= numGridPoints){
        throw IndexOutOfBoundsException("You have to be inside of $startX, $startY and ${gridDef.endX}, ${gridDef.endY}")
    }

    return ix + (iy * nx) // returns the correct index
}

fun getStandardDate(day: Int, month: Int, year: Int): String {
    return String.format("%02d/%02d/%04d", day, month, year)
}

fun getStandardTime(hour: Int, minute: Int, second: Int): String {
    return String.format("%02d:%02d:%02d", hour, minute, second)
}
