package jez.jetpackpop.data

import androidx.compose.ui.unit.dp
import jez.jetpackpop.model.*

private const val SIMPLE_SINGLE_LEVEL_COUNT = 10
private const val SIMPLE_DECOY_LEVEL_COUNT = 10

private fun getProgressionFractions(count: Int, position: Int): Pair<Float, Float> {
    val inverseFraction: Float =
        ((count - 1) - position) / (count - 1).toFloat()
    val progressFraction: Float = 1f - inverseFraction
    return Pair(progressFraction, inverseFraction)
}

val gameConfigurations = hashMapOf(
    GameChapter.SIMPLE_SINGLE to (0 until SIMPLE_SINGLE_LEVEL_COUNT)
        .map { index ->
            val (progressFraction, inverseFraction) = getProgressionFractions(SIMPLE_SINGLE_LEVEL_COUNT, index)
            GameConfiguration(
                id = GameConfigId(GameChapter.SIMPLE_SINGLE, index),
                timeLimitSeconds = 15 + 15 * inverseFraction,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = TargetColor.TARGET,
                        radius = (20 + 25 * inverseFraction).dp,
                        count = (5 + 25 * progressFraction).toInt(),
                        minSpeed = (20 + 60 * progressFraction).dp,
                        maxSpeed = (30 + 80 * progressFraction).dp,
                        clickable = true,
                    )
                )
            )
        },
    GameChapter.SIMPLE_DECOY to (0 until SIMPLE_DECOY_LEVEL_COUNT)
        .map { index ->
            val (progressFraction, inverseFraction) = getProgressionFractions(SIMPLE_DECOY_LEVEL_COUNT, index)
            GameConfiguration(
                id = GameConfigId(GameChapter.SIMPLE_DECOY, index),
                timeLimitSeconds = 20 + 15 * inverseFraction,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = TargetColor.TARGET,
                        radius = (20 + 25 * inverseFraction).dp,
                        count = (5 + 25 * progressFraction).toInt(),
                        minSpeed = (20 + 60 * progressFraction).dp,
                        maxSpeed = (30 + 80 * progressFraction).dp,
                        clickable = true,
                    ),
                    TargetConfiguration(
                        color = TargetColor.DECOY,
                        radius = (80 + 25 * progressFraction).dp,
                        count = (2 + 5 * progressFraction).toInt(),
                        minSpeed = (20 + 30 * progressFraction).dp,
                        maxSpeed = (25 + 50 * progressFraction).dp,
                        clickable = false,
                    )
                )
            )
        },
)