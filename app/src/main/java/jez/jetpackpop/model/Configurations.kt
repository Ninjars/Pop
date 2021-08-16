package jez.jetpackpop.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jez.jetpackpop.ui.target1
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameConfiguration(
    val id: GameConfigId,
    val timeLimitSeconds: Float,
    val targetConfigurations: List<TargetConfiguration>,
) : Parcelable

@Parcelize
data class GameConfigId(
    val id: Int
) : Parcelable

@Parcelize
data class TargetConfiguration(
    val color: Color,
    val radius: Dp,
    val count: Int,
    val minSpeed: Dp,
    val maxSpeed: Dp,
    val clickable: Boolean,
) : Parcelable

fun getGameConfiguration(configId: GameConfigId): GameConfiguration? =
    when (configId.id) {
        0 ->
            GameConfiguration(
                id = configId,
                timeLimitSeconds = 20f,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = target1,
                        radius = 20.dp,
                        count = 5,
                        minSpeed = 32.dp,
                        maxSpeed = 64.dp,
                        clickable = true,
                    )
                )
            )
        1 ->
            GameConfiguration(
                id = configId,
                timeLimitSeconds = 20f,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = target1,
                        radius = 20.dp,
                        count = 10,
                        minSpeed = 32.dp,
                        maxSpeed = 64.dp,
                        clickable = true,
                    )
                )
            )
        2 ->
            GameConfiguration(
                id = configId,
                timeLimitSeconds = 20f,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = target1,
                        radius = 20.dp,
                        count = 15,
                        minSpeed = 50.dp,
                        maxSpeed = 80.dp,
                        clickable = true,
                    )
                )
            )
        3 ->
            GameConfiguration(
                id = configId,
                timeLimitSeconds = 20f,
                targetConfigurations = listOf(
                    TargetConfiguration(
                        color = target1,
                        radius = 20.dp,
                        count = 20,
                        minSpeed = 60.dp,
                        maxSpeed = 90.dp,
                        clickable = true,
                    )
                )
            )
        else -> null
    }

fun getNextGameConfiguration(currentConfiguration: GameConfigId?): GameConfiguration? {
    return if (currentConfiguration == null) {
        null
    } else {
        val nextConfigId = GameConfigId(currentConfiguration.id + 1)
        getGameConfiguration(nextConfigId)
    }
}
