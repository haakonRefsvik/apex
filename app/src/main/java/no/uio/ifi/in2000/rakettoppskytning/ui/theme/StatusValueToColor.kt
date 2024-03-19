package no.uio.ifi.in2000.rakettoppskytning.ui.theme

import androidx.compose.ui.graphics.Color


fun getColorFromStatusValue(statusCode: Double): Color{
    if(statusCode == 1.0){
        return Color(194, 16, 16, 255)
    }

    return Color.Transparent
}
