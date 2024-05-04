package jez.jetpackpop.features.app.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import jez.jetpackpop.R
import jez.jetpackpop.audio.GameSoundEffect
import jez.jetpackpop.audio.SoundManager
import jez.jetpackpop.ui.PopMegaButton
import jez.jetpackpop.ui.ScreenScaffold
import jez.jetpackpop.ui.lose
import jez.jetpackpop.ui.win
import kotlinx.coroutines.delay
import kotlin.math.max

data class ScoreInfo(
    val remainingSeconds: Int,
    val levelScore: Int,
    val totalScore: Int,
    val levelScoreRecord: Int?,
    val levelTimeRecord: Int?,
    val chapterScoreRecord: Int?,
)

data class LevelInfo(
    val chapterName: String,
    val totalLevels: Int,
    val currentLevel: Int,
)

@Composable
fun GameWinMenu(
    soundManager: SoundManager,
    scoreInfo: ScoreInfo,
    levelInfo: LevelInfo,
    onClick: () -> Unit,
    @StringRes titleText: Int = R.string.game_end_win_title,
    @StringRes ctaText: Int = R.string.game_end_win_cta,
) {
    LaunchedEffect(Unit) {
        soundManager.playSound(GameSoundEffect.GAME_WIN)
    }
    ScreenScaffold(
        topSlot = { TopReadout(levelInfo) },
        middleSlot = {
            DisplayMenu(
                color = MaterialTheme.colors.win,
                titleText = titleText,
                buttonText = ctaText,
                modifier = it,
                onClick = onClick,
            )
        },
        bottomSlot = { ScoreReadout(scoreInfo) }
    )
}

@Composable
fun GameLoseMenu(
    soundManager: SoundManager,
    scoreInfo: ScoreInfo,
    levelInfo: LevelInfo,
    onClick: () -> Unit,
) {
    LaunchedEffect(Unit) {
        soundManager.playSound(GameSoundEffect.GAME_LOSE)
    }
    ScreenScaffold(
        topSlot = { TopReadout(levelInfo) },
        middleSlot = {
            DisplayMenu(
                color = MaterialTheme.colors.lose,
                titleText = R.string.game_end_lose_title,
                buttonText = R.string.game_end_lose_cta,
                modifier = it,
                onClick = onClick,
            )
        },
        bottomSlot =
        if (scoreInfo.levelScoreRecord != null || scoreInfo.levelTimeRecord != null) {
            {
                HighScoreReadout(
                    scoreInfo.levelScoreRecord,
                    scoreInfo.levelTimeRecord,
                )
            }
        } else {
            null
        }
    )
}

@Composable
private fun TopReadout(levelInfo: LevelInfo) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.game_end_heading_chapter),
        )
        Text(
            text = levelInfo.chapterName,
            style = MaterialTheme.typography.h4,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.game_end_heading_level),
            )
            Text(
                text = "${levelInfo.currentLevel + 1}/${levelInfo.totalLevels}"
            )
        }
    }
}

@Composable
private fun HighScoreReadout(
    levelScoreRecord: Int?,
    levelTimeRecord: Int?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        levelScoreRecord?.let {
            ScoreRow(
                stringResource(R.string.game_end_heading_level_score_record),
                it,
            )
        }
        levelTimeRecord?.let {
            ScoreRow(
                stringResource(R.string.game_end_heading_remaining_time_record),
                it,
            )
        }
    }
}

@Composable
private fun ScoreReadout(scoreInfo: ScoreInfo) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        ScoreRow(
            stringResource(R.string.game_end_heading_total_score),
            scoreInfo.totalScore,
            isRecord = scoreInfo.totalScore >= (scoreInfo.chapterScoreRecord ?: 0)
        )
        ScoreRow(
            stringResource(R.string.game_end_heading_level_score),
            scoreInfo.levelScore,
            isRecord = scoreInfo.levelScore >= (scoreInfo.levelScoreRecord ?: 0),
        )
        ScoreRow(
            stringResource(R.string.game_end_heading_remaining_time),
            scoreInfo.remainingSeconds,
            isRecord = scoreInfo.remainingSeconds >= (scoreInfo.levelTimeRecord ?: 0),
        )
    }
}

@Composable
private fun ScoreRow(
    label: String,
    score: Int,
    isRecord: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
    ) {
        var maxBaseline by remember { mutableFloatStateOf(0f) }
        fun updateMaxBaseline(textLayoutResult: TextLayoutResult) {
            maxBaseline =
                max(maxBaseline, textLayoutResult.size.height - textLayoutResult.lastBaseline)
        }

        val topBaselinePadding = with(LocalDensity.current) { maxBaseline.toDp() }
        Text(
            text = label,
            onTextLayout = ::updateMaxBaseline,
            modifier = Modifier.paddingFromBaseline(bottom = topBaselinePadding)
        )
        Spacer(
            modifier = Modifier
                .padding(topBaselinePadding)
                .weight(1f)
                .height(1.dp)
                .background(LocalContentColor.current)

        )
        if (isRecord) {
            Text(
                text = stringResource(R.string.game_end_new_record),
                onTextLayout = ::updateMaxBaseline,
                modifier = Modifier.paddingFromBaseline(bottom = topBaselinePadding)
            )
            Spacer(
                modifier = Modifier
                    .padding(topBaselinePadding)
                    .weight(1f)
                    .height(1.dp)
                    .background(LocalContentColor.current)

            )
        }
        Text(
            text = score.toString(),
            onTextLayout = ::updateMaxBaseline,
            modifier = Modifier.paddingFromBaseline(bottom = topBaselinePadding)
        )
    }
}

@Composable
private fun DisplayMenu(
    color: Color,
    @StringRes titleText: Int,
    @StringRes buttonText: Int,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(750)
        enabled = true
    }
    PopMegaButton(
        mainText = titleText,
        subText = buttonText,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
            contentColor = Color.White,
            disabledBackgroundColor = color,
            disabledContentColor = Color.White,
        ),
        clickEnabled = enabled,
        modifier = modifier
    )
}
