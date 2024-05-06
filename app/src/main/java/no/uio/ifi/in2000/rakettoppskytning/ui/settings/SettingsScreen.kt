package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.data.navigation.Routes
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.formatNewValue
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecsEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdsEvent
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.StatusColor
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.filter50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings25
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings50
import kotlin.math.abs
import kotlin.math.roundToInt


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    weatherRepository: WeatherRepository,
    onThresholdEvent: (ThresholdsEvent) -> Unit,
    onRocketSpecsEvent: (RocketSpecsEvent) -> Unit,
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val settings1check = settingsViewModel.settingscheck1
    val settings2check = settingsViewModel.settingscheck2

    val scope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.background(settings100),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopAppBar(
                colors = TopAppBarColors(settings100, settings100, settings0, settings0, settings0),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "ArrowBack",
                            tint = settings0
                        )
                    }
                },
                title = {
                    ClickableText(
                        text = AnnotatedString(
                            text = "",
                            spanStyle = SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        ),
                        onClick = { navController.navigateUp() },
                    )
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = main50,
                modifier = Modifier
                    .shadow(
                        10.dp,
                        RectangleShape,
                        false,
                        DefaultShadowColor,
                        DefaultShadowColor
                    )
                    .heightIn(max = 50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = main50),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(modifier = Modifier.sizeIn(maxWidth = 38.dp), onClick = {
                        navController.navigate(Routes.favCards)
                    }) {
                        Icon(
                            Icons.Default.Favorite,
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "Favorite",
                            tint = main0,
                        )
                    }

                    Spacer(modifier = Modifier.widthIn(110.dp))
                    IconButton(onClick = {
                        scope.launch { homeScreenViewModel.scaffold.bottomSheetState.partialExpand() }
                        navController.popBackStack("HomeScreen", false)
                    }) {
                        Icon(
                            Icons.Sharp.LocationOn,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Location",
                            tint = main0
                        )
                    }

                    Spacer(modifier = Modifier.width(110.dp))
                    IconButton(onClick = { navController.navigate(Routes.settings) }) {
                        Icon(
                            Icons.Sharp.Settings,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Settings",
                            tint = main100
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = settings100),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Settings",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 25.sp,
                color = settings0
            )

            Spacer(modifier = Modifier.height(20.dp))

            MultiChoiceSegmentedButtonRow(modifier = Modifier.width(330.dp)) {
                SegmentedButton(
                    colors = SegmentedButtonColors(
                        activeContainerColor = settings25,
                        activeContentColor = settings50,
                        activeBorderColor = settings50,
                        inactiveContainerColor = settings100,
                        inactiveContentColor = settings50,
                        inactiveBorderColor = settings50,
                        disabledActiveContainerColor = settings50,
                        disabledActiveContentColor = settings50,
                        disabledActiveBorderColor = settings50,
                        disabledInactiveContainerColor = settings50,
                        disabledInactiveContentColor = settings50,
                        disabledInactiveBorderColor = settings50,
                    ),
                    checked = settings1check.value,
                    onCheckedChange = {
                        settings1check.value = true
                        settings2check.value = false
                    },
                    shape = RoundedCornerShape(percent = 50),
                ) {
                    Text("Weather values")

                }
                Spacer(modifier = Modifier.width(30.dp))
                SegmentedButton(
                    colors = SegmentedButtonColors(
                        activeContainerColor = settings25,
                        activeContentColor = settings50,
                        activeBorderColor = settings50,
                        inactiveContainerColor = settings100,
                        inactiveContentColor = settings50,
                        inactiveBorderColor = settings50,
                        disabledActiveContainerColor = settings50,
                        disabledActiveContentColor = settings50,
                        disabledActiveBorderColor = settings50,
                        disabledInactiveContainerColor = settings50,
                        disabledInactiveContentColor = settings50,
                        disabledInactiveBorderColor = settings50,
                    ),
                    checked = settings2check.value,
                    onCheckedChange = {
                        settings2check.value = true
                        settings1check.value = false
                    },
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Text("Rocket profile")

                }

            }
            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (settings1check.value) {
                    item {
                        Spacer(modifier = Modifier.width(25.dp))
                        Column(
                            modifier = Modifier.width(340.dp),
                        )
                        {

                            Row(
                                modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(30.dp),
                                    painter = painterResource(R.drawable.trykk),
                                    contentDescription = "tap",
                                    tint = settings0
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Adjust weather values",
                                    fontWeight = FontWeight.W400,
                                    fontSize = 18.sp,
                                    color = settings0
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))

                            HorizontalDivider(
                                modifier = Modifier.width(340.dp),
                                thickness = 1.dp,
                                color = settings0
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }


                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_PRECIPITATION.ordinal],
                            title = "Max precipitation",
                            drawableId = R.drawable.vann,
                            suffix = "mm",
                        )
                    }

                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_WIND.ordinal],
                            title = "Max wind speed",
                            drawableId = R.drawable.vind2,
                            suffix = "m/s",
                        )
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_SHEAR_WIND.ordinal],
                            title = "Max wind shear",
                            drawableId = R.drawable.vind2,
                            suffix = "m/s",
                        )
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_DEW_POINT.ordinal],
                            title = "Max dew point",
                            drawableId = R.drawable.luftfuktighet,
                            suffix = "°C",
                        )

                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_HUMIDITY.ordinal],
                            title = "Max humidity",
                            drawableId = R.drawable.luftfuktighet,
                            suffix = "%",
                        )

                    }
                    item {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Show ippc on map", color = main50)
                            Switch(
                                checked = settingsViewModel.ippcOnMap.value,
                                onCheckedChange = {
                                    settingsViewModel.ippcOnMap.value =
                                        !settingsViewModel.ippcOnMap.value
                                },
                                colors = SwitchDefaults.colors(checkedTrackColor = main0),
                            )

                        }

                    }

                } else {
                    item {
                        Spacer(modifier = Modifier.width(25.dp))
                        Column(
                            modifier = Modifier.width(340.dp),
                        )
                        {
                            Row(
                                modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(30.dp),
                                    painter = painterResource(R.drawable.rakett_pin2),
                                    contentDescription = "tap",
                                    tint = settings0
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Adjust rocket profile",
                                    fontWeight = FontWeight.W400,
                                    fontSize = 18.sp,
                                    color = settings0
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))

                            HorizontalDivider(
                                modifier = Modifier.width(340.dp),
                                thickness = 1.dp, color = settings0
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.APOGEE.ordinal],
                            title = "Apogee",
                            desc = "The rockets highest point",
                            drawableId = R.drawable.rakett_pin2,
                            suffix = "m",
                            numberOfDecimals = 0,
                            numberOfIntegers = 6
                        )
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.LAUNCH_ANGLE.ordinal],
                            title = "Launch angle",
                            drawableId = R.drawable.rakett_pin2,
                            suffix = "Deg",
                            numberOfDecimals = 1,
                            numberOfIntegers = 3,
                            lowestInput = 0.0,
                            highestInput = 90.0
                        )
                    }
                    item {
                        val deg =
                            settingsViewModel.rocketSpecMutableStates[RocketSpecType.LAUNCH_DIRECTION.ordinal].doubleValue
                        val string = findClosestDegree(deg)


                        SettingsCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.LAUNCH_DIRECTION.ordinal],
                            title = "Launch direction",
                            drawableId = R.drawable.rakett_pin2,
                            desc = "Currently $string",
                            suffix = "Deg",
                            numberOfDecimals = 1,
                            numberOfIntegers = 3,
                            lowestInput = 0.0,
                            highestInput = 360.0
                        )
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.THRUST_NEWTONS.ordinal],
                            title = "Thrust",
                            drawableId = R.drawable.rakett_pin2,
                            desc = "Thrust in newtons",
                            suffix = "N",
                            numberOfDecimals = 0,
                            numberOfIntegers = 5,
                            lowestInput = 0.0
                        )
                    }

                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.BURN_TIME.ordinal],
                            title = "Burn time",
                            drawableId = R.drawable.rakett_pin2,
                            desc = "Duration of engine burn",
                            suffix = "Sec",
                            numberOfDecimals = 0,
                            numberOfIntegers = 5,
                            lowestInput = 0.0
                        )
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.DRY_WEIGHT.ordinal],
                            title = "Dry weight",
                            drawableId = R.drawable.rakett_pin2,
                            desc = "",
                            suffix = "Kg",
                            numberOfDecimals = 0,
                            numberOfIntegers = 5,
                            lowestInput = 0.0
                        )
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.WET_WEIGHT.ordinal],
                            title = "Wet weight",
                            drawableId = R.drawable.rakett_pin2,
                            desc = "",
                            suffix = "Kg",
                            numberOfDecimals = 0,
                            numberOfIntegers = 5,
                            lowestInput = 0.0
                        )
                    }
                    item {
                        SliderCard(settingsViewModel)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { // Things to do after closing screen:
            CoroutineScope(Dispatchers.IO).launch {
                settingsViewModel.updateThresholdValues(onThresholdEvent)     // update values in thresholdRepo
                settingsViewModel.updateRocketSpecValues(onRocketSpecsEvent)
                weatherRepository.thresholdValuesUpdated() // update status-colors in the weatherCards
                if (mapViewModel.makeTrajectory.value) {
                    mapViewModel.deleteTrajectory()
                }
            }
        }
    }
}

