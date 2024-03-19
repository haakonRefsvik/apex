package no.uio.ifi.in2000.rakettoppskytning.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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


val yAxisData = listOf(0f, 1435f, 3495f, 5478f, 6989f, 8721f)
val xAxisData = listOf(12.3f, 14.1f, 7.9f, 19.9f, 25.2f, 18.4f)
val xLabels = listOf("Tid1", "Tid2", "Tid3", "Tid4", "Tid5", "Tid6")


@Composable
fun Graf(xData: List<Float>, yData: List<Float>) {

    val pointsData: MutableList<Point> = mutableListOf()

    xData.forEachIndexed { index, fl ->
        pointsData.add(Point(yData[index], fl))
    }


    val xLabels = listOf("Tid1", "Tid2", "Tid3", "Tid4", "Tid5")

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1)
        .labelData { i ->
            xLabels.getOrNull(i) ?: ""
        }
        .labelAndAxisLinePadding(15.dp)
        .build()


    val steps = 5
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val yScale = 100 / steps
            (i * yScale).toString()
        }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.White
    )

    LineChart(
        modifier = Modifier
            .fillMaxSize(),

        lineChartData = lineChartData
    )

}
