package no.uio.ifi.in2000.rakettoppskytning.data.favoriteCards

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteCardDao
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDao
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteCard
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherFavorites

class FavoriteCardRepository(
    private val favoriteCardDao: FavoriteCardDao,
    private val favoriteDao: FavoriteDao
    )
{

    private val _allFavoriteCards = MutableStateFlow(listOf<FavoriteCard>())
    fun observeFavorites(): StateFlow<List<FavoriteCard>> = _allFavoriteCards.asStateFlow()

    suspend fun insertFavoriteCard(date: String, lat: String, lon: String){
        favoriteCardDao.insertFavoriteCard(FavoriteCard(lat, lon, date))
        getFavoriteCards()
    }

    suspend fun deleteFavoriteCard(date: String, lat: String, lon: String){
        favoriteCardDao.deleteFavoriteCard(FavoriteCard(lat, lon, date))
        getFavoriteCards()
    }

    suspend fun getFavoriteCards(){
        try {
            val flow = favoriteCardDao.getFavoriteCards()
            flow.collect{ sublist ->
                _allFavoriteCards.update { sublist }
            }
        }catch (e: Exception){
            Log.d("FavoriteCard", e.stackTraceToString())
        }

    }

    suspend fun removeExpiredCards(date: String){
        favoriteCardDao.removeExpiredCards(date)
    }


    fun getFavoriteByLatLon( lat: String, lon: String): String? =
        favoriteDao.getFavoriteByLatLon(lat, lon)

}
