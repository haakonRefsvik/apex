package no.uio.ifi.in2000.rakettoppskytning.ui.details

import android.util.Log
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.Gravity
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.columnSeries
import com.patrykandpatrick.vico.core.model.lineSeries
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.primaryContainerDark
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.primaryContainerLight
import kotlin.math.roundToInt


@Composable
fun ShearWindCard(verticalProfile: VerticalProfile, statusCode: Double = 0.0) {
    ElevatedCard(
        modifier = Modifier
            .height(140.dp)
            .width(360.dp)
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
                            contentDescription = "VindSymbol"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Peak vertical shear",
                            modifier = Modifier.padding(vertical = 5.dp),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
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
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        color = Color.Black.copy(alpha = 0.7f),
                        text = "The mean altitude of this shearwind is ${
                            verticalProfile.getMaxSheerWind().altitude.roundToInt()} m",
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                    )

                }
            }
        }

    }
}
@Composable
fun ShearWindDirCard(verticalProfile: VerticalProfile){

    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) { modelProducer.tryRunTransaction { lineSeries { series(4, 12, 8, 16) } } }




    ElevatedCard(
        modifier = Modifier
            .height(200.dp)
            .width(360.dp)
    ) {
        Row {
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                ),
                modelProducer,
            )
        }
    }
}

@Composable
fun ShearWindSpeedCard(verticalProfile: VerticalProfile){
    val shearWindList = verticalProfile.getAllSheerWinds()
    val altitudes: MutableList<Double> = shearWindList.map { it.altitude }.toMutableList()
    altitudes.add(0, altitudes[0])

    val steps = 4
    var pointsData: MutableList<Point> = mutableListOf()
    pointsData = shearWindList.mapIndexed{ index, shearWind ->
        Point(index.toFloat() + 1, shearWind.windSpeed.toFloat())
    }.toMutableList()
    pointsData.add(0, Point(0F, shearWindList.first().windSpeed.toFloat()))

    pointsData.add(Point(pointsData.size.toFloat() - 1, 0F))
    pointsData.add(Point(0F, 0F))
    pointsData.add(Point(pointsData.size.toFloat() - 2, 0F))

    val maxY = pointsData.maxBy { it.y }.y
    val minY = pointsData.minBy { it.y }.y

    val xAxisData = AxisData.Builder()
        .axisLabelFontSize(14.sp)
        .axisLabelColor(Color.Black.copy(alpha = 0.7f))
        .axisStepSize(80.dp)
        .backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1)
        .labelData { i ->
            var s = ""
            if(i < altitudes.size && i != 0){
                s = "${altitudes[i].roundToInt()} m"
            }
            s
        }
        .labelAndAxisLinePadding(15.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .axisLabelFontSize(14.sp)
        .axisLabelColor(Color.Black.copy(alpha = 0.7f))
        .steps(steps)
        .backgroundColor(Color(247, 242, 249))
        .labelAndAxisLinePadding(20.dp)
        .axisLineThickness(1.5.dp)
        .startPadding(0.dp)
        .labelData { i ->
            val yScale = (maxY - minY) / steps
            ((i * yScale ) + minY) .roundToInt().toString() + " m/s"
        }.build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(width = 5.0F),
                    IntersectionPoint(color = Color.Transparent),
                    SelectionHighlightPoint(
                        color = Color.Transparent,
                    ),
                    ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Black,
                                Color.Transparent
                            )
                        ), alpha = 0.3f
                    ),
                    SelectionHighlightPopUp(
                        popUpLabel = {x, y -> ""}
                    ),

                    )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(enableVerticalLines = false),
        backgroundColor = Color.Transparent,
        paddingRight = 0.dp,
        paddingTop = 25.dp,
        bottomPadding = 20.dp
        )

    ElevatedCard(
        modifier = Modifier
            .height(200.dp)
            .width(360.dp)
    ) {
        Row {
            Spacer(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
            )
            Spacer(modifier = Modifier.width(15.dp))
            Row {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            painter = painterResource(R.drawable.vind2),
                            contentDescription = "VindSymbol"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Shearwind speed",
                            modifier = Modifier.padding(vertical = 0.dp),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LineChart(
                        modifier = Modifier
                            .width(330.dp)
                            .height(200.dp),
                        lineChartData = lineChartData
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}