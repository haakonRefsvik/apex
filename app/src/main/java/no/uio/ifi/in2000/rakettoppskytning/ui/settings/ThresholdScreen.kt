package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.annotation.SuppressLint
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.model.settings.ThresholdValues

@Preview(showBackground = true)
@Composable
fun ThresholdPreview(){
    val navController = rememberNavController()
    ThresholdScreen(navController = navController, ThresholdViewModel(ThresholdRepository()))
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThresholdScreen(
    navController: NavHostController,
    thresholdViewModel: ThresholdViewModel
    ) {
    //nedbør 0
    //vind & shearwind
    //luftfuktighet
    //dewpoint
    //tåke/sikt 0%
    //sette høyde
    val maxPrecipitation by thresholdViewModel.maxPrecipitation
    val maxWind by thresholdViewModel.maxWind
    val maxShearWind by thresholdViewModel.maxShearWind
    val maxHumidity by thresholdViewModel.maxHumidity
    val maxDewPoint by thresholdViewModel.maxDewPoint

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
                            imageVector = Icons.Default.ArrowBack,
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
                .padding(innerPadding)
        ) {
            LazyColumn {
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxPrecipitation,
                        title = "Maks nedbør",
                        desc = "Juster øvre grense for nedbør",
                        drawableId = R.drawable.vann,
                        suffix = "mm"
                    )
                }
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxHumidity,
                        title = "Maks luftfuktighet",
                        desc = "Juster øvre grense for luftfuktighet",
                        drawableId = R.drawable.luftfuktighet,
                        suffix = "%"
                    )
                }
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxWind,
                        title = "Maks vind",
                        desc = "Juster øvre grense for vindhastighet på bakken",
                        drawableId = R.drawable.vind2,
                        suffix = "m/s"
                    )
                }
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxShearWind,
                        title = "Maks vindskjær",
                        desc = "Juster øvre grense for de vertikale vindskjærene",
                        drawableId = R.drawable.vind2,
                        suffix = "m/s"
                    )
                }
                item {
                    ThresholdCard(
                        mutableValue = thresholdViewModel.maxDewPoint,
                        title = "Minimalt duggpunkt",
                        desc = "Juster nedre grense for duggpunkt",
                        drawableId = R.drawable.luftfuktighet,
                        suffix = "℃"
                    )
                }
            }
        }
    }
}

@Composable
fun ThresholdCard(mutableValue: MutableState<Double>, title: String, desc: String, suffix: String, drawableId: Int){
    val controller = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 10.dp)
        ,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(drawableId),
                contentDescription = "id: $drawableId"
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier.width(180.dp)
            ) {
                Text(title, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = desc,
                    lineHeight = 16.sp,
                    fontSize = 12.sp,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                modifier = Modifier
                    .width(60.dp)
                    .height(50.dp),
                textStyle = TextStyle(textAlign = TextAlign.Center),
                value = mutableValue.value.toString(),
                onValueChange = { input ->
                    mutableValue.value = input.toDouble()
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
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.width(30.dp),
                text = suffix,
                fontSize = 14.sp)
        }
    }
    Spacer(modifier = Modifier.height(10.dp))

}

