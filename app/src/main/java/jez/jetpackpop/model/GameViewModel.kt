package jez.jetpackpop.model

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.*
import kotlin.random.Random

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(
        GameState(
            width = 0f,
            height = 0f,
            processState = GameProcessState.INSTANTIATED,
            config = null,
            targets = emptyList(),
            remainingTime = -1f,
            score = 0,
        )
    )
    val gameState: StateFlow<GameState> = _gameState

    fun onMeasured(width: Float, height: Float) {
        _gameState.value = gameState.value.copy(
            width = width,
            height = height,
            processState = if (gameState.value.processState == GameProcessState.WAITING_MEASURE)
                GameProcessState.READY
            else
                gameState.value.processState
        )
    }

    fun start() {
        val currentState = gameState.value
        _gameState.value = when (currentState.processState) {
            GameProcessState.WAITING_MEASURE,
            GameProcessState.READY ->
                currentState.startGame()
            GameProcessState.PAUSED ->
                currentState.copy(processState = GameProcessState.RUNNING)
            GameProcessState.RUNNING ->
                currentState
            GameProcessState.INSTANTIATED,
            GameProcessState.END_WIN,
            GameProcessState.END_LOSE ->
                throw IllegalStateException("attempted to start game when in state $currentState.processState")
        }
    }

    fun onTargetTapped(data: TargetData) {
        val currentState = gameState.value
        _gameState.value = currentState.run {
            copy(
                score = score + 1,
                targets = targets.filter { it.id != data.id || it.color != data.color }.toList()
            )
        }
    }

    fun update(deltaSeconds: Float) {
        val currentState = gameState.value
        when (currentState.processState) {
            GameProcessState.WAITING_MEASURE -> start()
            GameProcessState.RUNNING -> _gameState.value = currentState.iterateState(deltaSeconds)
            else -> {
            }
        }
    }

    private fun GameState.startGame(): GameState {
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

    private fun GameState.iterateState(deltaSeconds: Float): GameState {
        val nextRemainingTime =
            if (remainingTime == -1f) -1f else max(0f, remainingTime - deltaSeconds)

        val nextProcessState = when {
            nextRemainingTime == 0f -> GameProcessState.END_LOSE
            targets.none { it.clickable } -> GameProcessState.END_WIN
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

    private fun TargetData.update(deltaTime: Float, state: GameState): TargetData {
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

        val intendedPos = center + newVelocity * deltaTime
        return copy(
            center = Offset(
                x = max(radius.value, min(state.width - radius.value, intendedPos.x)),
                y = max(radius.value, min(state.height - radius.value, intendedPos.y)),
            ),
            velocity = newVelocity,
        )
    }
}
