package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = OceanTeal,
    onPrimary = OnOceanTeal,
    primaryContainer = SoftMintContainer,
    onPrimaryContainer = OnSoftMintContainer,
    secondary = WarmCoral,
    onSecondary = OnWarmCoral,
    secondaryContainer = SoftPeachContainer,
    onSecondaryContainer = OnSoftPeachContainer,
    tertiary = CocoaCaramel,
    onTertiary = OnCocoaCaramel,
    tertiaryContainer = SoftLatteContainer,
    onTertiaryContainer = OnSoftLatteContainer,
    background = SandCream,
    onBackground = InkCharcoal,
    surface = PureCloudWhite,
    onSurface = InkCharcoal,
    surfaceVariant = WarmSandSurface,
    onSurfaceVariant = MutedSlate,
    outline = SoftBorder,
    error = SoftRoseError,
    errorContainer = SoftRoseContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkTealPrimary,
    onPrimary = InkCharcoal,
    primaryContainer = DarkMintContainer,
    onPrimaryContainer = DarkInk,
    secondary = DarkPeachSecondary,
    onSecondary = InkCharcoal,
    secondaryContainer = DarkPeachContainer,
    onSecondaryContainer = DarkInk,
    tertiary = DarkLatteTertiary,
    onTertiary = InkCharcoal,
    background = DarkOceanBg,
    onBackground = DarkInk,
    surface = DarkOceanSurface,
    onSurface = DarkInk,
    surfaceVariant = Color(0xFF232F2F),
    onSurfaceVariant = DarkMuted,
    outline = Color(0xFF334242)
)

private val PoopAttackShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun PoopAttackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PoopAttackTypography,
        shapes = PoopAttackShapes,
        content = content
    )
}
