package no.uio.ifi.in2000.rakettoppskytning.ui

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import no.uio.ifi.in2000.rakettoppskytning.model.details.WeatherDetails

abstract class JsonNavType<T> : NavType<T>(isNullableAllowed = false) {
    abstract fun fromJsonParse(value: String): T
    abstract fun T.getJsonParse(): String

    override fun get(bundle: Bundle, key: String): T? =
        bundle.getString(key)?.let { parseValue(it) }

    override fun parseValue(value: String): T = fromJsonParse(value)

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, value.getJsonParse())
    }
}

class DataArgType : JsonNavType<WeatherDetails>() {
    override fun fromJsonParse(value: String): WeatherDetails =
        Gson().fromJson(value, WeatherDetails::class.java)

    override fun WeatherDetails.getJsonParse(): String = Gson().toJson(this)
}