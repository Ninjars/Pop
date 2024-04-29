package jez.jetpackpop.features.highscore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import jez.jetpackpop.ChapterLevelScoresProto
import jez.jetpackpop.ChapterScoreProto
import jez.jetpackpop.HighScoresProto
import jez.jetpackpop.LevelScoreProto
import jez.jetpackpop.features.app.domain.GameChapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object HighScoreDataSerializer : Serializer<HighScoresProto> {
    override val defaultValue: HighScoresProto = HighScoresProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): HighScoresProto =
        try {
            HighScoresProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read HighScoresProto proto.", exception)
        }


    override suspend fun writeTo(t: HighScoresProto, output: OutputStream) = t.writeTo(output)
}

class HighScoresRepository(
    private val dataStore: DataStore<HighScoresProto>
) {
    val highScoresFlow: Flow<HighScores> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Timber.e("HighScoresRepository", "Error reading sort order preferences.", exception)
                emit(HighScoresProto.getDefaultInstance())
            } else {
                throw exception
            }
        }
        .map { proto ->
            HighScores(
                proto.chapterScoresOrBuilderList.associate { GameChapter.withName(it.chapterName) to it.score }
            )
        }

    suspend fun recordEndOfLevel(
        chapterName: String,
        levelIndex: Int,
        levelScore: Int,
        timeRemaining: Int,
        totalChapterScore: Int,
    ) {
        dataStore.updateData { proto ->
            val builder = proto.toBuilder()
            builder
                .updateLevelScore(
                    chapterName = chapterName,
                    level = levelIndex,
                    score = levelScore,
                    timeRemaining = timeRemaining,
                )
                .updateChapterScore(
                    chapterName = chapterName,
                    score = totalChapterScore,
                )
                .build()
        }
    }

    private fun HighScoresProto.Builder.updateLevelScore(
        chapterName: String,
        level: Int,
        score: Int,
        timeRemaining: Int
    ): HighScoresProto.Builder {
        val chapterLevelScoresIndex =
            chapterLevelScoresList.indexOfFirst { it.chapterName == chapterName }.let {
                if (it < 0) chapterLevelScoresCount
                else it
            }
        val existingChapterRecord = chapterLevelScoresList.getOrNull(chapterLevelScoresIndex)

        if (existingChapterRecord == null) {
            Timber.d("updateLevelScore: addNewChapterLevelScore $chapterName level $level score $score time $timeRemaining")
            return addNewChapterLevelScore(chapterName, level, score, timeRemaining)
        }

        val existingLevelRecordIndex =
            existingChapterRecord.scoresList.indexOfFirst { it.level == level }
        val existingLevelScoreRecord =
            existingChapterRecord.scoresList.getOrNull(existingLevelRecordIndex)
        val isExistingRecordHigherScore =
            existingLevelScoreRecord?.score?.let { it > score } ?: false

        return if (isExistingRecordHigherScore) {
            Timber.d("updateLevelScore: existing record has higher score ${existingLevelScoreRecord?.score} vs $score")
            this
        } else {
            Timber.d("updateLevelScore: addOrSetLevelScore $chapterName level $level existingRecordIndex $existingLevelRecordIndex score $score time $timeRemaining")
            addOrSetLevelScore(
                chapterLevelsRecordIndex = chapterLevelScoresIndex,
                existingChapterLevelsRecord = existingChapterRecord,
                level = level,
                recordIndex = existingLevelRecordIndex,
                score = score,
                timeRemaining = timeRemaining
            ).also {
                Timber.d("updateLevelScore: updated level scores list $chapterLevelScoresList")
            }
        }
    }

    private fun HighScoresProto.Builder.addNewChapterLevelScore(
        chapterName: String,
        levelIndex: Int,
        score: Int,
        timeRemaining: Int,
    ): HighScoresProto.Builder {
        val levelScoreProto = LevelScoreProto.newBuilder()
            .setLevel(levelIndex)
            .setScore(score)
            .setTimeRemaining(timeRemaining)
        val chapterLevelRecord = ChapterLevelScoresProto.newBuilder()
            .setChapterName(chapterName)
            .addScores(levelScoreProto)
        addChapterLevelScores(chapterLevelRecord)
        return this
    }

    private fun HighScoresProto.Builder.addOrSetLevelScore(
        chapterLevelsRecordIndex: Int,
        existingChapterLevelsRecord: ChapterLevelScoresProto,
        level: Int,
        recordIndex: Int,
        score: Int,
        timeRemaining: Int,
    ): HighScoresProto.Builder {
        val levelScoreProto = LevelScoreProto.newBuilder()
            .setLevel(level)
            .setScore(score)
            .setTimeRemaining(timeRemaining)
        val existingScoresCount = existingChapterLevelsRecord.scoresCount
        val index = if (recordIndex < 0) existingScoresCount else recordIndex
        val builder =
            if (index >= existingScoresCount || existingChapterLevelsRecord.getScores(index) == null) {
                Timber.d("addOrSetLevelScore: adding new score. chapterLevelsRecordIndex $chapterLevelsRecordIndex level $level recordIndex $recordIndex targetIndex $index chapterLevelScoresCount $existingScoresCount")
                existingChapterLevelsRecord.toBuilder()
                .addScores(index, levelScoreProto)

        } else {
                Timber.d("addOrSetLevelScore: updating new score. chapterLevelsRecordIndex $chapterLevelsRecordIndex level $level recordIndex $recordIndex targetIndex $index chapterLevelScoresCount $existingScoresCount")
                existingChapterLevelsRecord.toBuilder()
                .setScores(index, levelScoreProto)
        }
        setChapterLevelScores(chapterLevelsRecordIndex, builder)
        return this
    }

    private fun HighScoresProto.Builder.updateChapterScore(
        chapterName: String,
        score: Int
    ): HighScoresProto.Builder {
        val index = chapterScoresList.indexOfFirst { it.chapterName == chapterName }.let {
            if (it < 0) chapterScoresCount
            else it
        }
        val existingScore = chapterScoresList.getOrNull(index)?.score ?: -1

        Timber.d("updateChapterScore: $chapterName existingScore $existingScore newScore $score")
        return if (score > existingScore) {
            addOrSetChapterScore(index, chapterName, score)
        } else {
            this
        }
    }

    private fun HighScoresProto.Builder.addOrSetChapterScore(
        index: Int,
        chapterName: String,
        score: Int
    ): HighScoresProto.Builder {
        val chapterScoreProto = ChapterScoreProto.newBuilder()
            .setChapterName(chapterName)
            .setScore(score)
        return if (index >= chapterScoresCount || getChapterScores(index) == null) {
            addChapterScores(index, chapterScoreProto)
        } else {
            setChapterScores(index, chapterScoreProto)
        }
    }
}
