package jez.jetpackpop.features.app.domain

import androidx.compose.runtime.Immutable

@Immutable
data class GameConfiguration(
    val id: GameConfigId,
    val timeLimitSeconds: Float,
    val targetConfigurations: List<TargetConfiguration>,
    val isLastInChapter: Boolean,
    val gameLoopHandler: GameLoop,
    val targetFactory: TargetFactory,
) {

    val interactionEnabled = gameLoopHandler != DemoGameLoop

    companion object {
        val Default =
            GameConfiguration(
                id = GameConfigId.Default,
                timeLimitSeconds = -1f,
                targetConfigurations = emptyList(),
                isLastInChapter = false,
                gameLoopHandler = DemoGameLoop,
                targetFactory = TargetFactory(),
            )
    }
}

data class GameConfigId(
    val chapter: GameChapter,
    val id: Int,
) {
    companion object {
        val Default = GameConfigId(GameChapter.SIMPLE_SINGLE, -1)
    }
}

data class TargetConfiguration(
    val type: TargetType,
    val radius: Float,
    val count: Int,
    val minSpeed: Float,
    val maxSpeed: Float,
    val clickResult: ClickResult?,
) {
    enum class ClickResult {
        SCORE,
        SPLIT,
    }
}

enum class GameChapter(val persistenceName: String) {
    SIMPLE_SINGLE("SIMPLE"),
    SIMPLE_DECOY("MASKED"),
    SPLITTER("SPLITTER"),
    SIMPLE_SINGLE_HARD("SIMPLE_2"),
    SIMPLE_DECOY_HARD("MASKED_2"),
    SPLITTER_HARD("SPLITTER_2"),
    ;

    fun getNextChapter(): GameChapter? {
        val nextOrdinal = ordinal + 1
        return if (nextOrdinal >= entries.size)
            null
        else
            entries[nextOrdinal]
    }

    companion object {
        fun withName(name: String) =
            entries.find { it.persistenceName == name }
                ?: throw IllegalArgumentException("No GamChapter map for $name found")
    }
}

enum class TargetType {
    TARGET,
    DECOY,
    SPLIT_TARGET,
}

fun getFirstGameConfiguration(chapter: GameChapter): GameConfiguration =
    gameConfigurations[chapter]!![0]

fun getGameConfiguration(configId: GameConfigId): GameConfiguration? =
    gameConfigurations.getOrDefault(configId.chapter, emptyList()).getOrNull(configId.id)

fun getNextGameConfiguration(currentConfiguration: GameConfigId?): GameConfiguration? {
    if (currentConfiguration == null) {
        return null
    }

    val chapter = currentConfiguration.chapter
    val chapterItems = gameConfigurations.getOrDefault(chapter, emptyList())
    val nextId = currentConfiguration.id + 1

    val nextConfigId = if (nextId < chapterItems.size) {
        GameConfigId(
            chapter = chapter,
            id = nextId,
        )

    } else {
        chapter.getNextChapter()?.let {
            val nextChapterItems = gameConfigurations.getOrDefault(chapter, emptyList())
            if (nextChapterItems.isEmpty()) {
                null
            } else {
                GameConfigId(
                    chapter = it,
                    id = 0
                )
            }
        }
    }
    return if (nextConfigId == null) {
        null
    } else {
        getGameConfiguration(nextConfigId)
    }
}
