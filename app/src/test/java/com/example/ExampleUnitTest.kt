package com.example

import com.example.data.DefaultStoryData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun defaultExpressions_matchNineAcrossThreeMovies() {
        val expressions = DefaultStoryData.initialExpressions
        assertEquals(9, expressions.size)
        val movies = expressions.map { it.movieTitle }.toSet()
        assertEquals(setOf("Notting Hill", "Minari", "Inside Out 2"), movies)
    }

    @Test
    fun defaultStoryboard_hasFourBeatsAndSixtyFourWords() {
        val beats = DefaultStoryData.initialBeats
        assertEquals(4, beats.size)
        val totalWords = DefaultStoryData.countWordsInEnglishLines(beats)
        assertEquals(62, totalWords)
        assertTrue(totalWords in 60..70)
    }

    @Test
    fun defaultQuiz_hasExpectedCorrectAnswers() {
        val questions = DefaultStoryData.quizQuestions
        assertEquals(2, questions.size)
        assertEquals("B) I'm really bummed", questions[0].options[questions[0].correctIndex])
        assertEquals("C) I'm over the moon", questions[1].options[questions[1].correctIndex])
    }
}
