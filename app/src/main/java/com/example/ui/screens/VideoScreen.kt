package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.media.ClipAudioPlayer
import com.example.media.SpokenSubtitleState
import com.example.model.QuizQuestion
import com.example.model.StoryBeat
import com.example.ui.components.BigPrimaryButton
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.sin

private const val TOTAL_VIDEO_SECONDS = 30f

@Composable
fun VideoScreen(
    beats: List<StoryBeat>,
    subtitlesEnabled: Boolean,
    quizQuestions: List<QuizQuestion>,
    quizSelections: Map<Int, Int>,
    onToggleSubtitles: () -> Unit,
    onSelectQuizAnswer: (Int, Int) -> Unit,
    onGoToQuiz: () -> Unit,
    onFinishToFinal: () -> Unit
) {
    val context = LocalContext.current
    val audioPlayer = remember(context) { ClipAudioPlayer(context) }
    val subtitleState by audioPlayer.subtitleState.collectAsState()

    var hasStarted by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var elapsedSec by remember { mutableFloatStateOf(0f) }
    var lastSpokenBeatId by remember { mutableIntStateOf(-1) }
    var showQuizPopup by remember { mutableStateOf(false) }
    var isFullScreen by remember { mutableStateOf(false) }

    DisposableEffect(audioPlayer) {
        onDispose {
            audioPlayer.release()
        }
    }

    BackHandler(enabled = isFullScreen) {
        isFullScreen = false
    }

    val currentBeat = remember(beats, elapsedSec) {
        beats.firstOrNull { elapsedSec >= it.startSec && elapsedSec < it.endSec }
            ?: beats.last()
    }

    LaunchedEffect(isPlaying, currentBeat.id) {
        if (isPlaying && lastSpokenBeatId != currentBeat.id) {
            lastSpokenBeatId = currentBeat.id
            audioPlayer.speakBeat(currentBeat.id, currentBeat.lines)
        }
    }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        var lastTime = System.currentTimeMillis()
        while (isPlaying && elapsedSec < TOTAL_VIDEO_SECONDS) {
            delay(33L)
            val now = System.currentTimeMillis()
            val dt = (now - lastTime).coerceAtMost(100L) / 1000f
            lastTime = now
            elapsedSec = (elapsedSec + dt).coerceAtMost(TOTAL_VIDEO_SECONDS)
            if (elapsedSec >= TOTAL_VIDEO_SECONDS) {
                isPlaying = false
                isFullScreen = false
                audioPlayer.stop()
                showQuizPopup = true
            }
        }
    }

    fun startOrResumePlayback() {
        if (elapsedSec >= TOTAL_VIDEO_SECONDS) {
            elapsedSec = 0f
            lastSpokenBeatId = -1
        }
        hasStarted = true
        isPlaying = true
        if (lastSpokenBeatId != currentBeat.id) {
            lastSpokenBeatId = currentBeat.id
            audioPlayer.speakBeat(currentBeat.id, currentBeat.lines)
        }
    }

    fun replayFromBeginning() {
        audioPlayer.stop()
        showQuizPopup = false
        elapsedSec = 0f
        lastSpokenBeatId = -1
        hasStarted = true
        isPlaying = true
    }

    fun pausePlayback() {
        isPlaying = false
        audioPlayer.stop()
        lastSpokenBeatId = -1
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Step 4 · 30s Animated Short Film",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Inline Video Player Viewport
            Card(
                shape = RoundedCornerShape(26.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .testTag("video_player_frame")
            ) {
                VideoStageViewport(
                    hasStarted = hasStarted,
                    isPlaying = isPlaying,
                    elapsedSec = elapsedSec,
                    currentBeat = currentBeat,
                    subtitleState = subtitleState,
                    subtitlesEnabled = subtitlesEnabled,
                    isFullScreen = false,
                    onPlayFromCover = { startOrResumePlayback() },
                    onToggleSubtitles = onToggleSubtitles,
                    onToggleFullScreen = { isFullScreen = true }
                )
            }

            // Timeline & Interactive Controls
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (!hasStarted) "Ready to Play" else currentBeat.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = String.format(
                                "00:%02d / 00:30",
                                elapsedSec.roundToInt().coerceIn(0, 30)
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (elapsedSec / TOTAL_VIDEO_SECONDS).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isPlaying) pausePlayback() else startOrResumePlayback()
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("video_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    isPlaying -> "Pause"
                                    hasStarted && elapsedSec < TOTAL_VIDEO_SECONDS -> "Resume"
                                    else -> "Play"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = { replayFromBeginning() },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("video_replay_button")
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = "Replay from start")
                        }

                        OutlinedButton(
                            onClick = onToggleSubtitles,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("video_cc_control_button")
                        ) {
                            Icon(
                                imageVector = if (subtitlesEnabled) {
                                    Icons.Default.ClosedCaption
                                } else {
                                    Icons.Default.ClosedCaptionDisabled
                                },
                                contentDescription = if (subtitlesEnabled) "Subtitles On" else "Subtitles Off"
                            )
                        }

                        OutlinedButton(
                            onClick = { isFullScreen = true },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("video_fullscreen_control_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Full Screen"
                            )
                        }
                    }
                }
            }

            // Beat Quick-Jump Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                beats.forEach { beat ->
                    val isActive = hasStarted && currentBeat.id == beat.id
                    Surface(
                        onClick = {
                            audioPlayer.stop()
                            hasStarted = true
                            elapsedSec = beat.startSec
                            lastSpokenBeatId = -1
                            isPlaying = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isActive) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("beat_jump_${beat.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Beat ${beat.id}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                            Text(
                                text = beat.timeRange,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            BigPrimaryButton(
                text = stringResource(R.string.btn_take_quiz),
                icon = Icons.Default.Quiz,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                onClick = {
                    pausePlayback()
                    onGoToQuiz()
                },
                modifier = Modifier.testTag("go_to_quiz_button")
            )
        }
    }

    if (isFullScreen) {
        FullScreenVideoDialog(
            hasStarted = hasStarted,
            isPlaying = isPlaying,
            elapsedSec = elapsedSec,
            currentBeat = currentBeat,
            subtitleState = subtitleState,
            subtitlesEnabled = subtitlesEnabled,
            onPlayPause = {
                if (isPlaying) pausePlayback() else startOrResumePlayback()
            },
            onReplay = { replayFromBeginning() },
            onToggleSubtitles = onToggleSubtitles,
            onExitFullScreen = { isFullScreen = false }
        )
    }

    if (showQuizPopup) {
        PostVideoQuizPopup(
            questions = quizQuestions,
            selections = quizSelections,
            onSelectAnswer = onSelectQuizAnswer,
            onReplayVideo = { replayFromBeginning() },
            onContinueToFinal = {
                showQuizPopup = false
                onFinishToFinal()
            },
            onDismiss = { showQuizPopup = false }
        )
    }
}

