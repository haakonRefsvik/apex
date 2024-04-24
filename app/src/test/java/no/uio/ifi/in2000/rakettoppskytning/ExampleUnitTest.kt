package no.uio.ifi.in2000.rakettoppskytning

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.findLowerUpperLevel
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.getLevelRatios
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.getNearestLevelData
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.simulateTrajectory
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.getForecast
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.LocationForecast
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getShearWind
import no.uio.ifi.in2000.rakettoppskytning.model.getDayAndMonth
import no.uio.ifi.in2000.rakettoppskytning.model.getDayName
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertTrue(true)
    }

    @Test
    fun altitudeTest() {

        val level = LevelData(10000.0)
        level.tempValueKelvin = (-1.4 + 273.15)

        val h = level.getLevelHeightInMeters()
        val expected = 18420.54

        assertEquals(expected, h, 0.1)
    }

    @Test
    fun testShearWind() {

        val expected = 19.9483

        val lowerLevel = LevelData(85000.0)
        val upperLevel = LevelData(75000.0)
        lowerLevel.uComponentValue = -10.0188
        lowerLevel.vComponentValue = 2.61442
        upperLevel.uComponentValue = -8.97321
        upperLevel.vComponentValue = 3.48796

        val result = getShearWind(upperLevel, lowerLevel)

        assertEquals(expected, result, 0.1)
    }

    @Test
    fun testFavoriteCard() {
        assertEquals(true, true)
    }

    /*
    @Test
    fun testHoursBetweenDates(){
        val repo = SettingsRepository(db.thresholdsDao)
        val d1 = "2024-03-19T00:00:00Z"
        val d2 = "2024-03-20T00:00:00Z"

        val expected = 24

        val result = calculateHoursBetweenDates(d1, d2)

        assertEquals(expected, result)
    }

    @Test
    fun testClosenessMinLimit(){
        val repo = SettingsRepository(db.thresholdsDao)
        val v = -1.4
        val l = -2.0

        val result = repo.getCloseness(v, l, max = false)
        val expected = 0.7

        assertEquals(expected, result, 0.01)
    }
    @Test
    fun testClosenessMaxLimit(){
        val repo = SettingsRepository(db.thresholdsDao)
        val v = 2.2
        val l = 0.0

        val result = repo.getCloseness(v, l)
        val expected = 1.0

        assertEquals(expected, result, 0.01)
    }
    @Test
    fun testDaysAHead(){

        val result = getNumberOfDaysAhead("2024-03-20T18:00:00Z")
        val expected = 1

        assertEquals(result, expected)
    }

     */

    @Test
    fun testWeekDayName() {
        val result = getDayName("2024-04-10", 0)
        val expected = "Wednesday"

        assertEquals(result, expected)
    }

    @Test
    fun testDayAndMonth() {
        val date = "2022-08-15T12:34:56Z"
        val result = getDayAndMonth(date)
        val expected = "15.08"

        assertEquals(expected, result)
    }

    @Test
    fun testTri() {
        val levelDatas = hashMapOf<Double, LevelData>()
        levelDatas[850.0] = LevelData(850.0)

        val tra: List<Point> = simulateTrajectory(
            burnTime = 12.0,
            launchAngle = 80.0,
            launchDir = 0.0,
            altitude = 0.0,
            thrust = 4500.0,
            apogee = 3500.0,
            mass = 100.0,
            dt = 0.1,
            allLevels = levelDatas
        )

        assertEquals(565, tra.size)
    }

    @Test
    fun testGetNearestLevel() {
        val l0 = LevelData(101325.5)
        l0.tempValueKelvin = 273.0
        val l1 = LevelData(85000.0)
        l1.tempValueKelvin = 273.0
        val l2 = LevelData(75000.0)
        l2.tempValueKelvin = 273.0
        val h =
            hashMapOf<Double, LevelData>(Pair(101325.5, l0), Pair(85000.0, l1), Pair(75000.0, l2))

        val res1 = getNearestLevelData(h, 0.0)

        assertEquals(l0, res1)

        val res2 = getNearestLevelData(h, 1500.0)

        assertEquals(l1, res2)
    }

    @Test
    fun testRatios() {
        val l1 = 600.0
        val l2 = 1400.0

        val res = getLevelRatios(l1, l2, 800.0)
        assertEquals(Pair(0.75, 0.25), res)
    }


    @Test
    fun findLayers() {
        val l1 = LevelData(85000.0)
        l1.tempValueKelvin = 273.0
        val l0 = LevelData(101325.5)
        l0.tempValueKelvin = 273.0
        val l2 = LevelData(75000.0)
        l2.tempValueKelvin = 273.0
        val list = listOf(l0, l1, l2)

        val res = findLowerUpperLevel(list, 1500.0)

        list.forEach { println(it.pressurePascal) }

        assertEquals(Pair(l1, l2), res)
    }

}