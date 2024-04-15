package no.uio.ifi.in2000.rakettoppskytning.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Details
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue

@Composable
fun WindCard(details: Details, statusCode: Double = 0.0) {
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



            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            painter = painterResource(R.drawable.vind2),
                            contentDescription = "VindSymbol",
                                tint = details0
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Wind at ground",
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
                        text = "${details.windSpeed} m/s",
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                            color = details0
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Max speed of gust is ${details.windSpeedOfGust} m/s",
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                            color = details0
                    )

                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "N", modifier = Modifier.padding(bottom = 60.dp), color = details0)
                        Text(text = "S", modifier = Modifier.padding(top = 60.dp), color = details0)
                        Text(text = "V", modifier = Modifier.padding(end = 60.dp), color = details0)
                        Text(text = "Ø", modifier = Modifier.padding(start = 60.dp), color = details0)
                        Icon(
                            modifier = Modifier
                                .width(50.dp)
                                .rotate(270.0F + details.windFromDirection.toFloat()),
                            painter = painterResource(R.drawable.kompasspil),
                            contentDescription = "kompasspil",
                                tint = details0
                        )

                        Icon(
                            painter = painterResource(R.drawable.kompass),
                            contentDescription = "Kompass",
                            modifier = Modifier.size(100.dp),
                                tint = details0
                        )
                        Icon(
                            modifier = Modifier
                                .width(50.dp)
                                .rotate(270.0F + details.windFromDirection.toFloat()),
                            painter = painterResource(R.drawable.kompasspil),
                            contentDescription = "kompasspil",
                                tint = details0
                        )
                    }
                }
            }
        }

    }
}
