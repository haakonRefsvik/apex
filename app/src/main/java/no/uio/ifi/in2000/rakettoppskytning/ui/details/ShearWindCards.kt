package no.uio.ifi.in2000.rakettoppskytning.ui.details

import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.VicoZoomState
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import com.patrykandpatrick.vico.core.zoom.Zoom
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import kotlin.math.roundToInt

/**
It's a function that gives default zoom settings for a graph. */
fun getDefaultGraphZoom(): VicoZoomState {
    return VicoZoomState(
        true,
        Zoom.static(2.0F),
        Zoom.static(1.0F),
        Zoom.static(3.0F)
    )
}

/**
 *
 * It's a Composable function that displays information about wind shear using Jetpack Compose elements.
 * */
@Composable
fun ShearWindCard(verticalProfile: VerticalProfile, statusCode: Double = 0.0) {
    ElevatedCard(
        modifier = Modifier
            .height(140.dp)
            .width(360.dp),
            colors = CardColors(
                    containerColor = details50,
                    contentColor = details0,
                    disabledContainerColor = details50,
                    disabledContentColor = details0)
    ) {

        Row {
            Spacer(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(getColorFromStatusValue(statusCode))
            )
            Spacer(modifier = Modifier.width(15.dp))
            Row {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            painter = painterResource(R.drawable.vind2),
                            contentDescription = "WindSymbol"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Peak vertical shear",
                            modifier = Modifier.padding(vertical = 5.dp),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                                color = details0
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                            .width(200.dp)
                    )
                    Text(
                        text = String.format(
                            "%.1f",
                            verticalProfile.getMaxSheerWind().windSpeed
                        ) + " m/s",
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                            color = details0
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "The mean altitude of this shearwind is ${
                            verticalProfile.getMaxSheerWind().altitude.roundToInt()} m",
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                            color = details0.copy(alpha = 0.7F)
                    )

                }
            }
        }

    }
}

/**
 *
 * It's a Composable function that shows a chart of wind shear directions using data from verticalProfile.
 * */
