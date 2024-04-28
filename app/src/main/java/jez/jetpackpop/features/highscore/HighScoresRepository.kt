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

    suspend fun updateLevelScore(
        chapterName: String,
        levelIndex: Int,
        score: Int,
        timeRemaining: Int
    ) {
        dataStore.updateData { proto ->
            val existingChapterRecord =
                proto.chapterLevelScoresList.firstOrNull { it.chapterName == chapterName }
            val existingLevelRecord = existingChapterRecord?.scoresList?.getOrNull(levelIndex)
            val isExistingRecordHigherScore =
                existingLevelRecord?.score?.let { it > score } ?: false

            if (existingChapterRecord == null) {
                proto
            } else if (isExistingRecordHigherScore) {
                with(proto.toBuilder()) {
                    addNewChapterLevelScore(chapterName, levelIndex, score, timeRemaining)
                    build()
                }
            } else {
                with(proto.toBuilder()) {
                    addOrSetLevelScore(existingChapterRecord, levelIndex, score, timeRemaining)
                    build()
                }
            }
        }
    }

    private fun HighScoresProto.Builder.addNewChapterLevelScore(
        chapterName: String,
        levelIndex: Int,
        score: Int,
        timeRemaining: Int,
    ) {
        val levelScoreProto = LevelScoreProto.newBuilder()
            .setLevel(levelIndex)
            .setScore(score)
            .setTimeRemaining(timeRemaining)
            .build()
        val chapterLevelRecord = ChapterLevelScoresProto.newBuilder()
            .setChapterName(chapterName)
            .addScores(levelScoreProto)
        addChapterLevelScores(chapterLevelRecord)
    }

    private fun HighScoresProto.Builder.addOrSetLevelScore(
        existingChapterRecord: ChapterLevelScoresProto,
        index: Int,
        score: Int,
        timeRemaining: Int,
    ) {
        val levelScoreProto = LevelScoreProto.newBuilder()
            .setLevel(index)
            .setScore(score)
            .setTimeRemaining(timeRemaining)
            .build()
        if (index >= chapterScoresCount || getChapterScores(index) == null) {
            existingChapterRecord.toBuilder()
                .addScores(index, levelScoreProto)
        } else {
            existingChapterRecord.toBuilder()
                .setScores(index, levelScoreProto)
        }
    }

    suspend fun updateChapterScore(chapterName: String, score: Int) {
        dataStore.updateData { proto ->
            val index = proto.chapterScoresList.indexOfFirst { it.chapterName == chapterName }.let {
                if (it < 0) proto.chapterScoresCount
                else it
            }
            val existingScore = proto.chapterScoresList.getOrNull(index)?.score ?: -1

            Timber.i("updatesScore: existingScore $existingScore newScore $score")
            if (score > existingScore) {
                with(proto.toBuilder()) {
                    addOrSetChapterScore(index, chapterName, score)
                    build()
                }
            } else {
                proto
            }
        }
    }

    private fun HighScoresProto.Builder.addOrSetChapterScore(
        index: Int,
        chapterName: String,
        score: Int
    ) {
        val chapterScoreProto = ChapterScoreProto.newBuilder()
            .setChapterName(chapterName)
            .setScore(score)
            .build()
        if (index >= chapterScoresCount || getChapterScores(index) == null) {
            addChapterScores(index, chapterScoreProto)
        } else {
            setChapterScores(index, chapterScoreProto)
        }
    }
}
