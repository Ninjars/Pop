package jez.jetpackpop.features.app.domain

import androidx.compose.ui.unit.dp
import jez.jetpackpop.features.app.domain.TargetConfiguration.ClickResult.SCORE
import jez.jetpackpop.features.app.domain.TargetConfiguration.ClickResult.SPLIT

private const val CHAPTER_LEVEL_COUNT = 6
private const val LEVEL_DURATION = 10f

private fun getProgressionFraction(count: Int, position: Int): Float {
    val inverseFraction: Float =
        ((count - 1) - position) / (count - 1).toFloat()
    return 1f - inverseFraction
}

val gameConfigurations = hashMapOf(
    GameChapter.SIMPLE_SINGLE to (0 until CHAPTER_LEVEL_COUNT)
        .map { index ->
            val progressFraction = getProgressionFraction(
                CHAPTER_LEVEL_COUNT,
                index
            )
            GameConfiguration(
                id = GameConfigId(GameChapter.SIMPLE_SINGLE, index),
                timeLimitSeconds = LEVEL_DURATION,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = TargetColor.TARGET,
                        radius = (40 - 16 * progressFraction).dp,
                        count = (6 + 18 * progressFraction).toInt(),
                        minSpeed = (20 + 60 * progressFraction).dp,
                        maxSpeed = (30 + 80 * progressFraction).dp,
                        clickResult = SCORE,
                    )
                ),
                isLastInChapter = index == CHAPTER_LEVEL_COUNT - 1
            )
        },
    GameChapter.SIMPLE_DECOY to (0 until CHAPTER_LEVEL_COUNT)
        .map { index ->
            val progressFraction = getProgressionFraction(
                CHAPTER_LEVEL_COUNT,
                index
            )
            GameConfiguration(
                id = GameConfigId(GameChapter.SIMPLE_DECOY, index),
                timeLimitSeconds = LEVEL_DURATION,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = TargetColor.TARGET,
                        radius = (36 - 12 * progressFraction).dp,
                        count = (6 + 12 * progressFraction).toInt(),
                        minSpeed = (30 + 50 * progressFraction).dp,
                        maxSpeed = (40 + 70 * progressFraction).dp,
                        clickResult = SCORE,
                    ),
                    TargetConfiguration(
                        color = TargetColor.DECOY,
                        radius = 80.dp,
                        count = (2 + 3 * progressFraction).toInt(),
                        minSpeed = (20 + 30 * progressFraction).dp,
                        maxSpeed = (25 + 50 * progressFraction).dp,
                        clickResult = null,
                    )
                ),
                isLastInChapter = index == CHAPTER_LEVEL_COUNT - 1
            )
        },
    GameChapter.SPLITTER to (0 until CHAPTER_LEVEL_COUNT)
        .map { index ->
            val progressFraction = getProgressionFraction(
                CHAPTER_LEVEL_COUNT,
                index
            )
            GameConfiguration(
                id = GameConfigId(GameChapter.SPLITTER, index),
                timeLimitSeconds = LEVEL_DURATION,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = TargetColor.TARGET,
                        radius = (42 - 6 * progressFraction).dp,
                        count = (3 + 3 * progressFraction).toInt(),
                        minSpeed = (30 + 50 * progressFraction).dp,
                        maxSpeed = (40 + 70 * progressFraction).dp,
                        clickResult = SPLIT,
                    )
                ),
                isLastInChapter = index == CHAPTER_LEVEL_COUNT - 1
            )
        }
)
