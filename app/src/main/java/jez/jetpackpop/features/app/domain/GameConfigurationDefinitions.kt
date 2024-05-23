package jez.jetpackpop.features.app.domain

import androidx.compose.ui.util.lerp
import jez.jetpackpop.features.app.domain.TargetConfiguration.ClickResult.SCORE
import jez.jetpackpop.features.app.domain.TargetConfiguration.ClickResult.SPLIT

private const val CHAPTER_LEVEL_COUNT = 5

private fun getProgressionFraction(count: Int, position: Int): Float {
    val inverseFraction: Float =
        ((count - 1) - position) / (count - 1).toFloat()
    return 1f - inverseFraction
}

val gameConfigurations: Map<GameChapter, List<GameConfiguration>> =
    GameChapter.entries.associateWith { chapter ->
        when (chapter) {
            GameChapter.SIMPLE_SINGLE -> chapter.buildSimpleSingleChapter(hardMode = false)
            GameChapter.SIMPLE_DECOY -> chapter.buildSimpleDecoyChapter(hardMode = false)
            GameChapter.SPLITTER -> chapter.buildSplitterChapter(hardMode = false)
            GameChapter.SIMPLE_SINGLE_HARD -> chapter.buildSimpleSingleChapter(hardMode = true)
            GameChapter.SIMPLE_DECOY_HARD -> chapter.buildSimpleDecoyChapter(hardMode = true)
            GameChapter.SPLITTER_HARD -> chapter.buildSplitterChapter(hardMode = true)
        }
    }

private fun GameChapter.buildSimpleSingleChapter(hardMode: Boolean): List<GameConfiguration> {
    val initialDuration = 10f
    val finalDuration = if (hardMode) 15f else 10f

    return (0 until CHAPTER_LEVEL_COUNT)
        .map { index ->
            val progressFraction = getProgressionFraction(
                CHAPTER_LEVEL_COUNT,
                index
            )
            GameConfiguration(
                id = GameConfigId(this, index),
                timeLimitSeconds = lerp(initialDuration, finalDuration, progressFraction),
                targetConfigurations = listOf(
                    buildTargetConfiguration(
                        type = TargetType.TARGET,
                        clickResult = SCORE,
                        progressFraction = progressFraction,
                        initialRadius = if (hardMode) 40f else 50f,
                        finalRadius = if (hardMode) 30f else 40f,
                        initialCount = if (hardMode) 12 else 6,
                        finalCount = if (hardMode) 28 else 18,
                        initialMinSpeed = if (hardMode) 35f else 30f,
                        finalMinSpeed = if (hardMode) 90f else 80f,
                        initialMaxSpeed = if (hardMode) 50f else 40f,
                        finalMaxSpeed = if (hardMode) 180f else 120f,
                    ),
                ),
                isLastInChapter = index == CHAPTER_LEVEL_COUNT - 1,
                gameLoopHandler = ChapterLevelGameLoop,
                targetFactory = TargetFactory(),
            )
        }
}

private fun GameChapter.buildSimpleDecoyChapter(hardMode: Boolean): List<GameConfiguration> {
    val initialDuration = 10f
    val finalDuration = if (hardMode) 20f else 10f

    return (0 until CHAPTER_LEVEL_COUNT)
        .map { index ->
            val progressFraction = getProgressionFraction(
                CHAPTER_LEVEL_COUNT,
                index
            )
            GameConfiguration(
                id = GameConfigId(this, index),
                timeLimitSeconds = lerp(initialDuration, finalDuration, progressFraction),
                targetConfigurations = listOf(
                    buildTargetConfiguration(
                        type = TargetType.TARGET,
                        clickResult = SCORE,
                        progressFraction = progressFraction,
                        initialRadius = if (hardMode) 40f else 50f,
                        finalRadius = if (hardMode) 30f else 40f,
                        initialCount = if (hardMode) 10 else 6,
                        finalCount = if (hardMode) 20 else 18,
                        initialMinSpeed = if (hardMode) 35f else 30f,
                        finalMinSpeed = if (hardMode) 90f else 80f,
                        initialMaxSpeed = if (hardMode) 50f else 40f,
                        finalMaxSpeed = if (hardMode) 180f else 120f,
                    ),
                    buildTargetConfiguration(
                        type = TargetType.DECOY,
                        clickResult = null,
                        progressFraction = progressFraction,
                        initialRadius = if (hardMode) 50f else 80f,
                        finalRadius = if (hardMode) 100f else 80f,
                        initialCount = if (hardMode) 5 else 2,
                        finalCount = if (hardMode) 6 else 3,
                        initialMinSpeed = if (hardMode) 40f else 20f,
                        finalMinSpeed = if (hardMode) 80f else 50f,
                        initialMaxSpeed = if (hardMode) 50f else 30f,
                        finalMaxSpeed = if (hardMode) 100f else 80f,
                    ),
                ),
                isLastInChapter = index == CHAPTER_LEVEL_COUNT - 1,
                gameLoopHandler = ChapterLevelGameLoop,
                targetFactory = TargetFactory(),
            )
        }
}

private fun GameChapter.buildSplitterChapter(hardMode: Boolean): List<GameConfiguration> {
    val initialDuration = 10f
    val finalDuration = if (hardMode) 18f else 15f

    return (0 until CHAPTER_LEVEL_COUNT)
        .map { index ->
            val progressFraction = getProgressionFraction(
                CHAPTER_LEVEL_COUNT,
                index
            )
            GameConfiguration(
                id = GameConfigId(this, index),
                timeLimitSeconds = lerp(initialDuration, finalDuration, progressFraction),
                targetConfigurations = listOf(

                    buildTargetConfiguration(
                        type = TargetType.TARGET,
                        clickResult = SPLIT,
                        progressFraction = progressFraction,
                        initialRadius = if (hardMode) 45f else 50f,
                        finalRadius = if (hardMode) 36f else 40f,
                        initialCount = if (hardMode) 6 else 3,
                        finalCount = if (hardMode) 12 else 9,
                        initialMinSpeed = if (hardMode) 40f else 30f,
                        finalMinSpeed = if (hardMode) 70f else 60f,
                        initialMaxSpeed = if (hardMode) 80f else 40f,
                        finalMaxSpeed = if (hardMode) 150f else 100f,
                    )
                ),
                isLastInChapter = index == CHAPTER_LEVEL_COUNT - 1,
                gameLoopHandler = ChapterLevelGameLoop,
                targetFactory = TargetFactory(),
            )
        }
}

private fun buildTargetConfiguration(
    type: TargetType,
    clickResult: TargetConfiguration.ClickResult?,
    progressFraction: Float,
    initialRadius: Float,
    finalRadius: Float,
    initialCount: Int,
    finalCount: Int,
    initialMinSpeed: Float,
    finalMinSpeed: Float,
    initialMaxSpeed: Float,
    finalMaxSpeed: Float,
) =
    TargetConfiguration(
        type = type,
        radius = lerp(initialRadius, finalRadius, progressFraction),
        count = lerp(initialCount, finalCount, progressFraction),
        minSpeed = lerp(initialMinSpeed, finalMinSpeed, progressFraction),
        maxSpeed = lerp(initialMaxSpeed, finalMaxSpeed, progressFraction),
        clickResult = clickResult,
    )
