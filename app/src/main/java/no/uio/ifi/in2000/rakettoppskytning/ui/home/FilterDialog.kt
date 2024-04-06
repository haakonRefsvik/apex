package no.uio.ifi.in2000.rakettoppskytning.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Switch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue

@Composable
fun FilterDialog(
//    onDismissRequest: () -> Unit,
//    onConfirmation: () -> Unit,
//    sizeOnList: Int

) {
//    var sliderValue by remember { mutableFloatStateOf(sizeOnList.toFloat()) }
//    val uppperRange = sizeOnList.toFloat()
    var checked by remember { mutableStateOf(true) }
    AlertDialog(
        icon = {
            Icon(
                modifier = Modifier
                    .size(40.dp),
                painter = painterResource(R.drawable.filter),
                contentDescription = "Filter"
            )
        },


        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                            },
                            thumbContent = if (checked) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Show green")
                        Spacer(modifier = Modifier.width(13.dp))
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                            },
                            thumbContent = if (checked) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Show red")

                    }


                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Choose a value to filter/sort:", fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        for (i in 0..2) {
                            ElevatedCard(
                                modifier = Modifier
                                    .width(85.dp)
                                    .height(50.dp)
                            ) {
                                Text("s")

                            }
                            Spacer(modifier = Modifier.width(7.5.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Row {
                        for (i in 0..2) {
                            ElevatedCard(
                                modifier = Modifier
                                    .width(85.dp)
                                    .height(50.dp)
                            ) {
                                Text("2")
                                Text("Vindstyrke", fontSize = 10.sp)

                            }
                            Spacer(modifier = Modifier.width(7.5.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))


                }
            }
        },

        onDismissRequest = {
            //onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    //onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    //onDismissRequest()
                }
            ) {
                Text("Reset")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun stfu() {
    FilterDialog()

}