@Composable
private fun VideoStageViewport(
    hasStarted: Boolean,
    isPlaying: Boolean,
    elapsedSec: Float,
    currentBeat: StoryBeat,
    subtitleState: SpokenSubtitleState,
    subtitlesEnabled: Boolean,
    isFullScreen: Boolean,
    onPlayFromCover: () -> Unit,
    onToggleSubtitles: () -> Unit,
    onToggleFullScreen: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (!hasStarted) {
            CoverThumbnail(onPlayClick = onPlayFromCover)
        } else {
            AnimatedBeatStage(
                beat = currentBeat,
                elapsedSec = elapsedSec,
                isPlaying = isPlaying
            )

            // Bottom Word-for-Word White Bold English Subtitles (Max 2 lines)
            androidx.compose.animation.AnimatedVisibility(
                visible = subtitlesEnabled,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                val activeText = if (subtitleState.beatId == currentBeat.id &&
                    subtitleState.fullCleanText.isNotBlank()
                ) {
                    subtitleState.fullCleanText
                } else {
                    currentBeat.lines.firstOrNull()?.englishText?.replace("**", "").orEmpty()
                }
                val spokenCount = if (subtitleState.beatId == currentBeat.id) {
                    subtitleState.spokenWordCount
                } else {
                    0
                }
                WordSyncedSubtitleBanner(
                    text = activeText,
                    spokenWordCount = spokenCount
                )
            }
        }

        // Top overlay: Beat/Voice tag + CC On/Off + Full Screen Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasStarted) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.58f),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    val voiceTag = if (subtitleState.beatId == currentBeat.id &&
                        subtitleState.speaker.isNotBlank()
                    ) {
                        " · ${subtitleState.speaker} (${subtitleState.emotionLabel})"
                    } else ""
                    Text(
                        text = "${currentBeat.timeRange}$voiceTag",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasStarted) {
                    Surface(
                        onClick = onToggleSubtitles,
                        shape = RoundedCornerShape(50),
                        color = if (subtitlesEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Black.copy(alpha = 0.65f)
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag(if (isFullScreen) "fs_cc_toggle_button" else "cc_toggle_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (subtitlesEnabled) {
                                    Icons.Default.ClosedCaption
                                } else {
                                    Icons.Default.ClosedCaptionDisabled
                                },
                                contentDescription = if (subtitlesEnabled) {
                                    "Subtitles On"
                                } else {
                                    "Subtitles Off"
                                },
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (subtitlesEnabled) "CC On" else "CC Off",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Surface(
                    onClick = onToggleFullScreen,
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag(if (isFullScreen) "fs_exit_button" else "fullscreen_toggle_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isFullScreen) {
                                Icons.Default.FullscreenExit
                            } else {
                                Icons.Default.Fullscreen
                            },
                            contentDescription = if (isFullScreen) {
                                "Exit Full Screen"
                            } else {
                                "Full Screen"
                            },
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FullScreenVideoDialog(
    hasStarted: Boolean,
    isPlaying: Boolean,
    elapsedSec: Float,
    currentBeat: StoryBeat,
    subtitleState: SpokenSubtitleState,
    subtitlesEnabled: Boolean,
    onPlayPause: () -> Unit,
    onReplay: () -> Unit,
    onToggleSubtitles: () -> Unit,
    onExitFullScreen: () -> Unit
) {
    Dialog(
        onDismissRequest = onExitFullScreen,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .systemBarsPadding()
                .testTag("fullscreen_video_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    ) {
                        VideoStageViewport(
                            hasStarted = hasStarted,
                            isPlaying = isPlaying,
                            elapsedSec = elapsedSec,
                            currentBeat = currentBeat,
                            subtitleState = subtitleState,
                            subtitlesEnabled = subtitlesEnabled,
                            isFullScreen = true,
                            onPlayFromCover = onPlayPause,
                            onToggleSubtitles = onToggleSubtitles,
                            onToggleFullScreen = onExitFullScreen
                        )
                    }
                }

                // Bottom Cinema Transport Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onPlayPause,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play"
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (elapsedSec / TOTAL_VIDEO_SECONDS).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )

                    Text(
                        text = String.format("00:%02d / 00:30", elapsedSec.roundToInt().coerceIn(0, 30)),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )

                    TextButton(onClick = onReplay) {
                        Icon(Icons.Default.Replay, contentDescription = "Replay", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun CoverThumbnail(onPlayClick: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "coverFloat")
    val bobOffset by infinite.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF7AD2F5),
                        Color(0xFFB5ECFA),
                        Color(0xFFFDE2A8)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFFFF59D).copy(alpha = 0.7f),
                radius = size.minDimension * 0.16f,
                center = Offset(size.width * 0.85f, size.height * 0.22f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_poop_icon),
                contentDescription = "Adorable smiling 3D poop character cover",
                modifier = Modifier
                    .size(132.dp)
                    .offset { IntOffset(0, bobOffset.roundToInt()) }
                    .clip(RoundedCornerShape(32.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF217A74)
                ) {
                    Text(
                        text = "30s 3D Animated Clip",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Poop Attack!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF232B2B)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onPlayClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD97248),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("cover_play_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play Movie", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun AnimatedBeatStage(
    beat: StoryBeat,
    elapsedSec: Float,
    isPlaying: Boolean
) {
    val beatDuration = (beat.endSec - beat.startSec).coerceAtLeast(1f)
    val progress = ((elapsedSec - beat.startSec) / beatDuration).coerceIn(0f, 1f)

    val scale = 1.0f + (0.08f * progress)
    val shakeX = if (beat.id == 2 && progress in 0.05f..0.22f && isPlaying) {
        sin(elapsedSec * 45f) * 10f
    } else 0f

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val stageWidth = maxWidth
        val stageHeight = maxHeight

        Image(
            painter = painterResource(id = beat.imageRes),
            contentDescription = beat.sceneDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = shakeX - (18f * progress)
                }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.68f))
                    )
                )
        )

        if (beat.id == 3 || beat.id == 4) {
            val arcX = (stageWidth * (0.15f + 0.65f * progress))
            val arcY = (stageHeight * (0.45f - 0.25f * sin(progress * Math.PI).toFloat()))
            Image(
                painter = painterResource(id = R.drawable.img_poop_icon),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = arcX, y = arcY)
                    .size(44.dp)
                    .clip(CircleShape)
                    .graphicsLayer {
                        rotationZ = progress * 540f
                    }
            )
        }
    }
}

