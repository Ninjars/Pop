package jez.jetpackpop.features.app.domain

import androidx.compose.ui.geometry.Offset
import jez.jetpackpop.features.app.model.game.GameEndState
import jez.jetpackpop.features.app.model.game.GameProcessState
import jez.jetpackpop.features.app.model.game.GameState
import jez.jetpackpop.features.app.model.game.TargetData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

interface GameLoop {
    fun iterate(
        gameState: GameState,
        deltaSeconds: Float,
        events: MutableSharedFlow<GameLogicEvent>
    ): GameState
}

object DemoGameLoop : GameLoop {
    override fun iterate(
        gameState: GameState,
        deltaSeconds: Float,
        events: MutableSharedFlow<GameLogicEvent>
    ): GameState =
        with(gameState) {
            copy(
                targets = targets.map {
                    it.update(deltaSeconds, this)
                }
            )
        }
}

object ChapterLevelGameLoop : GameLoop {
    private fun GameState.toEndState(): GameEndState =
        if (config.isLastInChapter) {
            GameEndState.ChapterEndState(
                config.id,
                remainingSeconds,
                scoreData,
                processState == GameProcessState.END_WIN,
            )
        } else {
            GameEndState.LevelEndState(
                config.id,
                remainingSeconds,
                scoreData,
                processState == GameProcessState.END_WIN,
            )
        }

    override fun iterate(
        gameState: GameState,
        deltaSeconds: Float,
        events: MutableSharedFlow<GameLogicEvent>
    ): GameState {
        with(gameState) {
            val nextRemainingTime =
                if (remainingTime == -1f) -1f else max(0f, remainingTime - deltaSeconds)

            val nextProcessState = when {
                nextRemainingTime <= 0f -> GameProcessState.END_LOSE
                targets.none { it.clickResult != null } -> GameProcessState.END_WIN
                else -> processState
            }
            val processStateHasChanged = nextProcessState != processState
            return copy(
                remainingTime = nextRemainingTime,
                processState = nextProcessState,
                targets = targets.map {
                    it.update(deltaSeconds, this)
                }
            ).also {
                if (processStateHasChanged) {
                    when (it.processState) {
                        GameProcessState.END_WIN,
                        GameProcessState.END_LOSE -> {
                            events.tryEmit(
                                GameLogicEvent.GameEnded(it.toEndState())
                            )
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

private fun TargetData.update(deltaTime: Float, state: GameState): TargetData {
    val projectedPosition = center + velocity * deltaTime
    var newVelocity: Offset = velocity
    if (projectedPosition.x - radius < 0) {
        newVelocity = Offset(velocity.x.absoluteValue, velocity.y)
    } else if (projectedPosition.x + radius >= state.width) {
        newVelocity = Offset(-velocity.x.absoluteValue, velocity.y)
    }
    if (projectedPosition.y - radius < 0) {
        newVelocity = Offset(velocity.x, velocity.y.absoluteValue)
    } else if (projectedPosition.y + radius >= state.height) {
        newVelocity = Offset(velocity.x, -velocity.y.absoluteValue)
    }

    val intendedPos = center + newVelocity * deltaTime
    return copy(
        center = Offset(
            x = max(radius, min(state.width - radius, intendedPos.x)),
            y = max(radius, min(state.height - radius, intendedPos.y)),
        ),
        velocity = newVelocity,
    )
}
