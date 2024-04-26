package jez.jetpackpop.features.app.model.game

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jez.jetpackpop.features.app.domain.GameConfiguration
import jez.jetpackpop.features.app.domain.TargetColor

enum class GameProcessState {
    INITIALISED,

    // Game is running
    RUNNING,

    // Game is not updating
    PAUSED,

    END_WIN,

    END_LOSE,
}

@Stable
data class GameState(
    val width: Float,
    val height: Float,
    val processState: GameProcessState,
    val config: GameConfiguration,
    val targets: List<TargetData>,
    val remainingTime: Float,
    val scoreData: GameScoreData,
)

@Stable
data class TargetData(
    val id: String,
    val color: TargetColor,
    val radius: Float,
    val center: Offset,
    val velocity: Offset,
    val clickResult: ClickResult?,
) {
    val xOffset: Dp = center.x.dp - radius.dp

    val yOffset: Dp = center.y.dp - radius.dp

    enum class ClickResult {
        SCORE,
        SCORE_AND_SPLIT,
    }
}
