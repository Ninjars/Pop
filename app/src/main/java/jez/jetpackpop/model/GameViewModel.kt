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
            processState = GameProcessState.INITIALISED,
            config = GameConfiguration.DEFAULT,
            targets = emptyList(),
            remainingTime = -1f,
            score = 0,
        )
    )
    val gameState: StateFlow<GameState> = _gameState

    fun onMeasured(width: Float, height: Float) {
        val currentState = gameState.value
        _gameState.value = when (currentState.processState) {
            GameProcessState.INITIALISED ->
                currentState.copy(
                    width = width,
                    height = height,
                    processState = GameProcessState.READY,
                )
            GameProcessState.WAITING_MEASURE ->
                currentState.copy(
                    width = width,
                    height = height,
                    processState = GameProcessState.RUNNING,
                )
            else ->
                currentState.copy(
                    width = width,
                    height = height,
                )
        }
    }

    fun start(config: GameConfiguration) {
        val currentState = gameState.value
        if (config == currentState.config) return

        _gameState.value = when (currentState.processState) {
            GameProcessState.INITIALISED ->
                if (currentState.width == 0f) {
                    currentState.copy(
                        config = config,
                        processState = GameProcessState.WAITING_MEASURE
                    )
                } else {
                    currentState.startGame(config, currentState.width, currentState.height)
                }
            GameProcessState.WAITING_MEASURE ->
                currentState.copy(
                    config = config,
                )

            else ->
                currentState.startGame(config, currentState.width, currentState.height)
        }
    }

    fun resume() {
        val currentState = gameState.value
        _gameState.value = when (currentState.processState) {
            GameProcessState.PAUSED ->
                currentState.copy(processState = GameProcessState.RUNNING)
            else ->
                throw IllegalStateException("attempted to pause when in state $currentState.processState")
        }
    }

    fun pause() {
        val currentState = gameState.value
        _gameState.value = when (currentState.processState) {
            GameProcessState.RUNNING ->
                currentState.copy(processState = GameProcessState.PAUSED)
            else ->
                throw IllegalStateException("attempted to pause when in state $currentState.processState")
        }
    }

    fun clear() {
        val currentState = gameState.value
        _gameState.value = GameState(
            width = currentState.width,
            height = currentState.height,
            processState = GameProcessState.INITIALISED,
            config = GameConfiguration.DEFAULT,
            targets = emptyList(),
            remainingTime = -1f,
            score = 0,
        )
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
            GameProcessState.INITIALISED,
            GameProcessState.READY,
            GameProcessState.WAITING_MEASURE -> start(currentState.config)
            GameProcessState.RUNNING -> _gameState.value = currentState.iterateState(deltaSeconds)
            else -> {
            }
        }
    }

    private fun GameState.startGame(
        config: GameConfiguration,
        width: Float,
        height: Float
    ): GameState {
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
            config = config,
            processState = GameProcessState.RUNNING,
            targets = targets,
            remainingTime = config.timeLimitSeconds,
            width = width,
            height = height,
            score = 0,
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
        if (processState != GameProcessState.RUNNING) return this

        val nextRemainingTime =
            if (remainingTime == -1f) -1f else max(0f, remainingTime - deltaSeconds)

        val isDemo = config.isDemo
        val nextProcessState = when {
            nextRemainingTime <= 0f && !isDemo -> GameProcessState.END_LOSE
            targets.none { it.clickable } && !isDemo -> GameProcessState.END_WIN
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
