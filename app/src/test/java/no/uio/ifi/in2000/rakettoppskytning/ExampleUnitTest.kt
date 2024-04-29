package no.uio.ifi.in2000.rakettoppskytning
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.ui.platform.LocalContext
import androidx.media3.test.utils.TestUtil
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.Point
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.calculateAirDensity
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.findLowerUpperLevel
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.getLinearRatios
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.getNearestLevelData
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.getSigmoidRatios
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.mergeLevelData
import no.uio.ifi.in2000.rakettoppskytning.data.ballistic.simulateTrajectory
import no.uio.ifi.in2000.rakettoppskytning.data.database.AppDatabase
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteCardDao
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDao
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.model.getCurrentDate
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getShearWind
import no.uio.ifi.in2000.rakettoppskytning.model.getDayAndMonth
import no.uio.ifi.in2000.rakettoppskytning.model.getDayName
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteCard
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteCardViewModel
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import java.io.IOException

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
    fun testWeekDayName(){
        val result = getDayName("2024-04-10", 0)
        val expected = "Wednesday"

        assertEquals(result, expected)
    }

    @Test
    fun testDayAndMonth(){
        val date = "2022-08-15T12:34:56Z"
        val result = getDayAndMonth(date)
        val expected = "15.08"

        assertEquals(expected, result)
    }

    @Test
    fun getTime(){
        val exp = "2024-04-27T21:00:00Z"
        val result = getCurrentDate()

        assertEquals(exp, result)
    }

    /*
    @RunWith(AndroidJUnit4::class)
    class SimpleEntityReadWriteTest {
        private lateinit var favoriteCardDao: FavoriteCardDao
        private lateinit var favoriteDao: FavoriteDao
        private lateinit var db: AppDatabase

        @Before
        fun createDb() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java).build()
            favoriteCardDao = db.favoriteCardDao
            favoriteDao = db.favoriteDao

        }

        @After
        @Throws(IOException::class)
        fun closeDb() {
            db.close()
        }

        @Test
        @Throws(Exception::class)
        suspend fun writeUserAndReadInList() {
            val card: FavoriteCard = FavoriteCard("59.0", "11.0", "Now")
            val place: Favorite = Favorite("Blindern","59.0", "11.0", )

            favoriteCardDao.insertFavoriteCard(card)
            favoriteDao.upsertFavorite(place)

            val byName = favoriteDao.getFavoriteByLatLon("59.0", "11.0")

            assertEquals(byName.map { it?.name }.toString(), "Blindern")
        }
    }

     */

    @Test
    fun testTri(){
        val l0 = LevelData(101325.5)
        l0.tempValueKelvin = 273.0
        val l1 = LevelData(85000.0)
        l1.tempValueKelvin = 273.0
        val l2 = LevelData(75000.0)
        l2.tempValueKelvin = 273.0

        l0.vComponentValue = 10.0
        l1.vComponentValue = -10.0
        l2.vComponentValue = 10.0
        val list = mutableListOf<LevelData>()
        list.add(l0)
        list.add(l1)
        list.add(l2)

        val tra: List<Point> = simulateTrajectory(
            burnTime = 12.0,
            launchAngle = 80.0,
            launchDir = 0.0,
            altitude = 0.0,
            thrust = 4500.0,
            apogee = 3500.0,
            mass = 130.0,
            massDry = 100.0,
            dt = 0.1,
            allLevels = list,
        )

        assertEquals(true, tra.size in 201..999)
    }

    @Test
    fun testGetNearestLevel(){
        val l0 = LevelData(101325.5)
        l0.tempValueKelvin = 273.0
        val l1 = LevelData(85000.0)
        l1.tempValueKelvin = 273.0
        val l2 = LevelData(75000.0)
        l2.tempValueKelvin = 273.0
        val list = mutableListOf<LevelData>()
        list.add(l0)
        list.add(l1)
        list.add(l2)

        val res1 = getNearestLevelData(list, 0.0)

        assertEquals(l0, res1)

        val res2 = getNearestLevelData(list, 1500.0)

        assertEquals(l1, res2)
    }

    @Test
    fun testRatios(){
        val l1 = 600.0
        val l2 = 1400.0

        val res = getLinearRatios(l1, l2, 800.0)
        assertEquals(Pair(0.75, 0.25), res)
    }

    @Test
    fun testMergeDatas(){
        val alt = 1400.0
        val l0 = LevelData(101325.5)
        l0.tempValueKelvin = 273.0
        val l1 = LevelData(85000.0)
        l1.tempValueKelvin = 273.0
        val l2 = LevelData(75000.0)
        l2.tempValueKelvin = 273.0
        l1.vComponentValue = -10.0
        l2.vComponentValue = 10.0
        val list = listOf(l0, l1, l2)

        val ul = findLowerUpperLevel(list, alt)
        println("first h: ${ul?.first?.getLevelHeightInMeters()}, second h: ${ul?.second?.getLevelHeightInMeters()}")
        if(ul?.second != null){
            val r =  getLinearRatios(ul.first.getLevelHeightInMeters(), ul.second.getLevelHeightInMeters(), alt)
            println("first r: ${r?.first}, second r: ${r?.second}")

            val data = r?.let { mergeLevelData(it, ul.first.vComponentValue, ul.second.vComponentValue) }

            if (data != null) {
                assertEquals(true, data < l0.vComponentValue && data > l1.vComponentValue)
            }
        }


    }


    @Test
    fun findLayers(){
        val l1 = LevelData(85000.0)
        l1.tempValueKelvin = 273.0
        val l0 = LevelData(101325.5)
        l0.tempValueKelvin = 273.0
        val l2 = LevelData(75000.0)
        l2.tempValueKelvin = 273.0
        val list = listOf(l0, l1, l2)

        val res = findLowerUpperLevel(list, 1463.0)

        list.forEach { println(it.pressurePascal) }

        assertEquals(Pair(l1, l2), res)
    }

    @Test
    fun testAirDensity(){
        val d = calculateAirDensity(101325.0, 0.0)
        assertEquals(1.25, d, 0.1)

    }

    @Test
    fun testSigmoidRatios(){
        var alt = 2000.0
        val l = 1000.0
        val u = 3000.0

        val r1 = getSigmoidRatios(l, u, alt)
        val e1 = Pair(0.5, 0.5)
        assertEquals(e1, r1)

        alt = 1000.0
        val r2 = getSigmoidRatios(l, u, alt)
        val e2 = Pair(1.0, 0.0)
        assertEquals(e2, r2)

        alt = 3000.0
        val r4 = getSigmoidRatios(l, u, alt)
        val e4 = Pair(0.0, 1.0)
        assertEquals(e4, r4)

        alt = 1800.0
        val r3 = getSigmoidRatios(l, u, alt)
        assertEquals(0.8, r3?.first!!, 0.1)

        alt = 2300.0
        val r5 = getSigmoidRatios(l, u, alt)
        assertEquals(0.9, r5?.second!!, 0.1)

        alt = 3100.0
        val r6 = getSigmoidRatios(l, u, alt)
        assertEquals(null, r6)

        alt = 900.0
        val r7 = getSigmoidRatios(l, u, alt)
        assertEquals(null, r7)
    }


}