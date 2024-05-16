package no.uio.ifi.in2000.rakettoppskytning.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue

/**
 *WeatherCard that displays weather information.
 * It includes a value, an icon, a description, additional information, and a status code.
 * */
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
            .width(170.dp),
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
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        modifier = Modifier
                            .width(30.dp),
                        painter = painterResource(iconId),
                        contentDescription = desc,
                            tint = details0
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = desc,
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                            color = details0
                    )
                }
                Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                    Text(
                        text = value,
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                            color = details0
                    )
                }
                Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                    Text(
                        text = info,
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                            color = details0.copy(alpha = 0.7F)
                    )
                }
            }
        }
    }
}

