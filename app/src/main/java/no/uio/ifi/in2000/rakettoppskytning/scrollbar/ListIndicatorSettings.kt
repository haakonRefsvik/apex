package no.uio.ifi.in2000.rakettoppskytning.scrollbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp


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