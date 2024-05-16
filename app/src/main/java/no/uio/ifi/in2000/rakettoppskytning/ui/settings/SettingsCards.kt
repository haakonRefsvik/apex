package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.data.formatting.formatNewValue
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getTextFieldColors
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings50
import kotlin.math.roundToInt


/**
 * This Composable function, SliderCard, displays a card with a slider used to adjust the trajectory resolution.
 * It includes text displaying the current settings and the slider itself, allowing users to interact with it to change the resolution.
 * */
@Composable
fun SliderCard(settingsViewModel: SettingsViewModel) {
    val sliderPosition =
        settingsViewModel.rocketSpecMutableStates[RocketSpecType.RESOLUTION.ordinal]

    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        Column(
            modifier = Modifier.width(170.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Text(
                    text = "Trajectory resolution",
                    fontSize = 16.sp,
                    color = settings50
                )
                Spacer(modifier = Modifier.width(10.dp))

            }


            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = "Load every ${findPointSuffix(sliderPosition.doubleValue.roundToInt())}point" +
                        "\n${findPerformance(sliderPosition.doubleValue.roundToInt())}",
                lineHeight = 16.sp,
                fontSize = 13.sp,
                color = settings50.copy(alpha = 0.7F)
            )


        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.width(150.dp)) {
            Slider(
                value = sliderPosition.doubleValue.toFloat(),
                onValueChange = {
                    settingsChangesMade = true
                    sliderPosition.doubleValue = it.toDouble() },
                colors = SliderColors(
                    activeTickColor = filter0,
                    activeTrackColor = filter0,
                    inactiveTickColor = filter50,
                    inactiveTrackColor = filter50,
                    disabledInactiveTickColor = filter50,
                    disabledActiveTickColor = filter50,
                    disabledActiveTrackColor = filter50,
                    disabledInactiveTrackColor = filter50,
                    thumbColor = filter0,
                    disabledThumbColor = filter50
                ),
                steps = 8,
                valueRange = 1f..10f
            )
        }
    }

    Spacer(modifier = Modifier.height(27.dp))
}

/**
 *
 * SettingsCard creates a card for adjusting settings with a title, optional description, and an input field for users to input values.
 * It formats the displayed value based on specified decimals and integers.
 * */
@SuppressLint("SuspiciousIndentation")
@Composable
fun SettingsCard(
    mutableValue: MutableState<Double>,
    title: String,
    desc: String = "",
    suffix: String,
    numberOfDecimals: Int = 1,
    numberOfIntegers: Int = 2,
    highestInput: Double = Double.POSITIVE_INFINITY,
    lowestInput: Double = Double.NEGATIVE_INFINITY,
) {
    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        Column(
            modifier = Modifier.width(210.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = settings50
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    fontSize = 13.sp,
                    text = "($suffix)",
                    color = settings50.copy(alpha = 0.7F)
                )
            }


            if (desc != "") {
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = desc,
                    lineHeight = 16.sp,
                    fontSize = 13.sp,
                    color = settings50.copy(alpha = 0.7F)
                )

            }
        }
        Spacer(modifier = Modifier.width(40.dp))
        OutlinedTextField(
            modifier = Modifier
                .width(80.dp)
                .height(45.dp),
            textStyle = TextStyle(textAlign = TextAlign.Center, color = settings50),
            value = String.format("%.${numberOfDecimals}f", mutableValue.value),
            onValueChange = { input ->
                val newValue = try {
                    settingsChangesMade = true
                    val formatNewValue = formatNewValue(
                        input,
                        numberOfIntegers,
                        numberOfDecimals,
                        highestInput = highestInput,
                        lowestInput = lowestInput,
                        oldValue = mutableValue.value.toString()
                    )
                    formatNewValue

                } catch (e: Exception) {
                    mutableValue.value
                }

                mutableValue.value = newValue

            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    controller?.hide()
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            colors = getTextFieldColors()
        )

    }

    Spacer(modifier = Modifier.height(27.dp))

}