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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue

@Composable
fun FilterDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    sizeOnList: Int

) {
    var sliderValue by remember { mutableFloatStateOf(sizeOnList.toFloat()) }
    val uppperRange = sizeOnList.toFloat()
    AlertDialog(
        icon = {
            Icon(
                modifier = Modifier
                    .size(40.dp),
                painter = painterResource(R.drawable.filter),
                contentDescription = "Filter"
            )
        },

        title = {
            Text("Filter")

        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row {
                        ElevatedCard(
                            modifier = Modifier
                                .height(70.dp)
                                .width(120.dp),
                            onClick = {

                            },
                            enabled = false

                        )

                        {
                            Row {
                                Spacer(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight()
                                        .background(Color(58, 175, 37, 255))

                                )
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.width(65.dp),
                                        horizontalAlignment = Alignment.Start,
                                    ) {
                                        Text(
                                            "Only green cards",
                                            fontSize = 20.sp
                                        )

                                    }

                                }
                            }

                        }
                        Spacer(modifier = Modifier.width(30.dp))
                        ElevatedCard(
                            modifier = Modifier
                                .height(70.dp)
                                .width(120.dp),
                            onClick = {

                            },
                            enabled = true

                        )
                        {
                            Row {
                                Column {
                                    Spacer(
                                        modifier = Modifier
                                            .width(10.dp)
                                            .height(35.dp)
                                            .background(Color(58, 175, 37, 255))
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(10.dp)
                                            .height(35.dp)
                                            .background(Color(216, 64, 64, 255))
                                    )

                                }

                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.width(65.dp),
                                        horizontalAlignment = Alignment.Start,
                                    ) {
                                        Text(
                                            "Red and green",
                                            fontSize = 20.sp
                                        )

                                    }

                                }
                            }

                        }

                    }

                }

                item { Text(text = "Tid") }
                item {

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 1f..uppperRange,
                        steps = sizeOnList


                    )

                    Text("Antall: ${sliderValue.toInt()}")

                }
            }
        },

        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Reset")
            }
        }
    )
}