@Composable
fun SliderCard(settingsViewModel: SettingsViewModel) {
    val sliderPosition = settingsViewModel.sliderPosition

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
                text = "Load every ${findPointSuffix(sliderPosition.floatValue.roundToInt())}point" +
                        "\n${findPerformance(sliderPosition.floatValue.roundToInt())}",
                lineHeight = 16.sp,
                fontSize = 13.sp,
                color = settings50.copy(alpha = 0.7F)
            )


        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.width(150.dp)) {
            Slider(
                value = sliderPosition.floatValue,
                onValueChange = { sliderPosition.floatValue = it },
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun SettingsCard(
    mutableValue: MutableState<Double>,
    title: String,
    desc: String = "",
    suffix: String,
    drawableId: Int,
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
        )

    }

    Spacer(modifier = Modifier.height(27.dp))

}

fun findPointSuffix(res: Int): String {
    val result = when {
        res == 1 -> ""
        res == 2 -> "2nd "
        res == 3 -> "3rd "
        else -> "${res}th "
    }
    return result
}

fun findPerformance(res: Int): String {
    val result = when {
        res <= 2 -> "Low performance"
        res in 3..6 -> "Medium performace"
        res > 6 -> "High performance"
        else -> "unknown"
    }

    return result
}

fun findClosestDegree(degree: Double): String {
    val degreeToString: Map<Double, String> =
        mapOf(
            Pair(0.0, "North"),
            Pair(45.0, "North-East"),
            Pair(90.0, "East"),
            Pair(135.0, "South-East"),
            Pair(180.0, "South"),
            Pair(225.0, "South-West"),
            Pair(270.0, "West"),
            Pair(315.0, "North-West"),
            Pair(360.0, "North"),
        )

    var closestString = ""
    var shortestDistance = Double.MAX_VALUE

    for ((key, value) in degreeToString) {
        val distance = abs(degree - key)
        if (distance < shortestDistance) {
            shortestDistance = distance
            closestString = value
        }
    }

    if (shortestDistance != 0.0) {
        return "$closestString (ish)"
    }

    return closestString
}