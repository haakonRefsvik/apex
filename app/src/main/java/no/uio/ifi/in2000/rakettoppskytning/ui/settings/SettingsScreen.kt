package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdsEvent
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings50

/*
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ThresholdPreview() {
    val navController = rememberNavController()
    ThresholdScreen(
        navController = navController,
        SettingsViewModel(ThresholdsDao()),
        WeatherRepository(ThresholdRepository(), GribRepository())
    )
}


 */




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThresholdScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    weatherRepository: WeatherRepository,
    onThresholdEvent: (ThresholdsEvent) -> Unit,
    thresholdState: ThresholdState
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val settings1check = settingsViewModel.settingscheck1
    val settings2check = settingsViewModel.settingscheck2

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
                            imageVector = Icons.Default.KeyboardArrowLeft,
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
                modifier = Modifier.shadow(
                    10.dp,
                    RectangleShape,
                    false,
                    DefaultShadowColor,
                    DefaultShadowColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = main50),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(

                            Icons.Sharp.LocationOn,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Location",
                            tint = main0
                        )
                    }
                    Spacer(modifier = Modifier.width(94.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(R.drawable.rakket),
                            contentDescription = "Rakket",
                            tint = main0
                        )
                    }
                    Spacer(modifier = Modifier.width(95.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Sharp.Settings,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Settings",
                            tint = main0
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
                fontSize = 35.sp,
                color = settings0
            )

            Spacer(modifier = Modifier.height(20.dp))

            MultiChoiceSegmentedButtonRow(modifier = Modifier.width(375.dp)) {
                SegmentedButton(
                    checked = settings1check.value,
                    onCheckedChange = {
                        settings1check.value = true
                        settings2check.value = false
                    },
                    shape = RoundedCornerShape(percent = 50),
                ) {
                    Text("Adjust weather values")

                }
                Spacer(modifier = Modifier.width(15.dp))
                SegmentedButton(
                    checked = settings2check.value,
                    onCheckedChange = {
                        settings2check.value = true
                        settings1check.value = false
                    },
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Text("Adjust rocket profile")

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
                                    contentDescription = "trykk",
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
                        ThresholdCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_PRECIPITATION.ordinal],
                            title = "Max precipitation",
                            drawableId = R.drawable.vann,
                            suffix = "mm",
                        )
                    }
                    item {
                        ThresholdCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_HUMIDITY.ordinal],
                            title = "Max air humidity",
                            drawableId = R.drawable.luftfuktighet,
                            suffix = "%",
                            highestInput = 100.0,
                            numberOfIntegers = 3
                        )
                    }
                    item {
                        ThresholdCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_WIND.ordinal],
                            title = "Max wind speed",
                            drawableId = R.drawable.vind2,
                            suffix = "m/s",
                        )
                    }
                    item {
                        ThresholdCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_SHEAR_WIND.ordinal],
                            title = "Max wind shear",
                            drawableId = R.drawable.vind2,
                            suffix = "m/s",
                        )
                    }
                    item {
                        ThresholdCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_DEW_POINT.ordinal],
                            title = "Max dew point",
                            drawableId = R.drawable.luftfuktighet,
                            suffix = "°C",
                        )

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
                                    contentDescription = "trykk",
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
                        ThresholdCard(
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
                        ThresholdCard(
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
                        ThresholdCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.LAUNCH_DIRECTION.ordinal],
                            title = "Launch direction",
                            drawableId = R.drawable.rakett_pin2,
                            suffix = "Deg",
                            numberOfDecimals = 1,
                            numberOfIntegers = 3,
                            lowestInput = 0.0,
                            highestInput = 360.0
                        )
                    }
                    item {
                        ThresholdCard(
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
                        ThresholdCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.THRUST_NEWTONS.ordinal],
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
                        ThresholdCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.THRUST_NEWTONS.ordinal],
                            title = "Weight",
                            drawableId = R.drawable.rakett_pin2,
                            desc = "",
                            suffix = "Kg",
                            numberOfDecimals = 0,
                            numberOfIntegers = 5,
                            lowestInput = 0.0
                        )
                    }
                    item {
                        ThresholdCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.THRUST_NEWTONS.ordinal],
                            title = "Drop time",
                            drawableId = R.drawable.rakett_pin2,
                            desc = "",
                            suffix = "Sec",
                            numberOfDecimals = 0,
                            numberOfIntegers = 5,
                            lowestInput = 0.0
                        )
                    }
                }


            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { // Things to do after closing screen:
            CoroutineScope(Dispatchers.IO).launch {
                settingsViewModel.updateThresholdValues(onThresholdEvent)     // update values in thresholdRepo
                settingsViewModel.updateRocketSpecValues()
                weatherRepository.thresholdValuesUpdated() // update status-colors in the weatherCards
            }
        }
    }

}

@SuppressLint("SuspiciousIndentation")
@Composable
fun ThresholdCard(
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
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.width(20.dp))

        }

        Column(
            modifier = Modifier.width(210.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = settings50
            )
            if (desc != "") {
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = desc,
                    lineHeight = 16.sp,
                    fontSize = 13.sp,
                    color = settings50
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        OutlinedTextField(
            modifier = Modifier
                .width(80.dp)
                .height(45.dp),
            textStyle = TextStyle(textAlign = TextAlign.Center, color = settings50),
            value = String.format("%.${numberOfDecimals}f", mutableValue.value),
            onValueChange = { input ->
                val newValue = try {
                    formatNewValue(input, numberOfIntegers)
                } catch (e: Exception) {
                    mutableValue.value
                }


                if (newValue in lowestInput..highestInput) {
                    mutableValue.value = newValue

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
        )
        Column(
            modifier = Modifier.width(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = suffix,
                color = settings50
            )

        }
    }


    Spacer(modifier = Modifier.height(27.dp))

}

fun formatNewValue(
    input: String,
    numberOfIntegers: Int,
): Double {
    if (input == "") {
        return 0.0
    }

    val onlyDigitsAndDot = input.filter { it.isDigit() || it == '.' || it == '-' }

    val decimalParts = onlyDigitsAndDot.split(".")
    val integerPart = decimalParts.getOrNull(0) ?: ""

    if (integerPart == "") {
        Log.d("Mais", "GJØR OM TIL 0." + decimalParts[1])

        return ("0." + decimalParts[1]).toDouble()
    }

    if (decimalParts.size > 1 && decimalParts[1] == "") {
        return ("$integerPart.0").toDouble()
    }


    var formattedIntegerValue = integerPart

    while (formattedIntegerValue.length > numberOfIntegers) {
        formattedIntegerValue = formattedIntegerValue.dropLast(1)
    }

    val decimalPart = if (decimalParts.size > 1) {
        "." + decimalParts[1]  // Reconstruct the decimal part, if present
    } else {
        ""
    }

    val r = (formattedIntegerValue + decimalPart)

    return (r).toDouble()
}