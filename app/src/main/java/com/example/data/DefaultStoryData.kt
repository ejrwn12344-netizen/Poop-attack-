package com.example.data

import com.example.R
import com.example.model.DialogueLine
import com.example.model.MovieExpression
import com.example.model.QuizQuestion
import com.example.model.SceneSettings
import com.example.model.StoryBeat

object DefaultStoryData {

    val initialExpressions: List<MovieExpression> = listOf(
        MovieExpression(
            id = "expr_1",
            phrase = "was going to",
            koreanMeaning = "~하려고 했었다",
            movieTitle = "Notting Hill"
        ),
        MovieExpression(
            id = "expr_2",
            phrase = "If S had ..., S could ...",
            koreanMeaning = "~라면 ...할 수 있을 텐데",
            movieTitle = "Minari"
        ),
        MovieExpression(
            id = "expr_3",
            phrase = "I'm really bummed",
            koreanMeaning = "기분이 안 좋아",
            movieTitle = "Inside Out 2"
        ),
        MovieExpression(
            id = "expr_4",
            phrase = "I'm over the moon",
            koreanMeaning = "뛸 듯이 기뻐",
            movieTitle = "Inside Out 2"
        ),
        MovieExpression(
            id = "expr_5",
            phrase = "low point",
            koreanMeaning = "최악의 순간",
            movieTitle = "Notting Hill"
        ),
        MovieExpression(
            id = "expr_6",
            phrase = "make fun of",
            koreanMeaning = "~를 놀리다",
            movieTitle = "Inside Out 2"
        ),
        MovieExpression(
            id = "expr_7",
            phrase = "crossed the line",
            koreanMeaning = "선을 넘다",
            movieTitle = "Inside Out 2"
        ),
        MovieExpression(
            id = "expr_8",
            phrase = "jerk",
            koreanMeaning = "못된 녀석",
            movieTitle = "Inside Out 2"
        ),
        MovieExpression(
            id = "expr_9",
            phrase = "belong in jail",
            koreanMeaning = "감옥에 가야 한다",
            movieTitle = "Notting Hill"
        )
    )

    val initialSceneSettings = SceneSettings(
        place = "A bright, sunny beach with blue ocean waves and golden sand.",
        characters = "Friend A (young adult male, short brown hair, yellow t-shirt and red swim shorts), Friend B (young adult male, black curly hair, blue tank top and denim shorts), a cute cartoonish brown poop character with tiny smiling eyes on the sand, a middle-aged passerby man in a Hawaiian shirt, and a crowd of beachgoers.",
        situation = "Walking on the beach talking, stepping on cute poop, getting teased, throwing poop back and forth, hitting a passerby, and escalating into a chaotic beach-wide poop fight.",
        selectedStyle = "3D animation style"
    )

    val initialBeats: List<StoryBeat> = listOf(
        StoryBeat(
            id = 1,
            title = "Beat 1: Hook",
            timeRange = "0-7s",
            startSec = 0f,
            endSec = 7f,
            sceneDescription = "Bright 3D animated sunny beach. Friend A and Friend B walk along the shore smiling and chatting. Ahead on the sand sits a cute, cartoon poop character.",
            lines = listOf(
                DialogueLine(
                    speaker = "Friend A",
                    englishText = "Nice day! I **was going to** stay home.",
                    koreanSubtitle = "A: 날씨 좋다! 집에 있으려고 했는데."
                ),
                DialogueLine(
                    speaker = "Friend B",
                    englishText = "**If we had time**, we **could** walk further.",
                    koreanSubtitle = "B: 그러게! 시간이 있다면 더 걸을 텐데."
                )
            ),
            imageRes = R.drawable.img_beat_1
        ),
        StoryBeat(
            id = 2,
            title = "Beat 2: Problem",
            timeRange = "7-15s",
            startSec = 7f,
            endSec = 15f,
            sceneDescription = "Friend A steps squarely onto the cute poop. Friend A looks down in disgust and gets bummed out. Friend B points at his shoe and laughs hysterically.",
            lines = listOf(
                DialogueLine(
                    speaker = "Friend A",
                    englishText = "Eww! I stepped in poop! **I'm really bummed**!",
                    koreanSubtitle = "A: 으악! 나 똥 밟았어! 기분 진짜 안 좋아졌어!"
                ),
                DialogueLine(
                    speaker = "Friend B",
                    englishText = "Haha! **I'm over the moon**!",
                    koreanSubtitle = "B: 하하! 나 너무 신나!"
                )
            ),
            imageRes = R.drawable.img_beat_2
        ),
        StoryBeat(
            id = 3,
            title = "Beat 3: Turn",
            timeRange = "15-23s",
            startSec = 15f,
            endSec = 23f,
            sceneDescription = "Furious Friend A scoops up the cute poop and throws it at Friend B. Friend B is shocked, gets hit, turns dead serious, and angrily hurls poop back at Friend A.",
            lines = listOf(
                DialogueLine(
                    speaker = "Friend A",
                    englishText = "This is the real **low point**! You **make fun of** me?! Take this!",
                    koreanSubtitle = "A: 오늘 진짜 최악의 순간이네! 감히 날 보고 놀려?! 이거나 먹어라!"
                ),
                DialogueLine(
                    speaker = "Friend B",
                    englishText = "Gah! You threw poop at me?! You **crossed the line**!",
                    koreanSubtitle = "B: 악! 너 나한테 지금 똥을 던진 거야?! 너 완전 선 넘었어!"
                )
            ),
            imageRes = R.drawable.img_beat_3
        ),
        StoryBeat(
            id = 4,
            title = "Beat 4: Close",
            timeRange = "23-30s",
            startSec = 23f,
            endSec = 30f,
            sceneDescription = "A flying piece of poop misses and splatters onto a nearby middle-aged man in a Hawaiian shirt. He gets outraged and throws it back. Soon, multiple beachgoers start throwing poop at each other in a wild, funny beach fight.",
            lines = listOf(
                DialogueLine(
                    speaker = "Passerby",
                    englishText = "Gah! What is this?! You **jerk**s! You **belong in jail**!",
                    koreanSubtitle = "Passerby: 악! 이게 뭐야?! 저 자식들이! 너네 감옥이나 가라!"
                )
            ),
            imageRes = R.drawable.img_beat_4
        )
    )

    val quizQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = 1,
            question = "What expression did Friend A use to describe his feeling after stepping in poop?",
            options = listOf(
                "A) I'm over the moon",
                "B) I'm really bummed",
                "C) Low point",
                "D) Read the room"
            ),
            correctIndex = 1,
            explanation = "\"I'm really bummed\" (from Inside Out 2) means feeling disappointed or down!"
        ),
        QuizQuestion(
            id = 2,
            question = "What expression did Friend B use when laughing at Friend A stepping in poop?",
            options = listOf(
                "A) I'm really bummed",
                "B) Crossed the line",
                "C) I'm over the moon",
                "D) Belong in jail"
            ),
            correctIndex = 2,
            explanation = "\"I'm over the moon\" (from Inside Out 2) means feeling thrilled or super happy!"
        )
    )

    fun countWordsInEnglishLines(beats: List<StoryBeat>): Int {
        return beats.flatMap { it.lines }
            .flatMap { line ->
                line.englishText
                    .replace("**", "")
                    .trim()
                    .split(Regex("\\s+"))
                    .filter { it.isNotBlank() }
            }
            .size
    }
}
