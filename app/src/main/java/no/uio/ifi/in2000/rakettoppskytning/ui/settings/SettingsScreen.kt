package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.navigation.Routes
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.findClosestDegree
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.RocketSpecType
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType
import no.uio.ifi.in2000.rakettoppskytning.ui.bars.BottomBar
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.map.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings25
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.settings50


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val settings1check = settingsViewModel.weatherValueChosen
    val settings2check = settingsViewModel.rocketProfileChosen
    val context = LocalContext.current

    LaunchedEffect(Unit){
        settingsChangesMade = false
    }

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
            BottomBar(
                navController = navController,
                homeScreenViewModel = homeScreenViewModel,
                currentScreen = Routes.settings
            )
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
                            suffix = "mm",
                        )
                    }

                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_WIND.ordinal],
                            title = "Max wind speed",
                            suffix = "m/s",
                        )
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_SHEAR_WIND.ordinal],
                            title = "Max wind shear",
                            suffix = "m/s",
                        )
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_DEW_POINT.ordinal],
                            title = "Max dew point",
                            suffix = "°C",
                        )

                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.thresholdMutableStates[ThresholdType.MAX_HUMIDITY.ordinal],
                            title = "Max humidity",
                            suffix = "%",
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
                            suffix = "m",
                            numberOfDecimals = 0,
                            numberOfIntegers = 6
                        )
                    }
                    item {
                        SettingsCard(
                            mutableValue = settingsViewModel.rocketSpecMutableStates[RocketSpecType.LAUNCH_ANGLE.ordinal],
                            title = "Launch angle",
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
        onDispose {                                         // Things to do after closing screen:
            if (settingsChangesMade) {
                Toast.makeText(context, "Settings updated!", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    settingsViewModel.updateThresholdValues()   // update values in thresholdRepo
                    settingsViewModel.updateRocketSpecValues()
                    homeScreenViewModel.updateCardColors() // update status-colors in the weatherCards
                    if (mapViewModel.makeTrajectory.value) {
                        mapViewModel.deleteTrajectory()         // delete eventual trajectory
                    }
                }
            }
        }
    }
}

fun findPointSuffix(res: Int): String {
    val result = when (res) {
        1 -> ""
        2 -> "2nd "
        3 -> "3rd "
        else -> "${res}th "
    }
    return result
}

fun findPerformance(res: Int): String {
    val result = when {
        res == 10 -> "Peak performance"
        res <= 2 -> "Low performance"
        res in 3..6 -> "Medium performace"
        res in 7..9 -> "High performance"
        else -> "unknown"
    }

    return result
}