package no.uio.ifi.in2000.rakettoppskytning.ui.settings

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun SettingsScreen() {
    //nedbør 0
    //vind & shearwind
    //luftfuktighet
    //dewpoint
    //tåke/sikt 0%
    //sette høyde

    Column(modifier = Modifier.fillMaxSize()) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
        ) {
            Row(modifier = Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterVertically) {
                Text("Maxvind: ")
                Spacer(modifier = Modifier.width(100.dp))
                OutlinedTextField(
                    modifier = Modifier.width(125.dp),
                    value = "x",
                    onValueChange = {},
                     )



            }
        }
    }
}



