package no.uio.ifi.in2000.rakettoppskytning.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue


@Composable
fun WeatherCard(
    value: String,
    iconId: Int,
    desc: String,
    info: String = "",
    statusCode: Double = -1.0
) {
    ElevatedCard(
        modifier = Modifier
            .height(125.dp)
            .width(170.dp)
    ) {
        Row {
            Spacer(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(getColorFromStatusValue(statusCode))
            )
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        modifier = Modifier
                            .width(30.dp),
                        painter = painterResource(iconId),
                        contentDescription = desc
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = desc,
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                    Text(
                        text = value,
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                    Text(
                        color = Color.Black.copy(alpha = 0.7f),
                        text = info,
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    )
                }
            }
        }
    }
}

