package no.uio.ifi.in2000.rakettoppskytning.network


/*
@Composable
fun NetworkSnackbar(onDismiss: () -> Unit, showNetworkError: Boolean) {
    val snackbarWidth = 400.dp // Set the desired width of the Snackbar

    if (showNetworkError) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.width(snackbarWidth),
                action = {
                    TextButton(onClick = { onDismiss() }) {
                        Text("DISMISS")
                    }
                }) {
                Text("No internet connection.")
            }
        }
    }
}

















@Composable
fun Network(onDismiss: () -> Unit, showNetworkError: Boolean) {
    if (showNetworkError) {
        AlertDialog(

            modifier = Modifier.height(250.dp),

            icon = { androidx.compose.material3.Icon(imageVector = Icons.Default.Info , contentDescription = "Info Icon", tint = Color.Black)},

            title = {
                Text(text = "Network Connection Error")

            },
            text = {
                Text("Check your internet connection")
            },

            onDismissRequest = { /* Disable dismissing using the dialog's backdrop */ },
            confirmButton = {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text("OK")
                }
            }

        )

    }
}

                var showNetworkError by remember { mutableStateOf(false) }

                    val networkManager = NetworkConnection(this)
                    networkManager.observe(this){
                        if(!it){
                            //Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()


                            val toast = Toast(this)
                            val toastLayout = layoutInflater.inflate(R.layout.toast, null)

                            toast.view = toastLayout
                            toast.duration = Toast.LENGTH_LONG

                            val toastIcon = toastLayout.findViewById<ImageView>(R.id.toast_icon)
                            toastIcon.setImageResource(R.drawable.info_24)

                            val toastText = toastLayout.findViewById<TextView>(R.id.toast_text)
                            toastText.text = "No internet connection"

                            toast.show()

                        }
                    }


                    NetworkSnackbar(
                        onDismiss = { showNetworkError = false },
                        showNetworkError = showNetworkError
                    )

                    /*
                    Network(
                        onDismiss = { showNetworkError.value = false },
                        showNetworkError = showNetworkError.value
                    )



                    networkConnection.observe(this) { connected ->
                        if (!connected ) { // Check if the connection is lost
                            showNetworkError.value = true
                            //Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()
                        }

                    }




 */