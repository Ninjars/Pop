package jez.jetpackpop.features.game.model

import androidx.compose.ui.geometry.Offset
import jez.jetpackpop.features.game.data.TargetConfiguration
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class TargetFactory(
    private val width: Float,
    private val height: Float,
    private val random: Random = Random.Default
) {
    fun createTargets(isDemo: Boolean, configurations: List<TargetConfiguration>): List<TargetData> {
        return configurations.flatMap { targetConfig ->
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
                    clickResult = if (isDemo) null else {
                        when(targetConfig.clickResult) {
                            TargetConfiguration.ClickResult.SCORE -> TargetData.ClickResult.SCORE
                            null -> null
                        }
                    }
                )
            }
        }
    }

    private fun getRandomVelocity(random: Random, min: Float, max: Float): Offset {
        val speed = random.nextFloat() * (max - min) + min
        val angle = random.nextFloat() * 2 * PI
        return Offset(
            x = (cos(angle) * speed).toFloat(),
            y = (sin(angle) * speed).toFloat(),
        )
    }
}
