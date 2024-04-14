package no.uio.ifi.in2000.rakettoppskytning.ui.details

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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.soil.getSoilCategory
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.soil.getSoilDescription

@Composable
fun SoilCard(soilPercentage: Int) {

    val warningIconId = when{
        soilPercentage > 60 -> R.drawable.warning_green
        soilPercentage > 10 -> R.drawable.warning_yellow
        else -> R.drawable.warning_red
    }


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
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            painter = painterResource(R.drawable.vann),
                            contentDescription = "Vannsymbol"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Ground moisture",
                            modifier = Modifier.padding(vertical = 0.dp),
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
                        text = "$soilPercentage %",
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        color = Color.Black.copy(alpha = 0.7f),
                        text = getSoilCategory(soilPercentage),
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                    )

                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.TopCenter,
                    ) {
                        Icon(
                            modifier = Modifier
                                .width(50.dp),
                            painter = painterResource(warningIconId),
                            contentDescription = "faresymbol",
                            tint = Color.Unspecified
                        )
                        Text(text = getSoilDescription(soilPercentage),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .padding(top = 55.dp)
                                .width(100.dp)
                        )
                    }
                }
            }

        }

    }
}