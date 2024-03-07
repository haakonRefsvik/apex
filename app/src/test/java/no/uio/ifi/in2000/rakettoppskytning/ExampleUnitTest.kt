package no.uio.ifi.in2000.rakettoppskytning
import org.junit.Test

import org.junit.Assert.*
import kotlin.math.ln

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

    fun altitudeTest(){
        val temperature = -1
        val pressure = 85000
        val referencePressure = 101325
        val R = 8.31432 // Universal gas constant in N⋅m/(mol⋅K)
        val M = 0.0289644 // Molar mass of Earth's air in kg/mol
        val g = 9.80665 // Acceleration due to gravity in m/s^2

        val ratio = pressure / referencePressure
        val t1 = (R * (temperature + 273.15))
        val t2 = ((g * M)) * ln(ratio.toDouble())
        val altitude = t1 / t2
        val referenceAltitude = 0.0 // Assuming reference level is sea level
        val h =  referenceAltitude + altitude
        val exp = 1399.513

        assertSame("Samme?", exp, t2)
    }

}