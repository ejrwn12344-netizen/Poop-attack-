package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.firebase.configureAppCheck
import com.example.ui.components.AppStep
import com.example.ui.components.PoopAttackTopBar
import com.example.ui.screens.FinalScreen
import com.example.ui.screens.InputScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SceneScreen
import com.example.ui.screens.StoryboardScreen
import com.example.ui.screens.VideoScreen
import com.example.ui.theme.PoopAttackTheme
import com.example.viewmodel.PoopAttackViewModel

object Routes {
    const val INPUT = "input"
    const val SCENE = "scene"
    const val STORYBOARD = "storyboard"
    const val VIDEO = "video"
    const val QUIZ = "quiz"
    const val FINAL = "final"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Firebase App Check with launch intent before UI or ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            PoopAttackTheme {
                PoopAttackApp()
            }
        }
    }
}

@Composable
fun PoopAttackApp(
    viewModel: PoopAttackViewModel = viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.INPUT
    val context = LocalContext.current

    val expressions by viewModel.expressions.collectAsState()
    val validationWarning by viewModel.validationWarning.collectAsState()
    val sceneSettings by viewModel.sceneSettings.collectAsState()
    val beats by viewModel.beats.collectAsState()
    val totalWordCount by viewModel.totalWordCount.collectAsState()
    val aiState by viewModel.aiState.collectAsState()
    val koreanSubtitlesEnabled by viewModel.koreanSubtitlesEnabled.collectAsState()
    val englishSubtitlesEnabled by viewModel.englishSubtitlesEnabled.collectAsState()
    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val quizSelections by viewModel.quizSelections.collectAsState()
    val exportState by viewModel.exportState.collectAsState()

    val steps = listOf(
        AppStep(Routes.INPUT, 1, stringResource(R.string.step_expressions)),
        AppStep(Routes.SCENE, 2, stringResource(R.string.step_scene)),
        AppStep(Routes.STORYBOARD, 3, stringResource(R.string.step_storyboard)),
        AppStep(Routes.VIDEO, 4, stringResource(R.string.step_video)),
        AppStep(Routes.QUIZ, 5, stringResource(R.string.step_quiz)),
        AppStep(Routes.FINAL, 6, stringResource(R.string.step_final))
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            PoopAttackTopBar(
                currentRoute = currentRoute,
                steps = steps,
                canNavigateBack = navController.previousBackStackEntry != null,
                onBackClick = { navController.popBackStack() },
                onStepClick = { targetRoute ->
                    if (targetRoute != currentRoute) {
                        navController.navigate(targetRoute) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.INPUT
            ) {
                composable(Routes.INPUT) {
                    InputScreen(
                        expressions = expressions,
                        validationWarning = validationWarning,
                        onAddExpression = viewModel::addExpression,
                        onUpdateExpression = viewModel::updateExpression,
                        onDeleteExpression = viewModel::deleteExpression,
                        onResetDefaults = viewModel::resetExpressions,
                        onNextClick = { navController.navigate(Routes.SCENE) }
                    )
                }

                composable(Routes.SCENE) {
                    SceneScreen(
                        sceneSettings = sceneSettings,
                        onPlaceChange = viewModel::updatePlace,
                        onCharactersChange = viewModel::updateCharacters,
                        onSituationChange = viewModel::updateSituation,
                        onStyleSelect = viewModel::selectArtStyle,
                        onNextClick = { navController.navigate(Routes.STORYBOARD) }
                    )
                }

                composable(Routes.STORYBOARD) {
                    StoryboardScreen(
                        beats = beats,
                        totalWordCount = totalWordCount,
                        aiState = aiState,
                        onEditBeat = viewModel::updateBeat,
                        onRegenerateBeat = viewModel::regenerateBeatWithAi,
                        onResetStoryboard = viewModel::resetStoryboard,
                        onClearAiError = viewModel::clearAiError,
                        onWatchVideoClick = { navController.navigate(Routes.VIDEO) }
                    )
                }

                composable(Routes.VIDEO) {
                    VideoScreen(
                        beats = beats,
                        koreanSubtitlesEnabled = koreanSubtitlesEnabled,
                        englishSubtitlesEnabled = englishSubtitlesEnabled,
                        quizQuestions = quizQuestions,
                        quizSelections = quizSelections,
                        onToggleKoreanSubtitles = viewModel::toggleKoreanSubtitles,
                        onToggleEnglishSubtitles = viewModel::toggleEnglishSubtitles,
                        onSelectQuizAnswer = viewModel::selectQuizAnswer,
                        onGoToQuiz = { navController.navigate(Routes.QUIZ) },
                        onFinishToFinal = { navController.navigate(Routes.FINAL) }
                    )
                }

                composable(Routes.QUIZ) {
                    QuizScreen(
                        questions = quizQuestions,
                        selections = quizSelections,
                        onSelectAnswer = viewModel::selectQuizAnswer,
                        onFinishClick = { navController.navigate(Routes.FINAL) }
                    )
                }

                composable(Routes.FINAL) {
                    FinalScreen(
                        expressions = expressions,
                        quizQuestions = quizQuestions,
                        quizSelections = quizSelections,
                        exportState = exportState,
                        onReplayVideo = {
                            navController.navigate(Routes.VIDEO) {
                                launchSingleTop = true
                            }
                        },
                        onRetakeQuiz = {
                            viewModel.resetQuiz()
                            navController.navigate(Routes.QUIZ) {
                                launchSingleTop = true
                            }
                        },
                        onDownloadVideo = { viewModel.downloadVideo(context) }
                    )
                }
            }
        }
    }
}
