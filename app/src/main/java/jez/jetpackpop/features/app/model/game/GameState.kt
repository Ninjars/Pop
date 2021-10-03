package jez.jetpackpop.features.app.model.game

import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jez.jetpackpop.features.app.domain.GameConfiguration
import jez.jetpackpop.features.app.domain.TargetColor
import jez.jetpackpop.features.highscore.HighScores
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

enum class GameProcessState {
    INITIALISED,

    // Game is running
    RUNNING,

    // Game is not updating
    PAUSED,

    END_WIN,

    END_LOSE,
}

@Parcelize
data class GameState(
    val width: Float,
    val height: Float,
    val processState: GameProcessState,
    val config: GameConfiguration,
    val targets: List<TargetData>,
    val remainingTime: Float,
    val scoreData: GameScoreData,
    val highScores: HighScores,
) : Parcelable

@Parcelize
data class TargetData(
    val id: String,
    val color: TargetColor,
    val radius: Dp,
    val center: Offset,
    val velocity: Offset,
    val clickResult: ClickResult?,
) : Parcelable {
    @IgnoredOnParcel
    val xOffset: Dp = center.x.dp - radius

    @IgnoredOnParcel
    val yOffset: Dp = center.y.dp - radius

    enum class ClickResult {
        SCORE,
        SCORE_AND_SPLIT,
    }
}
