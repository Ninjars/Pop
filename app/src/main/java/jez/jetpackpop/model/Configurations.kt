package jez.jetpackpop.model

import android.os.Parcelable
import androidx.compose.ui.unit.Dp
import jez.jetpackpop.data.gameConfigurations
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameConfiguration(
    val id: GameConfigId,
    val timeLimitSeconds: Float,
    val targetConfigurations: List<TargetConfiguration>,
    val isLastInChapter: Boolean,
    val isDemo: Boolean = false,
) : Parcelable {
    companion object {
        val DEFAULT = GameConfiguration(GameConfigId(GameChapter.SIMPLE_SINGLE, -1), -1f, emptyList(), false)
    }
}


@Parcelize
data class GameConfigId(
    val chapter: GameChapter,
    val id: Int,
) : Parcelable

@Parcelize
data class TargetConfiguration(
    val color: TargetColor,
    val radius: Dp,
    val count: Int,
    val minSpeed: Dp,
    val maxSpeed: Dp,
    val clickable: Boolean,
) : Parcelable

enum class GameChapter(val persistenceName: String) {
    SIMPLE_SINGLE("SIMPLE"),
    SIMPLE_DECOY("MASKED"),
}

enum class TargetColor {
    TARGET,
    DECOY,
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
        getNextChapter(chapter)?.let {
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

private fun getNextChapter(chapter: GameChapter): GameChapter? =
    when (chapter) {
        GameChapter.SIMPLE_SINGLE -> GameChapter.SIMPLE_DECOY
        GameChapter.SIMPLE_DECOY -> null
    }
