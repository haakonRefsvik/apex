package no.uio.ifi.in2000.rakettoppskytning.ui.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100

/**
 * This Composable function creates a custom snackbar to notify users about possible controlled airspace,
 * featuring a clickable link ("IPPC") to open a web browser for more information.
 * */
@Composable
fun IppcSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    context: Context
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { _ ->
            Snackbar(
                containerColor = main100,
                modifier = Modifier.padding(16.dp),
                content = {
                    Text(
                        text = "You might be inside controlled airspace",
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                action = {
                    ClickableText(
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        text = AnnotatedString(

                            text = "IPPC",
                            spanStyle = SpanStyle(color = Color(38, 104, 245, 255))
                        ),
                        onClick = {
                            openWebBrowser(context = context)
                        }
                    )
                }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
    )
}

/**
 * This function opens web browser for IPPC website
 * */
private fun openWebBrowser(url: String = "https://www.ippc.no/ippc/", context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    context.startActivity(intent)
}