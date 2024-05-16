package no.uio.ifi.in2000.rakettoppskytning.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

lateinit var screenSize: Pair<Int, Int>

private val LightColorScheme = lightColorScheme(
    primary = main100,
    onPrimary = main50,
    primaryContainer = main0,

    onPrimaryContainer = firstButton100,
    secondary = firstButton50,
    onSecondary = firstButton0,
    secondaryContainer = secondButton100,
    onSecondaryContainer = secondButton50,
    tertiary = secondButton0,

    onTertiary = filter100,
    tertiaryContainer = filter50,
    onTertiaryContainer = filter0,

    error = details100,
    onError = details50,
    errorContainer = details0,

    onErrorContainer = weatherCard100,
    background = weatherCard50,
    onBackground = weatherCard0,

    surface = favorite100,
    onSurface = favorite0,

    surfaceVariant = favoriteCard100,
    onSurfaceVariant = favoriteCard50,
    outline = favoriteCard0,

    outlineVariant = settings100,
    scrim = settings50,
    inverseSurface = settings0,

    inverseOnSurface = time100,
    inversePrimary = time65,
    surfaceDim = time35,
    surfaceBright = time0,
    surfaceContainerLowest = iconButton50,

    /*
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight
     */
)

private val DarkColorScheme = darkColorScheme(
    primary = mainDark100,
    onPrimary = mainDark50,
    primaryContainer = mainDark0,

    onPrimaryContainer = firstButtonDark100,
    secondary = firstButtonDark50,
    onSecondary = firstButtonDark0,
    secondaryContainer = secondButtonDark100,
    onSecondaryContainer = secondButtonDark50,
    tertiary = secondButtonDark0,

    onTertiary = filterDark100,
    tertiaryContainer = filterDark50,
    onTertiaryContainer = filterDark0,

    error = detailsDark100,
    onError = detailsDark50,
    errorContainer = detailsDark0,

    onErrorContainer = weatherCardDark100,
    background = weatherCardDark50,
    onBackground = weatherCardDark0,

    surface = favoriteDark100,
    onSurface = favoriteDark0,

    surfaceVariant = favoriteCardDark100,
    onSurfaceVariant = favoriteCardDark50,
    outline = favoriteCardDark0,

    outlineVariant = settingsDark100,
    scrim = settingsDark50,
    inverseSurface = settingsDark0,

    inverseOnSurface = timeDark100,
    inversePrimary = timeDark65,
    surfaceDim = timeDark35,
    surfaceBright = timeDark0,

)

/** setting up customized theme */
@Composable
fun RakettoppskytningTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val useDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    when {
        useDynamicColors && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        useDynamicColors && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


// Function to get screen resolution in pixels
fun getScreenResolution(context: Context): Pair<Int, Int> {
    val screenWidthPx = context.resources.displayMetrics.widthPixels
    val screenHeightPx = context.resources.displayMetrics.heightPixels

    return Pair(screenWidthPx, screenHeightPx)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun getDatePickerColors(): DatePickerColors {
    return DatePickerColors(
        time100,
        time0,
        time35,
        time0,
        time0,
        time0,
        time0,
        time100,
        time100,
        time100,
        time100,
        time100,
        time100,
        time0,
        time100,
        time100, // selectedDayContentColo
        time100,
        time35,
        time100,
        time35,
        time35,
        time65,
        time100,
        Color.Transparent,
        TextFieldDefaults.colors(
            unfocusedTextColor = time0,
            unfocusedContainerColor = time100,
            focusedContainerColor = time100,
            focusedIndicatorColor = time35,
            unfocusedLabelColor = time0,
            unfocusedIndicatorColor = time0,
            unfocusedPlaceholderColor = time0,
            focusedTextColor = time0,
            focusedTrailingIconColor = time0,
            focusedLeadingIconColor = time0,
            focusedLabelColor = time35,
            cursorColor = time0,
            selectionColors = TextSelectionColors(time0, time0),
            errorContainerColor = time100,
            errorTextColor = time0
        )

    )
}

// factored out the custom TextFieldColors because its got too many parameters
fun getTextFieldColors(): TextFieldColors {
    return TextFieldColors(
        filter0,
        filter0,
        filter0,
        filter0,
        filter100,
        filter100,
        filter100,
        filter100,
        filter0,
        filter0,
        TextSelectionColors(filter0, filter0),
        filter0,
        iconButton50, // the border
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0,
        filter0
    )
}
