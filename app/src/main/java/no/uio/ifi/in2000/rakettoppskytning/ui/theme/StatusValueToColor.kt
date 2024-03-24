package no.uio.ifi.in2000.rakettoppskytning.ui.theme

import androidx.compose.ui.graphics.Color


fun getColorFromStatusValue(statusCode: Double): Color {
    if (statusCode == 1.0) {
        return StatusColor.RED.color
    }

    return StatusColor.GREEN.color

}

enum class StatusColor(val color: Color){
    GREEN(Color(58, 175, 37, 255)),
    RED(Color(216, 64, 64, 255)),
    YELLOW(Color(233, 189, 33, 255))
}