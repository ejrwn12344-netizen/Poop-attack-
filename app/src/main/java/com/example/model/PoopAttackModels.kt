package com.example.model

import androidx.annotation.DrawableRes

data class MovieExpression(
    val id: String,
    val phrase: String,
    val koreanMeaning: String,
    val movieTitle: String
)

data class SceneSettings(
    val place: String,
    val characters: String,
    val situation: String,
    val selectedStyle: String,
    val styleOptions: List<String> = listOf(
        "3D animation style",
        "2D Pastel Storybook",
        "Claymation Comedy",
        "Anime Summer Slice-of-Life"
    )
)

data class DialogueLine(
    val speaker: String,
    val englishText: String,
    val koreanSubtitle: String
)

data class StoryBeat(
    val id: Int,
    val title: String,
    val timeRange: String,
    val startSec: Float,
    val endSec: Float,
    val sceneDescription: String,
    val lines: List<DialogueLine>,
    @DrawableRes val imageRes: Int
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

sealed interface AiRegenerateState {
    data object Idle : AiRegenerateState
    data class Loading(val beatId: Int) : AiRegenerateState
    data class Error(val message: String) : AiRegenerateState
}

sealed interface VideoExportState {
    data object Idle : VideoExportState
    data class Exporting(val progress: Float) : VideoExportState
    data class Success(val displayName: String, val locationHint: String) : VideoExportState
    data class Error(val message: String) : VideoExportState
}
