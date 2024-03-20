package no.uio.ifi.in2000.rakettoppskytning.model.savedInDB

data class FavoriteState(
    val favorites: List<Favorite> = emptyList(),
    val name: String = "",
    val lat: String = "",
    val lon: String = "",
    val isAddingFavorite: Boolean = false,
)