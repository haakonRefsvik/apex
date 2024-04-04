package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
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
import androidx.navigation.compose.rememberNavController
import com.mapbox.maps.extension.style.style
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdsEvent
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.ui.home.formatNewValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.primaryLight

/*
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ThresholdPreview() {
    val navController = rememberNavController()
    ThresholdScreen(
        navController = navController,
        ThresholdViewModel(ThresholdRepository()),
        WeatherRepository(ThresholdRepository(), GribRepository())
    )
}

 */





//finne ut hvorfor null verdier legges inn i databasen:

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThresholdScreen(
    navController: NavHostController,
    thresholdViewModel: ThresholdViewModel,
    weatherRepository: WeatherRepository,
    onThresholdEvent: (ThresholdsEvent) -> Unit,
    thresholdState: ThresholdState
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Sharp.Menu,
                            contentDescription = "ArrowBack"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "ArrowBack"
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
            BottomAppBar() {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(

                            Icons.Sharp.LocationOn,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Location"
                        )
                    }
                    Spacer(modifier = Modifier.width(94.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(R.drawable.rakket),
                            contentDescription = "Rakket"
                        )
                    }
                    Spacer(modifier = Modifier.width(95.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Sharp.Settings,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Settings"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            LazyColumn(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                item {
                    Spacer(modifier = Modifier.width(25.dp))
                    Column(
                        modifier = Modifier.width(340.dp),
                    )
                    {
                    Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Innstillinger",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 35.sp
                        )

                        Spacer(modifier = Modifier.height(40.dp))
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically

                        ){
                            Icon(
                                modifier = Modifier
                                    .size(30.dp),
                                painter = painterResource(R.drawable.trykk),
                                contentDescription = "trykk"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Tillpass værvarslingen",
                                fontWeight = FontWeight.W400,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(15.dp))

                        HorizontalDivider(
                            modifier = Modifier.width(340.dp),
                            thickness = 1.dp, color = Color.Black.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }

                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxPrecipitation,
                        title = "Maks nedbør",
                        drawableId = R.drawable.vann,
                        suffix = "mm",
                        onThresholdEvent = onThresholdEvent,
                        thresholdState = thresholdState
                    )
                }
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxHumidity,
                        title = "Maks luftfuktighet",
                        drawableId = R.drawable.luftfuktighet,
                        suffix = "%",
                        onThresholdEvent = onThresholdEvent,
                        thresholdState = thresholdState
                    )
                }
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxWind,
                        title = "Maks vind",
                        drawableId = R.drawable.vind2,
                        suffix = "m/s",
                        onThresholdEvent = onThresholdEvent,
                        thresholdState = thresholdState
                    )
                }
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxShearWind,
                        title = "Maks vindskjær",
                        drawableId = R.drawable.vind2,
                        suffix = "m/s",
                        onThresholdEvent = onThresholdEvent,
                        thresholdState = thresholdState
                    )
                }
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxDewPoint,
                        title = "Minimalt duggpunkt",
                        drawableId = R.drawable.luftfuktighet,
                        suffix = "℃",
                        onThresholdEvent = onThresholdEvent,
                        thresholdState = thresholdState
                    )

                }
                item {
                    Spacer(modifier = Modifier.width(25.dp))
                    Column(
                        modifier = Modifier.width(340.dp),
                    )
                    {
                        Spacer(modifier = Modifier.height(30.dp))
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically

                        ){
                            Icon(
                                modifier = Modifier
                                    .size(30.dp),
                                painter = painterResource(R.drawable.rakett_pin2),
                                contentDescription = "trykk"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Tilpass rakettprofil",
                                fontWeight = FontWeight.W400,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(15.dp))

                        HorizontalDivider(
                            modifier = Modifier.width(340.dp),
                            thickness = 1.dp, color = Color.Black.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxDewPoint,
                        title = "Høyeste punkt",
                        desc = "Sett rakettens høyeste punkt",
                        drawableId = R.drawable.rakett_pin2,
                        suffix = "moh",
                        onThresholdEvent = onThresholdEvent,
                        thresholdState = thresholdState,
                        numberOfDecimals = 0
                    )
                }

            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { // Things to do after closing screen:
            CoroutineScope(Dispatchers.IO).launch {
            thresholdViewModel.saveThresholdValues()     // update values in thresholdRepo
            // onThresholdEvent(ThresholdsEvent.SaveThreshold)
            weatherRepository.thresholdValuesUpdated() // update status-colors in the weatherCards
        }
        }
    }

}

@Composable
fun ThresholdCard(
    mutableValue: MutableState<Double>,
    title: String,
    desc: String = "",
    suffix: String,
    drawableId: Int,
    onThresholdEvent: (ThresholdsEvent) -> Unit,
    thresholdState: ThresholdState,
    numberOfDecimals: Int = 1,
    numberOfIntegers: Int = 2
) {
    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val checkedState = remember { mutableStateOf(true) }



        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,

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
                Text(text = title,
                    fontSize = 16.sp,
                    style = TextStyle(color = Color.Black.copy(alpha = 0.7f))
                    )
                if(desc != ""){
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = desc,
                        lineHeight = 16.sp,
                        fontSize = 13.sp,
                        style = TextStyle(color = Color.Black.copy(alpha = 0.5f))
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            OutlinedTextField(
                modifier = Modifier
                    .width(80.dp)
                    .height(45.dp),
                textStyle = TextStyle(textAlign = TextAlign.Center),
                value = String.format("%.${numberOfDecimals}f", mutableValue.value),
                onValueChange = { input ->
                    mutableValue.value = formatNewValue(input)

                    when(title) {
                        "Maks nedbør" -> {onThresholdEvent(ThresholdsEvent.SetNedbor(mutableValue.value.toString()))}
                        "Maks luftfuktighet" -> {onThresholdEvent(ThresholdsEvent.SetLuftfuktighet(mutableValue.value.toString()))}
                        "Maks vind" -> {onThresholdEvent(ThresholdsEvent.SetVind(mutableValue.value.toString()))}
                        "Maks vindskjær" -> {onThresholdEvent(ThresholdsEvent.SetShearWind(mutableValue.value.toString()))}
                        "Minimalt duggpunkt" -> {onThresholdEvent(ThresholdsEvent.SetDuggpunkt(mutableValue.value.toString()))}
                    }
                    mutableValue.value = formatNewValue(input, numberOfIntegers)
                },
                //label = { Text(suffix) },
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
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = suffix,
                    style = TextStyle(color = Color.Black.copy(alpha = 0.5f))
                )

            }
            /*
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it }
            )

             */
        }


    Spacer(modifier = Modifier.height(27.dp))

}

fun formatNewValue(input: String, numberOfIntegers: Int): Double {
    val onlyDigitsAndDot = input.filter { it.isDigit() || it == '.' || it == '-' }

    val decimalParts = onlyDigitsAndDot.split(".")
    val integerPart = decimalParts.getOrNull(0) ?: ""

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