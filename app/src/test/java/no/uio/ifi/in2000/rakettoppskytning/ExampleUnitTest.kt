package no.uio.ifi.in2000.rakettoppskytning
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.getOldestDate
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getShearWind
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherForeCastLocationRepo
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
    fun altitudeTest(){

        val level = LevelData(10000.0)
        level.tempValueKelvin = (-1.4 + 273.15)

        val h = level.getLevelHeightInMeters()
        val expected = 18420.54

        assertEquals(expected, h, 0.1)
    }

    @Test
    fun testShearWind(){

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
    fun testDates(){
        val dates = listOf("2024-03-09T15:00:00Z", "2024-03-10T03:00:00Z", "2024-03-10T00:00:00Z")

        val expected = "2024-03-09T15:00:00Z"
        val result = getOldestDate(dates)

        assertEquals(expected, result)
    }
    @Test
    fun testHoursBetweenDates(){
        val repo = WeatherForeCastLocationRepo(ThresholdRepository())
        val d1 = "2024-03-19T00:00:00Z"
        val d2 = "2024-03-20T00:00:00Z"

        val expected = 24

        val result = repo.calculateHoursBetweenDates(d1, d2)

        assertEquals(expected, result)
    }

    @Test
    fun testClosenessMinLimit(){
        val repo = WeatherForeCastLocationRepo(ThresholdRepository())
        val v = -1.4
        val l = -2.0

        val result = repo.getCloseness(v, l, false)
        val expected = 0.7

        assertEquals(expected, result, 0.01)
    }
    @Test
    fun testClosenessMaxLimit(){
        val repo = WeatherForeCastLocationRepo(ThresholdRepository())
        val v = -0.2
        val l = 2.0

        val result = repo.getCloseness(v, l)
        val expected = 0.1

        assertEquals(expected, result, 0.01)
    }
}