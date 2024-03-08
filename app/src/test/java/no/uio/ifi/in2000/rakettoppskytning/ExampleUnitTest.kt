package no.uio.ifi.in2000.rakettoppskytning
import android.util.Log
import no.uio.ifi.in2000.rakettoppskytning.data.LevelData
import no.uio.ifi.in2000.rakettoppskytning.data.getShearWind
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

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

}