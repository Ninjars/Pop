package jez.jetpackpop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class GameColorsPalette(
    val overlay: Color = Color.Unspecified,
    val win: Color = Color.Unspecified,
    val lose: Color = Color.Unspecified,
    val miss: Color = Color.Unspecified,
    val target1: Color = Color.Unspecified,
    val target2: Color = Color.Unspecified,
    val target3: Color = Color.Unspecified,
)

val LocalGameColorsPalette = staticCompositionLocalOf { GameColorsPalette() }

val MaterialTheme.gameColors: GameColorsPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalGameColorsPalette.current
