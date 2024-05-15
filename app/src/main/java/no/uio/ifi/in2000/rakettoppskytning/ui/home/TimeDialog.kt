package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.time0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.time100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.time35
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.time65
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun TimeDialog(
    onDismissRequest: () -> Unit,
    homeScreenViewModel: HomeScreenViewModel
) {


    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val dtrpState = homeScreenViewModel.datePickerState.value
    val hourcheck = { x: Int -> if (x in 0..9) "0${x}" else x.toString() }


    DatePickerDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {

        },
        dismissButton = {
            OutlinedButton(onClick = { onDismissRequest() }) {
                Text(text = "Dismiss", color = time0)

            }
        },
        confirmButton = {
            Button(
                colors = ButtonColors(
                    time0,
                    time100,
                    time0,
                    time100
                ),
                onClick = {
                    homeScreenViewModel.resetList()
                    if (homeScreenViewModel.startHour.value == "") {
                        homeScreenViewModel.startHour.value = "0"
                    }
                    if (homeScreenViewModel.endHour.value == "") {
                        homeScreenViewModel.endHour.value = "0"
                    }
                    if (dtrpState.selectedStartDateMillis != null) {
                        homeScreenViewModel.startISOtime =
                            sdf.format(dtrpState.selectedStartDateMillis)
                                .replaceRange(
                                    11,
                                    16,
                                    "${hourcheck(homeScreenViewModel.startHour.value.toInt())}:00"
                                )
                    }

                    if (dtrpState.selectedEndDateMillis == null && dtrpState.selectedStartDateMillis != null) {
                        homeScreenViewModel.endISOtime =
                            sdf.format(dtrpState.selectedStartDateMillis)
                                .replaceRange(
                                    11,
                                    16,
                                    "${hourcheck(homeScreenViewModel.endHour.value.toInt())}:00"
                                )
                    } else if (dtrpState.selectedEndDateMillis == null && dtrpState.selectedStartDateMillis == null) {
                        homeScreenViewModel.endHour = homeScreenViewModel.endHour
                        homeScreenViewModel.startHour = homeScreenViewModel.startHour

                    } else {
                        homeScreenViewModel.endISOtime = sdf.format(dtrpState.selectedEndDateMillis)
                            .replaceRange(
                                11,
                                16,
                                "${hourcheck(homeScreenViewModel.endHour.value.toInt())}:00"
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
            time35,
            time35, // title/headline
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
            time65, // todayContentColor
            time35,
            time100,
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
                time65,
                Color.Transparent,
                Color.Transparent,
                TextSelectionColors(time0, time100),
                time65,
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
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, content = {
            item {
                DateRangePicker(
                    title = { Text(text = "") },
                    headline = { Text(text = "") },
                    showModeToggle = false,
                    state = dtrpState, modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    colors = DatePickerColors(
                        time100,
                        time0,
                        time35,
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
                        time100, // selectedDayContentColo
                        time100,
                        time35,
                        time100,
                        time35,
                        time35,
                        time65,
                        time100,
                        time0,
                        TextFieldDefaults.colors(
                            unfocusedTextColor = time0,
                            unfocusedContainerColor = time100,
                            focusedContainerColor = time100,
                            focusedIndicatorColor = time35,
                            unfocusedLabelColor = time0,
                            unfocusedIndicatorColor = time0,
                            unfocusedPlaceholderColor = time0,
                            focusedTextColor = time0,
                            focusedTrailingIconColor = time0,
                            focusedLeadingIconColor = time0,
                            focusedLabelColor = time35,
                            cursorColor = time0,
                            selectionColors = TextSelectionColors(time0, time0),
                            errorContainerColor = time100,
                            errorTextColor = time0
                        )
                    )
                )
            }
            item {
                Row {
                    InputFiled(homeScreenViewModel, "Start hour")
                    Spacer(modifier = Modifier.width(20.dp))
                    InputFiled(homeScreenViewModel, "End hour")
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

        })


    }


}

@Composable
fun InputFiled(
    homeScreenViewModel: HomeScreenViewModel,
    label: String
) {
    val mutableValue = if (label == "Start hour") {
        homeScreenViewModel.startHour
    } else {
        homeScreenViewModel.endHour
    }

    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = time0,
            focusedBorderColor = time35,
            cursorColor = time0,
            focusedTrailingIconColor = time35,
            unfocusedLabelColor = time0,
            focusedLabelColor = time35,
            selectionColors = TextSelectionColors(time0, time0),

            ),
        modifier = Modifier
            .width(90.dp)
            .height(90.dp),
        textStyle = TextStyle(fontSize = 34.sp, textAlign = TextAlign.Center, color = settings50),
        value = (mutableValue.value),
        onValueChange = { input ->
            if (input == "") {
                mutableValue.value = ""
            } else {
                val newValue = try {
                    input.toInt()
                } catch (e: Exception) {
                    mutableValue.value
                }


                if (newValue in 0..23) {
                    mutableValue.value = newValue.toString()

                }

            }

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
        label = { Text(label) }


    )

}