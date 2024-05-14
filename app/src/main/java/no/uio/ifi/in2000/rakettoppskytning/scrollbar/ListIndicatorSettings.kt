package no.uio.ifi.in2000.rakettoppskytning.scrollbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/*
This scrollbar was made by - Kerry Bisset: here is the link; https://blog.stackademic.com/jetpack-compose-multiplatform-scrollbar-scrolling-7c231a002ee1
 */

/**
 * Customizing the list indicator appearance and behavior.
 */
sealed class ListIndicatorSettings {

    data object Disabled : ListIndicatorSettings()


    data class EnabledMirrored(
        val indicatorHeight: Dp,
        val indicatorColor: Color,
        val graphicIndicator: @Composable (modifier: Modifier, alpha: Float) -> Unit = { _, _ -> }
    ) : ListIndicatorSettings()


    data class EnabledIndividualControl(
        val upperIndicatorHeight: Dp,
        val upperIndicatorColor: Color,
        val upperGraphicIndicator: @Composable (modifier: Modifier, alpha: Float) -> Unit = { _, _ -> },
        val lowerIndicatorHeight: Dp,
        val lowerIndicatorColor: Color,
        val lowerGraphicIndicator: @Composable (modifier: Modifier, alpha: Float) -> Unit = { _, _ -> }
    ) : ListIndicatorSettings()
}