package jez.jetpackpop.model

import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

enum class GameProcessState {
    INITIALISED,

    // GameState has a configuration and is ready to start
    READY,

    // GameState has been started but doesn't have dimensions yet; start delayed
    WAITING_MEASURE,

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
) : Parcelable

@Parcelize
data class TargetData(
    val id: Int,
    val color: TargetColor,
    val radius: Dp,
    val center: Offset,
    val clickable: Boolean,
    val velocity: Offset
) : Parcelable {
    @IgnoredOnParcel
    val xOffset: Dp = center.x.dp - radius

    @IgnoredOnParcel
    val yOffset: Dp = center.y.dp - radius
}

@Parcelize
data class GameScoreData(
    val startingScore: Int,
    val tapHistory: List<Boolean>,
    val gameScore: Int,
    val currentMultiplier: Int,
) : Parcelable {
    @IgnoredOnParcel
    val totalScore: Int = startingScore + gameScore
}
