package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.AiRegenerateState
import com.example.model.DialogueLine
import com.example.model.StoryBeat
import com.example.ui.components.BigPrimaryButton
import com.example.ui.components.FormattedExpressionText
import com.example.ui.theme.SageSuccess
import com.example.ui.theme.SoftAmberContainer
import com.example.ui.theme.SoftRoseContainer
import com.example.ui.theme.SoftSageContainer
import com.example.ui.theme.WarmAmberWarning

@Composable
fun StoryboardScreen(
    beats: List<StoryBeat>,
    totalWordCount: Int,
    aiState: AiRegenerateState,
    onEditBeat: (Int, String, List<DialogueLine>) -> Unit,
    onRegenerateBeat: (Int) -> Unit,
    onResetStoryboard: () -> Unit,
    onClearAiError: () -> Unit,
    onWatchVideoClick: () -> Unit
) {
    var editingBeat by remember { mutableStateOf<StoryBeat?>(null) }
    val inTargetRange = totalWordCount in 60..70

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
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step 3 · 4-Beat Storyboard",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            TextButton(
                                onClick = onResetStoryboard,
                                modifier = Modifier.testTag("reset_storyboard_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reset")
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (inTargetRange) SoftSageContainer else SoftAmberContainer
                        ) {
                            Text(
                                text = "Total: $totalWordCount words (Target: 60-70 words · 30s)",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (inTargetRange) SageSuccess else WarmAmberWarning,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                item {
                    AnimatedVisibility(visible = aiState is AiRegenerateState.Error) {
                        val msg = (aiState as? AiRegenerateState.Error)?.message.orEmpty()
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SoftRoseContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = onClearAiError) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }
                }

                items(beats, key = { it.id }) { beat ->
                    val isRegenerating = aiState is AiRegenerateState.Loading && aiState.beatId == beat.id
                    BeatCard(
                        beat = beat,
                        isRegenerating = isRegenerating,
                        anyRegenerating = aiState is AiRegenerateState.Loading,
                        onEditClick = { editingBeat = beat },
                        onRegenerateClick = { onRegenerateBeat(beat.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    BigPrimaryButton(
                        text = stringResource(R.string.btn_watch_video),
                        icon = Icons.Default.PlayArrow,
                        onClick = onWatchVideoClick,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.testTag("watch_video_button")
                    )
                }
            }
        }
    }

    editingBeat?.let { beat ->
        EditBeatDialog(
            beat = beat,
            onDismiss = { editingBeat = null },
            onSave = { newScene, newLines ->
                onEditBeat(beat.id, newScene, newLines)
                editingBeat = null
            }
        )
    }
}

@Composable
private fun BeatCard(
    beat: StoryBeat,
    isRegenerating: Boolean,
    anyRegenerating: Boolean,
    onEditClick: () -> Unit,
    onRegenerateClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("beat_card_${beat.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = beat.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = beat.timeRange,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = beat.imageRes),
                    contentDescription = "${beat.title} preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Scene",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = beat.sceneDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "English Lines",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    beat.lines.forEach { line ->
                        Column {
                            Text(
                                text = "${line.speaker}:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            FormattedExpressionText(
                                rawText = line.englishText,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = line.koreanSubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    enabled = !anyRegenerating,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("edit_beat_${beat.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Beat", style = MaterialTheme.typography.labelLarge)
                }

                Button(
                    onClick = onRegenerateClick,
                    enabled = !anyRegenerating,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("regenerate_beat_${beat.id}")
                ) {
                    if (isRegenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rewriting...", style = MaterialTheme.typography.labelMedium)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Regenerate", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditBeatDialog(
    beat: StoryBeat,
    onDismiss: () -> Unit,
    onSave: (String, List<DialogueLine>) -> Unit
) {
    var sceneText by remember { mutableStateOf(beat.sceneDescription) }
    var editableLines by remember { mutableStateOf(beat.lines) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit ${beat.title} (${beat.timeRange})",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = sceneText,
                    onValueChange = { sceneText = it },
                    label = { Text("Scene Description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Tip: Wrap target expressions with **...** to keep them bold!",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                editableLines.forEachIndexed { index, line ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Line ${index + 1} (${line.speaker})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = line.englishText,
                            onValueChange = { updated ->
                                editableLines = editableLines.toMutableList().also {
                                    it[index] = line.copy(englishText = updated)
                                }
                            },
                            label = { Text("English Line") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = line.koreanSubtitle,
                            onValueChange = { updated ->
                                editableLines = editableLines.toMutableList().also {
                                    it[index] = line.copy(koreanSubtitle = updated)
                                }
                            },
                            label = { Text("Korean Subtitle") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(sceneText, editableLines) }) {
                Text("Save Beat", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}
