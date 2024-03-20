package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

sealed interface FavoriteEvent {
    object SaveFavorite: FavoriteEvent
    data class SetName(val name: String): FavoriteEvent
    data class SetLat(val lat: String): FavoriteEvent
    data class SetLon(val lon: String): FavoriteEvent
    object ShowDialog: FavoriteEvent
    object HideDialog: FavoriteEvent
    data class DeleteFavorite(val favorite: Favorite): FavoriteEvent
}