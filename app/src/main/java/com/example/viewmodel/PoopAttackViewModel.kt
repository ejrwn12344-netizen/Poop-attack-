package com.example.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.BeatGeneratorRepository
import com.example.data.DefaultStoryData
import com.example.media.VideoExporter
import com.example.model.AiRegenerateState
import com.example.model.DialogueLine
import com.example.model.MovieExpression
import com.example.model.QuizQuestion
import com.example.model.SceneSettings
import com.example.model.StoryBeat
import com.example.model.VideoExportState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "PoopAttackViewModel"

class PoopAttackViewModel : ViewModel() {

    private val beatRepository = BeatGeneratorRepository()

    private val _expressions = MutableStateFlow(DefaultStoryData.initialExpressions)
    val expressions: StateFlow<List<MovieExpression>> = _expressions.asStateFlow()

    val validationWarning: StateFlow<String?> = _expressions.map { list ->
        val uniqueMovies = list.map { it.movieTitle.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .distinct()
            .size
        when {
            list.size < 5 && uniqueMovies <= 1 ->
                "Heads up: You have fewer than 5 expressions (${list.size}) and only $uniqueMovies movie. Add at least 5 expressions from 2+ movies!"
            list.size < 5 ->
                "Heads up: You currently have ${list.size} expressions. Add at least 5 expressions for a complete 30-second clip!"
            uniqueMovies <= 1 ->
                "Heads up: Only $uniqueMovies movie is represented. Mix expressions from at least 2 movies!"
            else -> null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _sceneSettings = MutableStateFlow(DefaultStoryData.initialSceneSettings)
    val sceneSettings: StateFlow<SceneSettings> = _sceneSettings.asStateFlow()

    private val _beats = MutableStateFlow(DefaultStoryData.initialBeats)
    val beats: StateFlow<List<StoryBeat>> = _beats.asStateFlow()

    val totalWordCount: StateFlow<Int> = _beats.map { list ->
        DefaultStoryData.countWordsInEnglishLines(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 64)

    private val _aiState = MutableStateFlow<AiRegenerateState>(AiRegenerateState.Idle)
    val aiState: StateFlow<AiRegenerateState> = _aiState.asStateFlow()

    private val _subtitlesEnabled = MutableStateFlow(true)
    val subtitlesEnabled: StateFlow<Boolean> = _subtitlesEnabled.asStateFlow()

    private val _quizQuestions = MutableStateFlow(DefaultStoryData.quizQuestions)
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    // Map of questionId -> selected option index
    private val _quizSelections = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quizSelections: StateFlow<Map<Int, Int>> = _quizSelections.asStateFlow()

    private val _exportState = MutableStateFlow<VideoExportState>(VideoExportState.Idle)
    val exportState: StateFlow<VideoExportState> = _exportState.asStateFlow()

    // --- Input Screen Actions ---

    fun addExpression(phrase: String, koreanMeaning: String, movieTitle: String) {
        val cleanPhrase = phrase.trim()
        val cleanKorean = koreanMeaning.trim()
        val cleanMovie = movieTitle.trim()
        if (cleanPhrase.isBlank()) return
        val newItem = MovieExpression(
            id = "expr_${UUID.randomUUID().toString().take(8)}",
            phrase = cleanPhrase,
            koreanMeaning = cleanKorean.ifBlank { "뜻 추가됨" },
            movieTitle = cleanMovie.ifBlank { "My Movie" }
        )
        _expressions.value = _expressions.value + newItem
    }

    fun updateExpression(id: String, phrase: String, koreanMeaning: String, movieTitle: String) {
        _expressions.value = _expressions.value.map { item ->
            if (item.id == id) {
                item.copy(
                    phrase = phrase.trim().ifBlank { item.phrase },
                    koreanMeaning = koreanMeaning.trim().ifBlank { item.koreanMeaning },
                    movieTitle = movieTitle.trim().ifBlank { item.movieTitle }
                )
            } else item
        }
    }

    fun deleteExpression(id: String) {
        _expressions.value = _expressions.value.filterNot { it.id == id }
    }

    fun resetExpressions() {
        _expressions.value = DefaultStoryData.initialExpressions
    }

    // --- Scene Screen Actions ---

    fun updatePlace(place: String) {
        _sceneSettings.value = _sceneSettings.value.copy(place = place)
    }

    fun updateCharacters(characters: String) {
        _sceneSettings.value = _sceneSettings.value.copy(characters = characters)
    }

    fun updateSituation(situation: String) {
        _sceneSettings.value = _sceneSettings.value.copy(situation = situation)
    }

    fun selectArtStyle(style: String) {
        _sceneSettings.value = _sceneSettings.value.copy(selectedStyle = style)
    }

    // --- Storyboard Screen Actions ---

    fun updateBeat(beatId: Int, sceneDescription: String, lines: List<DialogueLine>) {
        _beats.value = _beats.value.map { beat ->
            if (beat.id == beatId) {
                beat.copy(sceneDescription = sceneDescription.trim(), lines = lines)
            } else beat
        }
    }

    fun resetStoryboard() {
        _beats.value = DefaultStoryData.initialBeats
        _aiState.value = AiRegenerateState.Idle
    }

    fun regenerateBeatWithAi(beatId: Int) {
        val target = _beats.value.firstOrNull { it.id == beatId } ?: return
        viewModelScope.launch {
            _aiState.value = AiRegenerateState.Loading(beatId)
            try {
                val updated = beatRepository.regenerateBeat(
                    targetBeat = target,
                    allBeats = _beats.value,
                    expressions = _expressions.value,
                    sceneSettings = _sceneSettings.value
                )
                _beats.value = _beats.value.map { if (it.id == beatId) updated else it }
                _aiState.value = AiRegenerateState.Idle
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI beat regeneration error", e)
                _aiState.value = AiRegenerateState.Error(
                    e.localizedMessage ?: "Could not regenerate Beat $beatId. Check your connection and try again."
                )
            }
        }
    }

    fun clearAiError() {
        if (_aiState.value is AiRegenerateState.Error) {
            _aiState.value = AiRegenerateState.Idle
        }
    }

    // --- Video Screen Actions ---

    fun toggleSubtitles() {
        _subtitlesEnabled.value = !_subtitlesEnabled.value
    }

    // --- Quiz Screen Actions ---

    fun selectQuizAnswer(questionId: Int, optionIndex: Int) {
        _quizSelections.value = _quizSelections.value + (questionId to optionIndex)
    }

    fun resetQuiz() {
        _quizSelections.value = emptyMap()
    }

    // --- Final Screen Video Export ---

    fun downloadVideo(context: Context) {
        if (_exportState.value is VideoExportState.Exporting) return
        viewModelScope.launch {
            _exportState.value = VideoExportState.Exporting(0f)
            try {
                val (fileName, location) = VideoExporter.exportMp4(
                    context = context.applicationContext,
                    beats = _beats.value,
                    subtitlesEnabled = _subtitlesEnabled.value,
                    onProgress = { progress ->
                        _exportState.value = VideoExportState.Exporting(progress)
                    }
                )
                _exportState.value = VideoExportState.Success(fileName, location)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Video export failed", e)
                _exportState.value = VideoExportState.Error(
                    e.localizedMessage ?: "Failed to export MP4 video."
                )
            }
        }
    }

    fun clearExportState() {
        _exportState.value = VideoExportState.Idle
    }
}
