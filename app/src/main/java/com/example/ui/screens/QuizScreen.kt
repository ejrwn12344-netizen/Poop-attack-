package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.QuizQuestion
import com.example.ui.components.BigPrimaryButton
import com.example.ui.theme.SageSuccess
import com.example.ui.theme.SoftAmberContainer
import com.example.ui.theme.SoftSageContainer
import com.example.ui.theme.WarmAmberWarning

@Composable
fun QuizScreen(
    questions: List<QuizQuestion>,
    selections: Map<Int, Int>,
    onSelectAnswer: (Int, Int) -> Unit,
    onFinishClick: () -> Unit
) {
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
                        text = stringResource(R.string.btn_finish),
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = onFinishClick,
                        modifier = Modifier.testTag("quiz_to_final_button")
                    )
                }
            }
        }
    }
}

@Composable
fun QuizHeaderCard(
    questions: List<QuizQuestion>,
    selections: Map<Int, Int>
) {
    val correctCount = questions.count { q -> selections[q.id] == q.correctIndex }
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Quick Comprehension Quiz",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Score: $correctCount / ${questions.size} correct",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun QuizQuestionCard(
    number: Int,
    question: QuizQuestion,
    selectedOption: Int?,
    onSelectOption: (Int) -> Unit
) {
    val isAnswered = selectedOption != null
    val isCorrect = selectedOption == question.correctIndex

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_question_${question.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = "Question $number",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.forEachIndexed { optIndex, optionText ->
                    val isSelected = selectedOption == optIndex
                    val isThisCorrect = optIndex == question.correctIndex

                    val bgColor = when {
                        isSelected && isThisCorrect -> SoftSageContainer
                        isSelected && !isThisCorrect -> SoftAmberContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    }
                    val borderColor = when {
                        isSelected && isThisCorrect -> SageSuccess
                        isSelected && !isThisCorrect -> WarmAmberWarning
                        else -> MaterialTheme.colorScheme.outline
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectOption(optIndex) }
                            .padding(horizontal = 16.dp, vertical = 15.dp)
                            .testTag("quiz_${question.id}_opt_$optIndex"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (isThisCorrect) {
                                    Icons.Default.CheckCircle
                                } else {
                                    Icons.Default.ErrorOutline
                                },
                                contentDescription = if (isThisCorrect) "Correct" else "Try again",
                                tint = if (isThisCorrect) SageSuccess else WarmAmberWarning
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isAnswered) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isCorrect) SoftSageContainer else SoftAmberContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_feedback_${question.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = if (isCorrect) SageSuccess else WarmAmberWarning
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isCorrect) {
                                "Correct! ${question.explanation}"
                            } else {
                                "Try Again! Listen closely to how the character reacts and pick another option."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCorrect) SageSuccess else WarmAmberWarning
                        )
                    }
                }
            }
        }
    }
}
