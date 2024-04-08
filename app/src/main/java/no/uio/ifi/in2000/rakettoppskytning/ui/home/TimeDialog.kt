package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

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


    DatePickerDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {

        },
        dismissButton = {
            OutlinedButton(onClick = { onDismissRequest() }) {
                Text(text = "Dismiss")

            }
        },
        confirmButton = {
            Button(onClick = {

                homeScreenViewModel.startISOtime = sdf.format(dtrpState.selectedStartDateMillis)
                    .replaceRange(11, 15, "${tiState.hour}:${tiState.minute}")
                homeScreenViewModel.endISOtime = sdf.format(dtrpState.selectedEndDateMillis)
                    .replaceRange(11, 15, "${tiState.hour}:${tiState.minute}")
                homeScreenViewModel.filterList()
                onDismissRequest()
            }) {
                Text(text = "Confirm")

            }
        }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DateRangePicker(
                state = dtrpState, modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 390.dp),
                showModeToggle = false

            )
            Spacer(modifier = Modifier.height(20.dp))
            TimeInput(state = tiState)

        }


    }


}