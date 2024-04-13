package no.uio.ifi.in2000.rakettoppskytning.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

    /*
    surfaceContainerLowest = surfaceContainerLowestLight,
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

    /*
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight
    */
)

/*
@Composable
fun RakettoppskytningTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val useDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
        useDynamicColors && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        useDynamicColors && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
 */
@Composable
fun RakettoppskytningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }



    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}