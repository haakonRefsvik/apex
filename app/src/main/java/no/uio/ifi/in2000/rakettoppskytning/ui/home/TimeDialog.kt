package no.uio.ifi.in2000.rakettoppskytning.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.time0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.time100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.time35
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.time65
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun TimeDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    homeScreenViewModel: HomeScreenViewModel
) {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val dtrpState = homeScreenViewModel.dtrpState.value
    val tiState = homeScreenViewModel.tiState.value
    val hourcheck = { x: Int -> if (x in 0..9) "0${x}" else x.toString() }

    DatePickerDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = {
        },
        dismissButton = {
            OutlinedButton(
                border = BorderStroke(2.dp, time0),
                onClick = { onDismissRequest() }) {
                Text(text = "Dismiss", color = time0)

            }
        },
        confirmButton = {
            Button(
                colors = ButtonColors(
                    time0,
                    time100,
                    time0,
                    time100),
                onClick = {
                homeScreenViewModel.startISOtime = sdf.format(dtrpState.selectedStartDateMillis)
                    .replaceRange(11, 16, "${hourcheck(tiState.hour)}:${hourcheck(tiState.minute)}")
                if (dtrpState.selectedEndDateMillis == null) {
                    homeScreenViewModel.endISOtime = homeScreenViewModel.startISOtime
                } else {
                    homeScreenViewModel.endISOtime = sdf.format(dtrpState.selectedEndDateMillis)
                        .replaceRange(
                            11,
                            16,
                            "${hourcheck(tiState.hour)}:${hourcheck(tiState.minute)}"
                        )
                }

                homeScreenViewModel.filterList()
                onDismissRequest()
            }) {
                Text(text = "Confirm", color = time100)

            }
        },
        colors = DatePickerColors(
            time100,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            TextFieldColors(
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                TextSelectionColors(time0, time100),
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent
            )
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DateRangePicker(
                state = dtrpState, modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 390.dp),
                showModeToggle = false,
                colors = DatePickerColors(
                    time100,
                    time0,
                    time0,
                    time0,
                    time0,
                    time0,
                    time0,
                    time100,
                    time100,
                    time100,
                    time100,
                    time100,
                    time100,
                    time0,
                    time100,
                    time100,
                    time100,
                    time0,
                    time100,
                    time35,
                    time35,
                    time65,
                    time0,
                    time0,
                    TextFieldColors(
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        TextSelectionColors(time100, time100),
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        time100,
                        )
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            TimeInput(
                state = tiState,
                colors = TimePickerColors(
                    time0,
                    time0,
                    time100,
                    time0,
                    Color.Magenta,
                    Color.Green,
                    time0,
                    time0,
                    time0,
                    time0,
                    time100,
                    time100,
                    time0,
                    time0
                    )
            )
        }
    }
}