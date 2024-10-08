package no.uio.ifi.in2000.rakettoppskytning.model.grib

import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Series
import ucar.nc2.grib.grib2.Grib2Gds
import ucar.nc2.grib.grib2.Grib2Record
import ucar.nc2.grib.grib2.Grib2RecordScanner
import ucar.nc2.time.CalendarPeriod
import ucar.unidata.io.RandomAccessFile
import java.io.File
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


/**
 * Returns the date of a given GRIB-file
 * */
fun getTime(file: File): String {
    return try {
        val raf = RandomAccessFile(file.absolutePath, "r")
        val scan = Grib2RecordScanner(raf)
        val record: Grib2Record = scan.next()
        val referenceDate = record.id.referenceDate
        val hourOffset = CalendarPeriod.Hour.multiply(record.pds.forecastTime)
        referenceDate.add(hourOffset).toString()
    } catch (e: Exception) {
        ""
    }

}

/** Temperature, wind-speed and wind-direction for a given isobaric layer*/
data class VerticalProfile(
    val heightLimitMeters: Int = Int.MAX_VALUE,
    val lat: Double,
    val lon: Double,
    val verticalProfileMap: HashMap<Double, LevelData>,
    val time: String
) {

    private var groundLevel: LevelData? = null
    private var allShearWinds: List<ShearWind> = getAllSheerWinds()

    /** Gets all the levels of the profile in Pascal */
    private fun getAllLevels(): DoubleArray {
        return verticalProfileMap.keys.sortedDescending().toDoubleArray()
    }

    /** This function collects all level data and returns them as a list.*/
    fun getAllLevelData(): List<LevelData> {
        return verticalProfileMap.values.toList()
    }

    /** This function finds level data by level value or throws an exception if not found. */
    private fun findLevel(level: Double): LevelData {
        return verticalProfileMap[level] ?: throw Exception("Level not found")
    }

    /** This function adds ground information to the vertical profile if not already present. */
    fun addGroundInfo(series: Series) {
        if (groundLevel != null) {
            return
        }
        if (verticalProfileMap.isEmpty()) {
            return
        }
        val data = series.data.instant.details
        val pressurePascal =
            data.airPressureAtSeaLevel * 100 // forecast-pressure is in hecto-pascal
        val tempKelvin = data.airTemperature + 273.15
        val uCom = data.windSpeed * cos(Math.toRadians(data.windFromDirection))
        val vCom = data.windSpeed * sin(Math.toRadians(data.windFromDirection))

        val gl = LevelData(pressurePascal = pressurePascal)
        gl.uComponentValue = uCom
        gl.vComponentValue = vCom
        gl.tempValueKelvin = tempKelvin
        groundLevel = gl

        verticalProfileMap[pressurePascal] = gl

        for (value in verticalProfileMap.values) {
            value.seaPressurePa = pressurePascal
            value.groundLevelTempKelvin = tempKelvin
        }

        allShearWinds = getAllSheerWinds()
    }

    /**
     * Returns shear-wind (in m/s) from all isobaric layers.
     * index 0 is the shear-wind from the bottom two layers
     * */
    fun getAllSheerWinds(): List<ShearWind> {
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

    /**
    This function returns the maximum shear wind.
     */
    fun getMaxSheerWind(): ShearWind {
        return getAllSheerWinds().maxBy { it.windSpeed }
    }

    /** This function creates a string summarizing a vertical profile's data. */
    override fun toString(): String {

        var r = "---    Vertical profile for lat: $lat, lon: $lon     ---\n"
        r += "ForecastTime: $time\n"

        val sortedEntries = verticalProfileMap.entries.sortedByDescending { it.key }

        for ((key, value) in sortedEntries) {
            val windDir = value.getWindDir()
            val windSpeed = value.getWindSpeed()
            val temp = value.getTemperatureCelsius()
            val h = value.getLevelHeightInMeters()

            r += "\n$key Pressure(Pa):"
            r += "\n    - Height (meters):  $h"
            r += "\n    - Temperature:      $temp"
            r += "\n    - WindSpeed:        $windSpeed"
            r += "\n    - WindDirection:    $windDir"
        }

        return r
    }
}

/**
 * Makes a new isobaric layer in the map (if needed) and gives values to the LevelData-object
 * Parameters:
 * 0 -> tempValueKelvin
 * 2 -> uComponentValue
 * 3 -> vComponentValue
 * */
fun addLevelToMap(
    verticalMap: HashMap<Double, LevelData>,
    value: Double,
    level: Double,
    parameterNumber: Int,
) {
    if (verticalMap.containsKey(level)) {
        verticalMap[level]?.addValue(parameterNumber, value)
        return
    }
    val levelData = LevelData(level)

    levelData.addValue(parameterNumber, value)
    verticalMap[level] = levelData
}

/** Makes a hashmap with key: Isobaric layer (in Pascal), and a LevelData-object based on lon and lat*/
fun getVerticalProfileMap(
    lat: Double,
    lon: Double,
    file: File,
    maxHeight: Int
): HashMap<Double, LevelData> {

    val raf = try {
        RandomAccessFile(file.absolutePath, "r")
    } catch (e: Exception) {
        throw Exception(e.stackTraceToString())
    }
    val scan = Grib2RecordScanner(raf)
    val verticalMap = HashMap<Double, LevelData>()
    var lastHeight = 0

    while (scan.hasNext()) {
        val gr2 = scan.next()
        val levelPa = (gr2.pds.levelValue1)
        val height = getApproximateHeight(levelPa).roundToInt()

        if (lastHeight > maxHeight && height > maxHeight) {
            lastHeight = height
            continue
        }
        val parameterNumber = gr2.pds.parameterNumber
        val drs = gr2.dataRepresentationSection
        val data = gr2.readData(raf, drs.startingPosition)

        val index: Int = getDataIndexFromLatLon(
            lat,
            lon,
            gr2.gds ?: throw Exception("Grib Definition Section not found")
        )

        val value = try {
            data[index].toDouble()
        } catch (e: Exception) {
            return hashMapOf()
        }

        addLevelToMap(verticalMap, value, levelPa, parameterNumber)

        lastHeight = height
    }

    raf.close()
    return verticalMap
}

/** This function formats longitude values to be within the range [-180, 180].
 * */
fun formatLon(lon: Double): Double {
    if (lon > 180) {
        return lon - 360
    }
    if (lon < -180) {
        return lon + 360
    }
    return lon
}

/**
 * This function formats latitude values to be within the range [-90, 90].
 * */
fun formatLat(lat: Double): Double {
    if (lat > 90) {
        return lat - 180
    }
    if (lat < -90) {
        return lat + 180
    }
    return lat
}

/**
 * Function that finds where in the grib-file it has to look (at what index)
 * to get data from a specific lat, lon
 * */
fun getDataIndexFromLatLon(lat: Double, lon: Double, gds: Grib2Gds): Int {
    // X = LON
    // Y = LAT
    val gridDef = gds.makeHorizCoordSys() // Gets the structure of the grids
    if (gridDef.scanMode != 64) {
        throw Exception("Scan-mode ${gridDef.scanMode} not supported")
    }
    gridDef.gdsNumberPoints  // amount of grid-cells (same as nx * ny)
    val startX = formatLon(gridDef.startX)    // starting position of the grid-cell
    val startY = formatLat(gridDef.startY)
    val dx = gridDef.dx // space between each cell (in lon/lat)
    val dy = gridDef.dy
    val nx = gridDef.nx // total amount of cells in the x-direction

    val ix: Int = ((lon - startX) / dx).roundToInt()
    val iy: Int = ((lat - startY) / dy).roundToInt()

    return ix + (iy * nx)// returns the correct index
}