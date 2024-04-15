package jez.jetpackpop.features.app.domain

import jez.jetpackpop.features.app.model.game.TargetData
import jez.jetpackpop.features.app.model.game.Vec2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class TargetFactory(
    private val width: Float,
    private val height: Float,
    private val random: Random = Random.Default
) {
    fun createTargets(
        isDemo: Boolean,
        configurations: List<TargetConfiguration>
    ): List<TargetData> {
        return configurations.flatMap { targetConfig ->
            (0 until targetConfig.count).map {
                TargetData(
                    id = it.toString(),
                    color = targetConfig.color,
                    radius = targetConfig.radius,
                    center = Vec2(
                        random.nextFloat() * width,
                        random.nextFloat() * height
                    ),
                    velocity = getRandomVelocity(
                        random,
                        targetConfig.minSpeed,
                        targetConfig.maxSpeed
                    ),
                    clickResult = if (isDemo) null else {
                        when (targetConfig.clickResult) {
                            null -> null
                            TargetConfiguration.ClickResult.SCORE -> TargetData.ClickResult.SCORE
                            TargetConfiguration.ClickResult.SPLIT -> TargetData.ClickResult.SCORE_AND_SPLIT
                        }
                    }
                )
            }
        }
    }

    fun createSplitTargets(data: TargetData, count: Int): List<TargetData> {
        val baseVelocity = data.velocity.getDistance()
        return (0 until count).map {
            data.copy(
                id = "${data.id} split $it",
                velocity = getRandomVelocity(
                    random,
                    baseVelocity * 0.9f,
                    baseVelocity * 1.5f
                ),
                radius = data.radius * 0.75f,
                clickResult = TargetData.ClickResult.SCORE,
                color = TargetColor.SPLIT_TARGET,
            )
        }
    }

    private fun getRandomVelocity(random: Random, min: Float, max: Float): Vec2 {
        val speed = random.nextFloat() * (max - min) + min
        val angle = random.nextFloat() * 2 * PI
        return Vec2(
            x = (cos(angle) * speed).toFloat(),
            y = (sin(angle) * speed).toFloat(),
        )
    }
}
