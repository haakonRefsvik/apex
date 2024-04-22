package no.uio.ifi.in2000.rakettoppskytning.ui.home.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDao
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.Favorite
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModel(
    private val dao: FavoriteDao
): ViewModel() {

    //private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    private val _favorites =  dao.getFavorites().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(FavoriteState())
    val state = combine(_state, _favorites) { state, favorites ->
        state.copy(
            favorites = favorites
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavoriteState())

    fun onEvent(event: FavoriteEvent) {
        when(event) {
            is FavoriteEvent.DeleteFavorite -> {
                viewModelScope.launch {
                    dao.deleteFavorite(event.favorite)
                }
            }
            FavoriteEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingFavorite = false,
                    name = "",
                    lat = "",
                    lon = ""
                ) }
            }
            FavoriteEvent.SaveFavorite -> {
                val name = state.value.name
                val lat = state.value.lat
                val lon = state.value.lon

                if(name.isBlank() || lat.isBlank() || lon.isBlank()) {
                    return
                }

                val favorite = Favorite(
                    name = name,
                    lat = lat,
                    lon = lon
                )
                viewModelScope.launch {
                    dao.upsertFavorite(favorite)
                }
                _state.update { it.copy(
                    isAddingFavorite = false,
                    name = "",
                    lat = "",
                    lon = ""
                ) }
            }
            is FavoriteEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is FavoriteEvent.SetLat -> {
                _state.update { it.copy(
                    lat = event.lat
                ) }
            }
            is FavoriteEvent.SetLon -> {
                _state.update { it.copy(
                    lon = event.lon
                ) }
            }
            is FavoriteEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingFavorite = true
                ) }
            }
        }
    }
}