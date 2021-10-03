package jez.jetpackpop.features.highscore

import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import jez.jetpackpop.ChapterScoreProto
import jez.jetpackpop.HighScoresProto
import jez.jetpackpop.features.app.domain.GameChapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
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
                Log.e("HighScoresRepository", "Error reading sort order preferences.", exception)
                emit(HighScoresProto.getDefaultInstance())
            } else {
                throw exception
            }
        }
        .map { proto ->
            HighScores(
                proto.scoresOrBuilderList.map { GameChapter.withName(it.chapterName) to it.score }
                    .toMap()
            )
        }

    suspend fun updateHighScores(scores: HighScores) =
        dataStore.updateData { proto ->
            with(proto.toBuilder()) {
                val sortedScoreData = scores.chapterScores.entries
                    .sortedBy { it.key.ordinal }
                    .withIndex()
                    .toList()
                for ((index, score) in sortedScoreData) {
                    val chapter = score.key
                    if (index < scoresCount && getScores(index).score >= score.value) continue
                    addOrSetScore(index, chapter.persistenceName, score.value)
                }
                build()
            }
        }

    private fun HighScoresProto.Builder.addOrSetScore(index: Int, chapterName: String, score: Int) {
        val chapterScoreProto = ChapterScoreProto.newBuilder()
            .setChapterName(chapterName)
            .setScore(score)
            .build()
        if (index >= scoresCount || getScores(index) == null) {
            addScores(index, chapterScoreProto)
        } else {
            setScores(index, chapterScoreProto)
        }
    }
}
