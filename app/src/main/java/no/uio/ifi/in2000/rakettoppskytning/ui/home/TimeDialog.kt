package no.uio.ifi.in2000.rakettoppskytning.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Timedialog() {
    val state = rememberTimePickerState(0, 0, is24Hour = true)
    val state2 = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
    Column() {

        DatePickerDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ }) {
            DatePicker(
                modifier = Modifier.width(220.dp),
                state = state2
            )

            val openDialog = remember { mutableStateOf(true) }




        }
        TimeInput(
            state = state,

            )

    }


}