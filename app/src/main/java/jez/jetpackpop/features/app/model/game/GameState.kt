package jez.jetpackpop.features.app.model.game

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jez.jetpackpop.features.app.domain.GameConfiguration
import jez.jetpackpop.features.app.domain.TargetColor
import jez.jetpackpop.features.highscore.HighScores
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.sqrt

enum class GameProcessState {
    INITIALISED,

    // Game is running
    RUNNING,

    // Game is not updating
    PAUSED,

    END_WIN,

    END_LOSE,
}

@Parcelize
@Stable
data class GameState(
    val width: Float,
    val height: Float,
    val processState: GameProcessState,
    val config: GameConfiguration,
    val targets: List<TargetData>,
    val remainingTime: Float,
    val scoreData: GameScoreData,
    val highScores: HighScores,
) : Parcelable

@Parcelize
@Stable
data class TargetData(
    val id: String,
    val color: TargetColor,
    val radius: Float,
    val center: Vec2,
    val velocity: Vec2,
    val clickResult: ClickResult?,
) : Parcelable {
    @IgnoredOnParcel
    val xOffset: Dp = center.x.dp - radius.dp

    @IgnoredOnParcel
    val yOffset: Dp = center.y.dp - radius.dp

    enum class ClickResult {
        SCORE,
        SCORE_AND_SPLIT,
    }
}

@Parcelize
@Stable
data class Vec2(val x: Float, val y: Float) : Parcelable {
    /**
     * The magnitude of the offset.
     *
     * If you need this value to compare it to another [Offset]'s distance,
     * consider using [getDistanceSquared] instead, since it is cheaper to compute.
     */
    @Stable
    fun getDistance() = sqrt(x * x + y * y)

    /**
     * The square of the magnitude of the offset.
     *
     * This is cheaper than computing the [getDistance] itself.
     */
    @Stable
    fun getDistanceSquared() = x * x + y * y

    /**
     * Unary negation operator.
     *
     * Returns an offset with the coordinates negated.
     *
     * If the [Offset] represents an arrow on a plane, this operator returns the
     * same arrow but pointing in the reverse direction.
     */
    @Stable
    operator fun unaryMinus(): Offset = Offset(-x, -y)

    /**
     * Binary subtraction operator.
     *
     * Returns an offset whose [x] value is the left-hand-side operand's [x]
     * minus the right-hand-side operand's [x] and whose [y] value is the
     * left-hand-side operand's [y] minus the right-hand-side operand's [y].
     */
    @Stable
    operator fun minus(other: Offset): Offset = Offset(x - other.x, y - other.y)

    /**
     * Binary addition operator.
     *
     * Returns an offset whose [x] value is the sum of the [x] values of the
     * two operands, and whose [y] value is the sum of the [y] values of the
     * two operands.
     */
    @Stable
    operator fun plus(other: Offset): Offset = Offset(x + other.x, y + other.y)

    /**
     * Multiplication operator.
     *
     * Returns an offset whose coordinates are the coordinates of the
     * left-hand-side operand (an Offset) multiplied by the scalar
     * right-hand-side operand (a Float).
     */
    @Stable
    operator fun times(operand: Float): Offset = Offset(x * operand, y * operand)
}

