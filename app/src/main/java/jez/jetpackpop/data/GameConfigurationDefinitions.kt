package jez.jetpackpop.data

import androidx.compose.ui.unit.dp
import jez.jetpackpop.model.*

private const val SIMPLE_SINGLE_LEVEL_COUNT = 10
private const val SIMPLE_DECOY_LEVEL_COUNT = 10
val gameConfigurations = hashMapOf(
    GameChapter.SIMPLE_SINGLE to (0 until SIMPLE_SINGLE_LEVEL_COUNT)
        .map {
            val inverseFraction: Float =
                (SIMPLE_SINGLE_LEVEL_COUNT - it) / SIMPLE_SINGLE_LEVEL_COUNT.toFloat()
            val progressFraction: Float = 1f - inverseFraction
            GameConfiguration(
                id = GameConfigId(GameChapter.SIMPLE_SINGLE, it),
                timeLimitSeconds = 15 + 15 * inverseFraction,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = TargetColor.TARGET,
                        radius = (15 + 25 * inverseFraction).dp,
                        count = (5 + 25 * progressFraction).toInt(),
                        minSpeed = (20 + 60 * progressFraction).dp,
                        maxSpeed = (30 + 80 * progressFraction).dp,
                        clickable = true,
                    )
                )
            )
        },
    GameChapter.SIMPLE_DECOY to (0 until SIMPLE_DECOY_LEVEL_COUNT)
        .map {
            val inverseFraction: Float =
                (SIMPLE_DECOY_LEVEL_COUNT - it) / SIMPLE_DECOY_LEVEL_COUNT.toFloat()
            val progressFraction: Float = 1f - inverseFraction
            GameConfiguration(
                id = GameConfigId(GameChapter.SIMPLE_DECOY, it),
                timeLimitSeconds = 20 + 15 * inverseFraction,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = TargetColor.TARGET,
                        radius = (15 + 25 * inverseFraction).dp,
                        count = (5 + 25 * progressFraction).toInt(),
                        minSpeed = (20 + 60 * progressFraction).dp,
                        maxSpeed = (30 + 80 * progressFraction).dp,
                        clickable = true,
                    ),
                    TargetConfiguration(
                        color = TargetColor.DECOY,
                        radius = (25 + 40 * progressFraction).dp,
                        count = (1 + 5 * progressFraction).toInt(),
                        minSpeed = (10 + 30 * progressFraction).dp,
                        maxSpeed = (15 + 40 * progressFraction).dp,
                        clickable = false,
                    )
                )
            )
        },
)