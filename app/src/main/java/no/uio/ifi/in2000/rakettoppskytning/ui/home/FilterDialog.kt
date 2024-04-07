package no.uio.ifi.in2000.rakettoppskytning.ui.homeimport android.util.Logimport androidx.compose.foundation.layout.Arrangementimport androidx.compose.foundation.layout.Columnimport androidx.compose.foundation.layout.Rowimport androidx.compose.foundation.layout.Spacerimport androidx.compose.foundation.layout.fillMaxWidthimport androidx.compose.foundation.layout.heightimport androidx.compose.foundation.layout.paddingimport androidx.compose.foundation.layout.sizeimport androidx.compose.foundation.layout.widthimport androidx.compose.foundation.lazy.LazyColumnimport androidx.compose.foundation.lazy.grid.GridCellsimport androidx.compose.foundation.lazy.grid.LazyVerticalGridimport androidx.compose.foundation.lazy.grid.itemsIndexedimport androidx.compose.material.icons.Iconsimport androidx.compose.material.icons.filled.Checkimport androidx.compose.material3.AlertDialogimport androidx.compose.material3.DropdownMenuItemimport androidx.compose.material3.ElevatedCardimport androidx.compose.material3.ExperimentalMaterial3Apiimport androidx.compose.material3.ExposedDropdownMenuBoximport androidx.compose.material3.ExposedDropdownMenuDefaultsimport androidx.compose.material3.Iconimport androidx.compose.material3.MaterialThemeimport androidx.compose.material3.OutlinedTextFieldimport androidx.compose.material3.RangeSliderimport androidx.compose.material3.Switchimport androidx.compose.material3.SwitchDefaultsimport androidx.compose.material3.Textimport androidx.compose.material3.TextButtonimport androidx.compose.runtime.Composableimport androidx.compose.runtime.getValueimport androidx.compose.runtime.mutableIntStateOfimport androidx.compose.runtime.mutableStateOfimport androidx.compose.runtime.rememberimport androidx.compose.runtime.setValueimport androidx.compose.ui.Alignmentimport androidx.compose.ui.Modifierimport androidx.compose.ui.draw.alphaimport androidx.compose.ui.res.painterResourceimport androidx.compose.ui.tooling.preview.Previewimport androidx.compose.ui.unit.dpimport androidx.compose.ui.unit.spimport no.uio.ifi.in2000.rakettoppskytning.Rimport kotlin.math.roundToIntdata class Filtercard(val icon: Int, val text: String)@OptIn(ExperimentalMaterial3Api::class)@Composablefun FilterDialog(    onDismissRequest: () -> Unit,    onConfirmation: () -> Unit,    onResetRequest: () -> Unit,    homeScreenViewModel: HomeScreenViewModel) {    val isReversed = homeScreenViewModel.isReversed    val checkedGreen = homeScreenViewModel.checkedGreen    val checkedRed = homeScreenViewModel.checkedRed    val sliderPosition = homeScreenViewModel.sliderPosition    val options = homeScreenViewModel.options    var expanded by remember { mutableStateOf(false) }    val text = homeScreenViewModel.text    val markedCardIndex = homeScreenViewModel.markedCardIndex//    var checkedGreen by remember { mutableStateOf(true) }//    var checkedRed by remember { mutableStateOf(true) }////    var sliderPosition by remember { mutableStateOf(0f..360f) }////    val options = listOf("Lowest to highest", "Highest to lowest")//    var isReversed by remember { mutableStateOf((false)) }    val gridItems: List<Filtercard> = listOf(        Filtercard(R.drawable.vind2, "Wind strength"),        Filtercard(R.drawable.vind2, "Wind direction"),        Filtercard(R.drawable.vann, "Rainfall"),        Filtercard(R.drawable.eye, "View distance"),        Filtercard(R.drawable.luftfuktighet, "Air humidity"),        Filtercard(R.drawable.eye, "Dew point"),        )    AlertDialog(        icon = {            Icon(                modifier = Modifier                    .size(40.dp),                painter = painterResource(R.drawable.filter),                contentDescription = "Filter"            )        },        text = {            LazyColumn(                modifier = Modifier.fillMaxWidth(),            ) {                item {                    Row(                        modifier = Modifier.fillMaxWidth(),                        verticalAlignment = Alignment.CenterVertically                    ) {                        Switch(                            checked = checkedGreen.value,                            onCheckedChange = {                                if (checkedRed.value) {                                    checkedGreen.value = it                                } else {                                    checkedRed.value = true                                    checkedGreen.value = it                                }                            },                            thumbContent = if (checkedGreen.value) {                                {                                    Icon(                                        imageVector = Icons.Filled.Check,                                        contentDescription = null,                                        modifier = Modifier.size(SwitchDefaults.IconSize),                                    )                                }                            } else {                                null                            }                        )                        Spacer(modifier = Modifier.width(10.dp))                        Text(text = "Show green")                        Spacer(modifier = Modifier.width(13.dp))                        Switch(                            checked = checkedRed.value,                            onCheckedChange = {                                if (checkedGreen.value) {                                    checkedRed.value = it                                } else {                                    checkedGreen.value = true                                    checkedRed.value = it                                }                            },                            thumbContent = if (checkedRed.value) {                                {                                    Icon(                                        imageVector = Icons.Filled.Check,                                        contentDescription = null,                                        modifier = Modifier.size(SwitchDefaults.IconSize),                                    )                                }                            } else {                                null                            }                        )                        Spacer(modifier = Modifier.width(10.dp))                        Text(text = "Show red")                    }                }                item {                    Spacer(modifier = Modifier.height(10.dp))                    Text(                        "Choose a value to filter/sort:",                        fontSize = 10.sp,                        modifier = Modifier.padding(start = 5.dp)                    )                    Spacer(modifier = Modifier.height(10.dp))                    LazyVerticalGrid(                        modifier = Modifier                            .fillMaxWidth()                            .height(118.dp),                        columns = GridCells.Adaptive(75.dp),                        userScrollEnabled = false                    ) {                        itemsIndexed(gridItems) { index, item ->                            ElevatedCard(                                modifier = Modifier                                    .height(60.dp)                                    .padding(3.dp),                                onClick = {                                    markedCardIndex.intValue =                                        if (markedCardIndex.intValue == index) {                                            -1                                        } else {                                            index                                        }                                },                                ) {                                Row {                                    Icon(                                        modifier = Modifier                                            .size(25.dp)                                            .padding(start = 5.dp, top = 5.dp),                                        painter = painterResource(item.icon),                                        contentDescription = item.text                                    )                                    Row(                                        horizontalArrangement = Arrangement.End,                                        modifier = Modifier.fillMaxWidth()                                    ) {                                        if (index == markedCardIndex.intValue) {                                            Icon(                                                Icons.Default.Check,                                                contentDescription = "Checkmark",                                                modifier = Modifier                                                    .size(18.dp)                                                    .padding(end = 5.dp)                                            )                                        }                                    }                                }                                Text(                                    text = item.text,                                    fontSize = 11.sp,                                    modifier = Modifier.padding(start = 5.dp)                                )                            }                        }                    }                    Spacer(modifier = Modifier.height(10.dp))                    Column(                        modifier = Modifier                            .fillMaxWidth()                            .height(90.dp),                        horizontalAlignment = Alignment.CenterHorizontally                    ) {                        if (markedCardIndex.intValue in 0..5) {                            if (markedCardIndex.intValue == 1) {                                Column {                                    Text(text = "${sliderPosition.value.start.roundToInt()}° to ${sliderPosition.value.endInclusive.roundToInt()}°")                                    RangeSlider(                                        value = sliderPosition.value,                                        steps = 360,                                        onValueChange = { range -> sliderPosition.value = range },                                        valueRange = 0f..360f,                                        onValueChangeFinished = {                                            // launch some business logic update with the state you hold                                            // viewModel.updateSelectedSliderValue(sliderPosition)                                        },                                    )                                    Row {                                        Spacer(modifier = Modifier.width(5.dp))                                        Text("N")                                        Spacer(modifier = Modifier.width(56.dp))                                        Text("E")                                        Spacer(modifier = Modifier.width(56.dp))                                        Text("S")                                        Spacer(modifier = Modifier.width(56.dp))                                        Text("W")                                        Spacer(modifier = Modifier.width(48.dp))                                        Text("N")                                    }                                }                            } else {                                ExposedDropdownMenuBox(                                    expanded = expanded,                                    onExpandedChange = { expanded = it },                                ) {                                    OutlinedTextField(                                        modifier = Modifier                                            .menuAnchor()                                            .width(180.dp),                                        value = text.value,                                        onValueChange = { Log.d("value changed", "stfu") },                                        readOnly = true,                                        singleLine = true,                                        label = { Text("Sort") },                                        trailingIcon = {                                            ExposedDropdownMenuDefaults.TrailingIcon(                                                expanded = expanded                                            )                                        },                                        colors = ExposedDropdownMenuDefaults.textFieldColors(),                                    )                                    ExposedDropdownMenu(                                        expanded = expanded,                                        onDismissRequest = { expanded = false },                                    ) {                                        options.forEach { option ->                                            DropdownMenuItem(                                                text = {                                                    Text(                                                        option,                                                        style = MaterialTheme.typography.bodySmall                                                    )                                                },                                                onClick = {                                                    isReversed.value =                                                        if (option == "Highest to lowest") {                                                            true                                                        } else {                                                            false                                                        }                                                    text.value = option                                                    expanded = false                                                },                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,                                            )                                        }                                    }                                }                            }                        }                    }                }            }        },        onDismissRequest = {            onDismissRequest()        },        confirmButton = {            TextButton(                onClick = {                    onConfirmation()                }            ) {                Text("Confirm")            }        },        dismissButton = {            TextButton(                onClick = {                    onResetRequest()                }            ) {                Text("Reset")            }        }    )}