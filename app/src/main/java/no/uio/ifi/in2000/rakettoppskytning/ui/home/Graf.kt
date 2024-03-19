package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisConfig
import co.yml.charts.axis.AxisData
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
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile


@Composable
fun Graf(verticalProfiles: List<VerticalProfile>) {
    val yData: MutableList<Double> = mutableListOf()
    val xLabels: MutableList<String> = mutableListOf()
    yData.add(0.0)
    xLabels.add("")

    verticalProfiles.forEach {
        yData.add(it.getMaxSheerWind().windSpeed)

        xLabels.add(it.time.substring(11, 16))
    }

    val pointsData: MutableList<Point> = mutableListOf()


    val stepSize = 300.dp / (xLabels.size)




    yData.forEachIndexed { index, data ->

        pointsData.add(Point(index.toFloat(), data.toFloat()))
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(stepSize)
        .backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1)

        .shouldDrawAxisLineTillEnd(true)
        .labelData { i ->
            xLabels[i]

        }
        .labelAndAxisLinePadding(5.dp).axisLabelFontSize(10.sp)

        .build()


    val minY = yData.minOrNull() ?: 0.0
    val maxY = yData.maxOrNull() ?: 0.0

    val steps = 5
    val yAxisData = AxisData.Builder()
        .steps(steps)

        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp).axisLabelFontSize(10.sp)
        .labelData { i ->

            val yScale = (maxY - minY) / steps
            "%.2f".format((minY + i * yScale))
        }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(),
                    IntersectionPoint(radius = 4.dp),
                    SelectionHighlightPoint(radius = 4.dp),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,

        )

    LineChart(
        modifier = Modifier.fillMaxSize(),
        lineChartData = lineChartData
    )
}