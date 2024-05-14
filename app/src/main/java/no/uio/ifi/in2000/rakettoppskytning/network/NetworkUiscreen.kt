package no.uio.ifi.in2000.rakettoppskytning.network

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 Show a snackbar if we dont have network connection.
 The snackbar might appear even if we have the internet/network on but it takes time for the device to get connection so the snackbar might pop first and then disappear when have gotten secure network connection.

 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NetworkSnackbar() {

    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomStart).
            padding(bottom = 46.dp),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data
                )
            }
        )

        LaunchedEffect(true) {
            snackbarHostState.showSnackbar(message = "No internet connection")
        }
    }
}