@Composable
fun ShearWindDirCard(verticalProfile: VerticalProfile){
    val shearList = verticalProfile.getAllSheerWinds()
    val yData: List<Float> = shearList.map { it.direction.toFloat() }
    val xData: List<Int> = List(shearList.size) { index ->  index}
    val lineColor: Int = Color.Black.copy(alpha = 0.2f).toArgb()
    val modelProducer = remember { CartesianChartModelProducer.build() }
    val degreeMap = HashMap<Float, String>()
    degreeMap[0F] = "N"
    degreeMap[90F] = "E"
    degreeMap[180F] = "S"
    degreeMap[270F] = "W"
    degreeMap[360F] = "N"

    LaunchedEffect(Unit) { modelProducer.tryRunTransaction { lineSeries {
        series(x = xData, yData)
            }
        }
    }

    ElevatedCard(
        modifier = Modifier
            .height(200.dp)
            .width(360.dp)
    ) {
        Row(
            modifier = Modifier.background(details50)
        ){
            Spacer(
                modifier = Modifier
                    .width(20.dp)
                    .fillMaxHeight()
            )
            Row{
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            painter = painterResource(R.drawable.vind2),
                            contentDescription = "WindSymbol",
                            tint = details0
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Shearwind direction",
                            color = details0,
                            modifier = Modifier.padding(vertical = 0.dp),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .width(320.dp)
                            .height(130.dp)
                    ) {
                        CartesianChartHost(
                            rememberCartesianChart(
                                rememberLineCartesianLayer(
                                    lines = listOf( rememberLineSpec(
                                        backgroundShader = DynamicShaders.verticalGradient(
                                            arrayOf(
                                                details0.copy(alpha = 0.3F),
                                                details0.copy(alpha = 0.1F)
                                            )
                                        ),
                                        shader = DynamicShaders.color(details0.copy(alpha = 0.5F))),
                                    ),
                                    axisValueOverrider = AxisValueOverrider.fixed(
                                        minY = 0F,
                                        maxY = 360F
                                    )

                                ),
                                startAxis = rememberStartAxis(

                                    label =
                                    rememberTextComponent(
                                        textSize = 13.sp,
                                        color = details0,
                                        //textAlignment = Layout.Alignment.ALIGN_CENTER,
                                        padding = dimensionsOf(horizontal = 2.dp, vertical = 0.dp),
                                        margins = dimensionsOf(end = 10.dp),
                                        typeface = android.graphics.Typeface.DEFAULT,
                                    ),
                                    tickLength = 0.dp,
                                    itemPlacer = AxisItemPlacer.Vertical.step({ _ -> 90F }, true),
                                    valueFormatter = { value, _, _ ->
                                        degreeMap[value].toString()
                                    },
                                    guideline = LineComponent(lineColor)
                                ),
                                bottomAxis = rememberBottomAxis(
                                    rememberTextComponent(
                                        textSize = 13.sp,
                                        color = details0,
                                        textAlignment = Layout.Alignment.ALIGN_CENTER,
                                        padding = dimensionsOf(horizontal = 0.dp, vertical = 0.dp),
                                        margins = dimensionsOf(end = 10.dp),
                                        typeface = android.graphics.Typeface.DEFAULT,
                                    ),
                                    tickLength = 0.dp,
                                    title = "altitude in meters",
                                    guideline = null,
                                    valueFormatter = { value, _, _ ->
                                        try {
                                            "${shearList[value.toInt()].altitude.roundToInt()} m"
                                        }catch (e: Exception){
                                            ""
                                        }
                                    },
                                ),
                            ),
                            modelProducer,
                            zoomState = getDefaultGraphZoom()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }

        }
    }
}

/**
 * It's a Composable function displaying a chart of wind shear speeds using data from verticalProfile with Jetpack Compose.
 * */
@Composable
fun ShearWindSpeedCard(verticalProfile: VerticalProfile){
    val shearList = verticalProfile.getAllSheerWinds()
    val yData: List<Float> = shearList.map { it.windSpeed.toFloat() }
    val xData: List<Int> = List(shearList.size) { index ->  index}
    val lineColor: Int = Color.Black.copy(alpha = 0.2f).toArgb()
    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) { modelProducer.tryRunTransaction {
        lineSeries { series(x = xData, y= yData) }
    }
    }

    ElevatedCard(
        modifier = Modifier
            .height(200.dp)
            .width(360.dp)
    ) {
        Row(
            modifier = Modifier.background(details50)
        ){
            Spacer(
                modifier = Modifier
                    .width(20.dp)
                    .fillMaxHeight()
            )
            Row {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                    ,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(0.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            painter = painterResource(R.drawable.vind2),
                            contentDescription = "WindSymbol",
                            tint = details0
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Shearwind speed",
                            modifier = Modifier.padding(vertical = 0.dp),
                            fontSize = 15.sp,
                            color = details0,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .width(320.dp)
                            .height(130.dp)
                        ,

                        ) {
                        CartesianChartHost(
                            rememberCartesianChart(
                                rememberLineCartesianLayer(
                                    lines = listOf( rememberLineSpec(
                                        backgroundShader = DynamicShaders.verticalGradient(
                                            arrayOf(
                                                details0.copy(alpha = 0.3F),
                                                details0.copy(alpha = 0.1F)
                                            )
                                        ),
                                        shader = DynamicShaders.color(details0.copy(alpha = 0.5F)),
                                        ),

                                    ),
                                ),

                                startAxis = rememberStartAxis(
                                    label =
                                    rememberTextComponent(
                                        textSize = 13.sp,
                                        color = details0,
                                        padding = dimensionsOf(horizontal = 0.dp, vertical = 0.dp),
                                        margins = dimensionsOf(end = 10.dp),
                                        typeface = android.graphics.Typeface.DEFAULT,
                                    ),
                                    tickLength = 0.dp,
                                    valueFormatter = { value, _, _ ->
                                        "${value.roundToInt()} m/s"
                                    },
                                    guideline = LineComponent(lineColor)
                                ),
                                bottomAxis = rememberBottomAxis(
                                    rememberTextComponent(
                                        textSize = 13.sp,
                                        color = details0,
                                        textAlignment = Layout.Alignment.ALIGN_CENTER,
                                        padding = dimensionsOf(horizontal = 0.dp, vertical = 0.dp),
                                        margins = dimensionsOf(end = 10.dp),
                                        typeface = android.graphics.Typeface.DEFAULT,
                                    ),
                                    tickLength = 0.dp,
                                    title = "altitude in meters",
                                    guideline = null,
                                    valueFormatter = { value, _, _ ->
                                        "${shearList[value.toInt()].altitude.roundToInt()} m"
                                    },
                                ),
                            ),
                            modelProducer,
                            zoomState = getDefaultGraphZoom()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))

            }
        }
    }
}