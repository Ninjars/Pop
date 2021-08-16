package jez.jetpackpop.model

import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.*
import kotlin.random.Random

enum class GameProcessState {
    // GameState exists
    INSTANTIATED,

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
    val config: GameConfiguration?,
    val targets: List<TargetData>,
    val remainingTime: Float,
    val score: Int,
) : Parcelable {

    fun onMeasured(width: Float, height: Float): GameState {
        return copy(
            width = width,
            height = height,
            processState = GameProcessState.READY
        )
    }

    fun start(): GameState {
        return when (processState) {
            GameProcessState.WAITING_MEASURE,
            GameProcessState.READY ->
                startGame()
            GameProcessState.PAUSED ->
                return copy(processState = GameProcessState.RUNNING)
            GameProcessState.RUNNING ->
                this
            GameProcessState.INSTANTIATED,
            GameProcessState.END_WIN,
            GameProcessState.END_LOSE ->
                throw IllegalStateException("attempted to start game when in state $processState")
        }
    }

    private fun startGame(): GameState {
        if (config == null) throw IllegalStateException("started game before configured")
        if (width == 0f) {
            return copy(processState = GameProcessState.WAITING_MEASURE)
        }

        val random = Random.Default
        val targets = config.targetConfigurations.flatMap { targetConfig ->
            (0 until targetConfig.count).map {
                TargetData(
                    id = it,
                    color = targetConfig.color,
                    radius = targetConfig.radius,
                    center = Offset(
                        random.nextFloat() * width,
                        random.nextFloat() * height
                    ),
                    velocity = getRandomVelocity(
                        random,
                        targetConfig.minSpeed.value,
                        targetConfig.maxSpeed.value
                    ),
                    clickable = targetConfig.clickable,
                )
            }
        }
        return copy(
            processState = GameProcessState.RUNNING,
            targets = targets,
            remainingTime = config.timeLimitSeconds
        )
    }

    private fun getRandomVelocity(random: Random, min: Float, max: Float): Offset {
        val speed = random.nextFloat() * (max - min) + min
        val angle = random.nextFloat() * 2 * PI
        return Offset(
            x = (cos(angle) * speed).toFloat(),
            y = (sin(angle) * speed).toFloat(),
        )
    }

    fun update(deltaSeconds: Float): GameState {
        return when (processState) {
            GameProcessState.WAITING_MEASURE -> start()
            GameProcessState.RUNNING -> iterateState(deltaSeconds)
            else -> this
        }
    }

    private fun iterateState(deltaSeconds: Float): GameState {
        val nextRemainingTime = if (remainingTime == -1f) -1f else max(0f, remainingTime - deltaSeconds)

        val nextProcessState = when{
            nextRemainingTime == 0f -> GameProcessState.END_LOSE
            targets.isEmpty() -> GameProcessState.END_WIN
            else -> processState
        }
        return copy(
            remainingTime = nextRemainingTime,
            processState = nextProcessState,
            targets = targets.map {
                it.update(deltaSeconds, this)
            }
        )
    }

    fun onTargetTapped(data: TargetData): GameState {
        return copy(
            score = score + 1,
            targets = targets.filter { it.id != data.id || it.color != data.color }.toList()
        )
    }
}

@Parcelize
data class TargetData(
    val id: Int,
    val color: Color,
    val radius: Dp,
    val center: Offset,
    val clickable: Boolean,
    private val velocity: Offset
) : Parcelable {
    @IgnoredOnParcel
    val xOffset: Dp = center.x.dp - radius

    @IgnoredOnParcel
    val yOffset: Dp = center.y.dp - radius

    fun update(deltaTime: Float, state: GameState): TargetData {
        val projectedPosition = center + velocity * deltaTime
        var newVelocity: Offset = velocity
        if (projectedPosition.x - radius.value < 0) {
            newVelocity = Offset(velocity.x.absoluteValue, velocity.y)
        } else if (projectedPosition.x + radius.value >= state.width) {
            newVelocity = Offset(-velocity.x.absoluteValue, velocity.y)
        }
        if (projectedPosition.y - radius.value < 0) {
            newVelocity = Offset(velocity.x, velocity.y.absoluteValue)
        } else if (projectedPosition.y + radius.value >= state.height) {
            newVelocity = Offset(velocity.x, -velocity.y.absoluteValue)
        }

        return copy(
            center = center + newVelocity * deltaTime,
            velocity = newVelocity,
        )
    }
}
