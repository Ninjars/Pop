package jez.jetpackpop.features.app.model.game

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import jez.jetpackpop.features.app.domain.GameConfiguration
import jez.jetpackpop.features.app.domain.TargetType
import kotlin.math.ceil

enum class GameProcessState {
    INITIALISED,

    // Game is running
    RUNNING,

    // Game is not updating
    PAUSED,

    END_WIN,

    END_LOSE,
}

@Immutable
data class GameState(
    val width: Float,
    val height: Float,
    val processState: GameProcessState,
    val config: GameConfiguration,
    val targets: List<TargetData>,
    val effects: List<CircleEffectData>,
    val remainingTime: Float,
    val scoreData: GameScoreData,
    val effectCounter: Int = 0,
    val overtime: Float = 0f,
) {
    val gameIsLooping =
        processState == GameProcessState.RUNNING
                || processState == GameProcessState.END_LOSE
                || processState == GameProcessState.END_WIN
    val gameHasEnded =
        processState == GameProcessState.END_LOSE
                || processState == GameProcessState.END_WIN

    val remainingSeconds = ceil(remainingTime).toInt()
}

@Immutable
data class CircleEffectData(
    val id: Int,
    val type: EffectType,
    val center: Offset,
    val startRadius: Float,
    val endRadius: Float,
    val startAtMs: Long,
    val endAtMs: Long,
) {
    enum class EffectType {
        MISS,
        POP_TARGET,
        POP_SPLIT,
    }
}

@Immutable
data class TargetData(
    val id: String,
    val type: TargetType,
    val radius: Float,
    val center: Offset,
    val velocity: Offset,
    val clickResult: ClickResult?,
) {
    enum class ClickResult {
        SCORE,
        SCORE_AND_SPLIT,
    }
}
