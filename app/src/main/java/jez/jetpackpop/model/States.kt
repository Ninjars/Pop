package jez.jetpackpop.model

import android.os.Parcelable
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class GameProcessState {
    INSTANTIATED, CONFIGURED, READY, RUNNING, PAUSED
}

@Parcelize
data class GameState(
    val width: Float,
    val height: Float,
    val processState: GameProcessState,
    val config: GameConfiguration?,
    val targets: List<TargetData>,
) : Parcelable {

    fun onMeasured(width: Float, height: Float): GameState {
        return copy(
            width = width,
            height = height,
            processState = if (processState == GameProcessState.CONFIGURED)
                GameProcessState.READY
            else
                processState
        )
    }

    fun start(): GameState {
        return when (processState) {
            GameProcessState.INSTANTIATED ->
                throw IllegalStateException("attempted to start before GameState was ready")
            GameProcessState.CONFIGURED ->
                throw IllegalStateException("attempted to start before GameState was configured")
            GameProcessState.READY ->
                startGame()
            GameProcessState.PAUSED ->
                return copy(processState = GameProcessState.RUNNING)
            GameProcessState.RUNNING ->
                this
        }
    }

    private fun startGame(): GameState {
        if (config == null) throw IllegalStateException("started game before configured")

        val random = Random(config.randomSeed)
        val targets = config.targetConfigurations.flatMap { targetConfig ->
            (0..targetConfig.count).map {
                TargetData(
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
                )
            }
        }
        return copy(
            processState = GameProcessState.RUNNING,
            targets = targets,
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
            GameProcessState.RUNNING ->
                copy(
                    targets = targets.map {
                        it.update(deltaSeconds, this)
                    }
                )
            else -> this
        }
    }
}

@Parcelize
data class TargetData(
    val color: Color,
    val radius: Dp,
    val center: Offset,
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
