package no.uio.ifi.in2000.rakettoppskytning.data.favoriteCards

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteCardDao
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDao
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteCard

class FavoriteCardRepository(
    private val favoriteCardDao: FavoriteCardDao,
    private val favoriteDao: FavoriteDao
    )
{

    private val _allFavoriteCards = MutableStateFlow(listOf<FavoriteCard>())
    private val _allFavoriteLocations = MutableStateFlow(listOf<Favorite>())

    fun observeFavorites(): StateFlow<List<FavoriteCard>> = _allFavoriteCards.asStateFlow()
    fun observeFavoriteLocations(): StateFlow<List<Favorite>> = _allFavoriteLocations.asStateFlow()

    suspend fun insertFavoriteCard(date: String, lat: String, lon: String){
        favoriteCardDao.insertFavoriteCard(FavoriteCard(lat, lon, date))
        getFavoriteCards()
    }

    fun getAllCards(): List<FavoriteCard> {
        return _allFavoriteCards.value
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


    suspend fun insertFavoriteLocation(name: String, lat: String, lon: String){
        favoriteDao.insertFavorite(Favorite(name, lat, lon))
        getFavoriteLocation()
    }

    suspend fun deleteFavoriteLocation(name: String, lat: String, lon: String){
        favoriteDao.deleteFavorite(name, lat, lon)
        getFavoriteLocation()
    }

    suspend fun getFavoriteLocation(){
        try {
            val flow = favoriteDao.getFavorites()
            flow.collect{ sublist ->
                _allFavoriteLocations.update { sublist }
            }
        }catch (e: Exception){
            Log.d("FavoriteLocations", e.stackTraceToString())
        }

    }

}