@Composable
private fun WordSyncedSubtitleBanner(
    text: String,
    spokenWordCount: Int
) {
    val words = remember(text) {
        text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    }
    val annotated = remember(words, spokenWordCount) {
        buildAnnotatedString {
            words.forEachIndexed { idx, word ->
                val isReached = spokenWordCount <= 0 || idx < spokenWordCount
                withStyle(
                    SpanStyle(
                        color = if (isReached) Color.White else Color.White.copy(alpha = 0.68f),
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append(word)
                }
                if (idx < words.lastIndex) append(" ")
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Black.copy(alpha = 0.62f),
        modifier = Modifier.testTag("video_subtitle_banner")
    ) {
        Text(
            text = annotated,
            color = Color.White,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium.copy(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 4f
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun PostVideoQuizPopup(
    questions: List<QuizQuestion>,
    selections: Map<Int, Int>,
    onSelectAnswer: (Int, Int) -> Unit,
    onReplayVideo: () -> Unit,
    onContinueToFinal: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 600.dp)
                .padding(vertical = 24.dp)
                .testTag("post_video_quiz_popup")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        QuizHeaderCard(questions = questions, selections = selections)
                    }
                    itemsIndexed(questions, key = { _, q -> q.id }) { idx, question ->
                        QuizQuestionCard(
                            number = idx + 1,
                            question = question,
                            selectedOption = selections[question.id],
                            onSelectOption = { optIdx -> onSelectAnswer(question.id, optIdx) }
                        )
                    }
                }

                Surface(
                    tonalElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BigPrimaryButton(
                            text = "Finish & See Wrap-Up",
                            onClick = onContinueToFinal,
                            modifier = Modifier.testTag("popup_finish_button")
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = onReplayVideo) {
                                Text("Replay Video")
                            }
                            TextButton(onClick = onDismiss) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
    }
}
