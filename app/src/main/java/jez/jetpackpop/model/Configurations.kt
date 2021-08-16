package jez.jetpackpop.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameConfiguration(
    val timeLimitSeconds: Float,
    val targetConfigurations: List<TargetConfiguration>,
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