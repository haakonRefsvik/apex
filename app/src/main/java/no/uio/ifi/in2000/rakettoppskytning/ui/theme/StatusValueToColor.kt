package no.uio.ifi.in2000.rakettoppskytning.ui.theme

import androidx.compose.ui.graphics.Color


/** getColorFromStatusValue assigns a color based on the input status code: -1.0 returns transparent, 1.0 returns red, and otherwise green. */
fun getColorFromStatusValue(statusCode: Double): Color {

    if(statusCode == -1.0){
        return Color.Transparent
    }

    if (statusCode == 1.0) {
        return StatusColor.RED.color
    }

    return StatusColor.GREEN.color

}

/**
This enum defines colors for status indicators: green and red. */
enum class StatusColor(val color: Color){
    GREEN(Color(58, 175, 37, 255)),
    RED(Color(216, 64, 64, 255)),
}