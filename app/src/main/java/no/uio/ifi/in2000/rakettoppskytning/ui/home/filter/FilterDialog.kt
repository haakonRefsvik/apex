package no.uio.ifi.in2000.rakettoppskytning.ui.home.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.filter.FilterCard
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.StatusColor
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getTextFieldColors
import kotlin.math.roundToInt

/**
 *  Creates a filter dialog UI for selecting filter options such as wind strength, wind direction, rainfall, etc., in an app.
 *  It includes interactive elements like switches, sliders, and dropdown menus for user selection.
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onResetRequest: () -> Unit,
    homeScreenViewModel: HomeScreenViewModel

) {

    val isReversed = homeScreenViewModel.isReversed
    val checkedGreen = homeScreenViewModel.checkedGreen
    val checkedRed = homeScreenViewModel.checkedRed
    val sliderPosition = homeScreenViewModel.sliderPosition
    val options = homeScreenViewModel.options
    var expanded by remember { mutableStateOf(false) }
    val text = homeScreenViewModel.text
    val markedCardIndex = homeScreenViewModel.markedCardIndex
    val gridItems: List<FilterCard> = listOf(
        FilterCard(R.drawable.vind2, "Wind strength", FilterCategory.WIND_STRENGTH),
        FilterCard(R.drawable.vind2, "Wind direction", FilterCategory.WIND_DIR),
        FilterCard(R.drawable.vann, "Rainfall", FilterCategory.RAIN),
        FilterCard(R.drawable.eye, "View distance", FilterCategory.VIEW_DIST),
        FilterCard(R.drawable.luftfuktighet, "Air humidity", FilterCategory.AIR_HUMID),
        FilterCard(R.drawable.dewpoint, "Dew point", FilterCategory.DEW_POINT),

        )

    AlertDialog(
        modifier = Modifier
            .width(320.dp),
        containerColor = filter100,
        icon = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Row(modifier = Modifier.padding(end = 90.dp)) {
                    Icon(
                        modifier = Modifier
                            .size(40.dp),
                        painter = painterResource(R.drawable.filter),
                        contentDescription = "Filter",
                        tint = filter0
                    )


                }

                Row {
                    IconButton(modifier = Modifier
                        .size(30.dp), onClick = { onDismissRequest() }) {
                        Icon(
                            Icons.Default.Close,
                            modifier = Modifier
                                .size(30.dp),
                            contentDescription = "Filter",
                            tint = filter0
                        )


                    }

                }

            }
        },


        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Switch(
                            colors = SwitchDefaults.colors(checkedTrackColor = StatusColor.GREEN.color),
                            checked = checkedGreen.value,
                            onCheckedChange = {
                                if (checkedRed.value) {
                                    checkedGreen.value = it

                                } else {
                                    checkedRed.value = true
                                    checkedGreen.value = it
                                }

                            },
                            thumbContent = if (checkedGreen.value) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Show green", color = filter0)
                        Spacer(modifier = Modifier.width(13.dp))

                        Switch(
                            colors = SwitchDefaults.colors(checkedTrackColor = StatusColor.RED.color),
                            checked = checkedRed.value,
                            onCheckedChange = {

                                if (checkedGreen.value) {
                                    checkedRed.value = it
                                } else {
                                    checkedGreen.value = true
                                    checkedRed.value = it
                                }

                            },
                            thumbContent = if (checkedRed.value) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Show red", color = filter0)

                    }


                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Choose a value to filter/sort:",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 5.dp),
                        color = filter0
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        columns = GridCells.Adaptive(75.dp),
                        userScrollEnabled = false

                    ) {
                        itemsIndexed(gridItems) { _, item ->
                            ElevatedCard(
                                modifier = Modifier
                                    .height(60.dp)
                                    .padding(3.dp),
                                colors = CardColors(
                                    containerColor = filter50,
                                    contentColor = filter0,
                                    disabledContainerColor = filter50,
                                    disabledContentColor = filter0
                                ),
                                onClick = {
                                    markedCardIndex.value =
                                        if (markedCardIndex.value == item.type) {
                                            FilterCategory.UNFILTERED
                                        } else {
                                            item.type
                                        }
                                },


                                ) {
                                Row {

                                    Icon(
                                        modifier = Modifier
                                            .size(25.dp)
                                            .padding(start = 5.dp, top = 5.dp),
                                        painter = painterResource(item.icon),
                                        contentDescription = item.text,
                                        tint = filter0
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (item.type == markedCardIndex.value) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Checkmark",
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .padding(end = 5.dp),
                                                tint = filter0
                                            )

                                        }


                                    }

                                }
                                Text(
                                    text = item.text,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 5.dp),
                                    color = filter0
                                )

                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (markedCardIndex.value != FilterCategory.UNFILTERED) {

                            if (markedCardIndex.value == FilterCategory.WIND_DIR) {
                                Column {
                                    Text(
                                        text = "${sliderPosition.value.start.roundToInt()}° to ${sliderPosition.value.endInclusive.roundToInt()}°",
                                        color = filter0

                                    )
                                    RangeSlider(
                                        value = sliderPosition.value,
                                        steps = 360,
                                        onValueChange = { range -> sliderPosition.value = range },
                                        valueRange = 0f..360f,
                                        onValueChangeFinished = {
                                        },
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
                                    )
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("N", color = filter0)
                                        Spacer(modifier = Modifier.width(53.dp))
                                        Text("E", color = filter0)
                                        Spacer(modifier = Modifier.width(55.dp))
                                        Text("S", color = filter0)
                                        Spacer(modifier = Modifier.width(52.dp))
                                        Text("W", color = filter0)
                                        Spacer(modifier = Modifier.width(50.dp))
                                        Text("N")

                                    }
                                }
                            } else {

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = it },
                                ) {
                                    OutlinedTextField(

                                        modifier = Modifier
                                            .menuAnchor()
                                            .width(180.dp),
                                        value = text.value,
                                        onValueChange = { },
                                        readOnly = true,
                                        singleLine = true,
                                        label = { Text("Sort") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },
                                        colors = getTextFieldColors()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                    ) {
                                        options.forEach { option ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        option,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                },
                                                onClick = {
                                                    isReversed.value =
                                                        option == "Highest to lowest"
                                                    text.value = option
                                                    expanded = false
                                                },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                colors = MenuDefaults.itemColors()
                                            )
                                        }
                                    }

                                }

                            }

                        }

                    }


                }
            }
        },

        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm", color = filter0)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onResetRequest()
                }
            ) {
                Text("Reset", color = filter0)
            }
        }
    )
}