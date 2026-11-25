package com.example.ai

import android.util.Log
import com.example.model.DialogueLine
import com.example.model.MovieExpression
import com.example.model.SceneSettings
import com.example.model.StoryBeat
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "BeatGeneratorRepo"

class BeatGeneratorRepository {

    private val beatSchema = Schema.obj(
        properties = mapOf(
            "sceneDescription" to Schema.string(
                description = "A vivid 1-2 sentence 3D animated scene description matching the beach setting and characters."
            ),
            "lines" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "speaker" to Schema.string(
                            description = "Speaker name, e.g. Friend A, Friend B, or Passerby"
                        ),
                        "englishText" to Schema.string(
                            description = "Short natural English line with target expressions wrapped in **bold** markers."
                        ),
                        "koreanSubtitle" to Schema.string(
                            description = "Natural Korean translation prefixed with speaker initial, e.g. A: ..."
                        )
                    )
                ),
                description = "1 or 2 short dialogue lines for this beat."
            )
        )
    )

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = beatSchema
            }
        )
    }

    suspend fun regenerateBeat(
        targetBeat: StoryBeat,
        allBeats: List<StoryBeat>,
        expressions: List<MovieExpression>,
        sceneSettings: SceneSettings
    ): StoryBeat {
        val otherBeatsWords = allBeats
            .filter { it.id != targetBeat.id }
            .sumOf { beat ->
                beat.lines.sumOf { line ->
                    line.englishText.replace("**", "").trim()
                        .split(Regex("\\s+")).count { it.isNotBlank() }
                }
            }
        val targetMinWords = (60 - otherBeatsWords).coerceIn(8, 22)
        val targetMaxWords = (70 - otherBeatsWords).coerceIn(targetMinWords + 2, 26)

        val expressionSummary = expressions.joinToString("\n") {
            "- \"${it.phrase}\" (${it.koreanMeaning}, from ${it.movieTitle})"
        }
        val otherStoryContext = allBeats.joinToString("\n") { b ->
            val dialogue = b.lines.joinToString(" / ") { "${it.speaker}: ${it.englishText}" }
            "${b.title} (${b.timeRange}): $dialogue"
        }

        val prompt = """
            You are a friendly short-film scriptwriter for Korean university English learners.
            Rewrite ONLY ${targetBeat.title} (${targetBeat.timeRange}) of a family-friendly, humorous 30-second animated comedy clip called "Poop Attack!".

            Scene Settings:
            - Place: ${sceneSettings.place}
            - Characters: ${sceneSettings.characters}
            - Situation: ${sceneSettings.situation}
            - Style: ${sceneSettings.selectedStyle}

            Available Movie Expressions:
            $expressionSummary

            Current Full 4-Beat Storyboard for Continuity:
            $otherStoryContext

            Strict Requirements for ${targetBeat.title}:
            1. Keep the humor cute, cartoonish, and family-friendly (no real people, celebrities, or brand logos).
            2. Keep total English dialogue words in THIS beat between $targetMinWords and $targetMaxWords words so the 4 beats stay around 60-70 words total.
            3. Use 1-2 target expressions naturally and wrap every target expression in **double asterisks** (e.g. **was going to**, **I'm really bummed**).
            4. Provide natural Korean subtitles for each line (e.g. "A: ...").
        """.trimIndent()

        try {
            val response = withTimeout(60_000L) {
                model.generateContent(prompt)
            }
            val jsonText = response.text ?: throw IllegalStateException("Empty AI response.")
            val root = JSONObject(jsonText)
            val newSceneDesc = root.getString("sceneDescription").trim()
            val linesJson = root.getJSONArray("lines")
            val parsedLines = buildList {
                for (i in 0 until linesJson.length()) {
                    val obj = linesJson.getJSONObject(i)
                    add(
                        DialogueLine(
                            speaker = obj.getString("speaker").trim(),
                            englishText = obj.getString("englishText").trim(),
                            koreanSubtitle = obj.getString("koreanSubtitle").trim()
                        )
                    )
                }
            }
            if (parsedLines.isEmpty()) {
                throw IllegalStateException("AI returned no dialogue lines.")
            }
            return targetBeat.copy(
                sceneDescription = newSceneDesc,
                lines = parsedLines
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to regenerate beat ${targetBeat.id}", e)
            throw e
        }
    }
}
