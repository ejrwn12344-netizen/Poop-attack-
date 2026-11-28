package com.example.media

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.model.DialogueLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

private const val TAG = "ClipAudioPlayer"

data class SpokenSubtitleState(
    val beatId: Int = 1,
    val speaker: String = "",
    val emotionLabel: String = "",
    val fullCleanText: String = "",
    val koreanSubtitle: String = "",
    val spokenWordCount: Int = 0,
    val totalWords: Int = 0
)

private data class VoicePerformance(
    val voice: Voice?,
    val pitch: Float,
    val rate: Float,
    val emotionLabel: String
)

class ClipAudioPlayer(context: Context) {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isReady = false
    private val scope = CoroutineScope(Dispatchers.Main)
    private var fallbackJob: Job? = null

    private var friendAVoice: Voice? = null
    private var friendBVoice: Voice? = null
    private var passerbyVoice: Voice? = null

    private val _subtitleState = MutableStateFlow(SpokenSubtitleState())
    val subtitleState: StateFlow<SpokenSubtitleState> = _subtitleState.asStateFlow()

    private var activeQueue: List<Pair<Int, DialogueLine>> = emptyList()
    private var queueIndex = 0

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val langResult = tts?.setLanguage(Locale.US)
                isReady = langResult != TextToSpeech.LANG_MISSING_DATA &&
                    langResult != TextToSpeech.LANG_NOT_SUPPORTED
                selectDistinctMaleVoices()
                setupListener()
                Log.d(TAG, "TTS initialized. ready=$isReady")
            } else {
                Log.e(TAG, "TTS initialization failed with status=$status")
            }
        }
    }

    private fun selectDistinctMaleVoices() {
        try {
            val allVoices = tts?.voices.orEmpty()
            val enUsVoices = allVoices.filter {
                it.locale.language == "en" && it.locale.country == "US" && !it.isNetworkConnectionRequired
            }.ifEmpty {
                allVoices.filter { it.locale.language == "en" }
            }

            val knownMaleHints = listOf("-iom-", "-iol-", "-tpd-", "-tpc-", "-iom", "-iol", "male", "man", "guy")
            val knownFemaleHints = listOf("-sfg-", "-tpf-", "-iob-", "-x-", "female", "woman", "girl")

            val maleVoices = enUsVoices.filter { v ->
                val lower = v.name.lowercase(Locale.US)
                knownMaleHints.any { lower.contains(it) } ||
                    (v.features?.any { it.contains("male", ignoreCase = true) } == true &&
                        knownFemaleHints.none { lower.contains(it) })
            }.ifEmpty {
                enUsVoices.filter { v ->
                    val lower = v.name.lowercase(Locale.US)
                    knownFemaleHints.none { lower.contains(it) }
                }
            }.ifEmpty { enUsVoices }

            friendAVoice = maleVoices.getOrNull(0)
            friendBVoice = maleVoices.getOrNull(1) ?: maleVoices.getOrNull(0)
            passerbyVoice = maleVoices.getOrNull(2) ?: maleVoices.lastOrNull()
        } catch (e: Exception) {
            Log.w(TAG, "Could not enumerate male TTS voices, using pitch-tuned default", e)
        }
    }

    private fun performanceFor(beatId: Int, speaker: String): VoicePerformance {
        return when {
            speaker.contains("Friend A", ignoreCase = true) -> when (beatId) {
                1 -> VoicePerformance(friendAVoice, pitch = 0.94f, rate = 0.96f, emotionLabel = "Cheerful & Relaxed")
                2 -> VoicePerformance(friendAVoice, pitch = 0.78f, rate = 0.84f, emotionLabel = "Disgusted & Bummed")
                else -> VoicePerformance(friendAVoice, pitch = 1.06f, rate = 1.18f, emotionLabel = "Angrily Shouting")
            }
            speaker.contains("Friend B", ignoreCase = true) -> when (beatId) {
                1 -> VoicePerformance(friendBVoice, pitch = 1.02f, rate = 1.00f, emotionLabel = "Cheerful")
                2 -> VoicePerformance(friendBVoice, pitch = 1.15f, rate = 1.12f, emotionLabel = "Laughing Hysterically")
                else -> VoicePerformance(friendBVoice, pitch = 1.10f, rate = 1.22f, emotionLabel = "Shocked & Furious")
            }
            else -> VoicePerformance(passerbyVoice, pitch = 0.65f, rate = 1.06f, emotionLabel = "Deep & Outraged")
        }
    }

    private fun setupListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                val idx = utteranceId?.removePrefix("line_")?.toIntOrNull() ?: return
                val pair = activeQueue.getOrNull(idx) ?: return
                val clean = pair.second.englishText.replace("**", "").trim()
                val korean = pair.second.koreanSubtitle.trim()
                val words = clean.split(Regex("\\s+")).filter { it.isNotBlank() }
                val perf = performanceFor(pair.first, pair.second.speaker)
                scope.launch {
                    _subtitleState.value = SpokenSubtitleState(
                        beatId = pair.first,
                        speaker = pair.second.speaker,
                        emotionLabel = perf.emotionLabel,
                        fullCleanText = clean,
                        koreanSubtitle = korean,
                        spokenWordCount = 1.coerceAtMost(words.size),
                        totalWords = words.size
                    )
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        startWordTimer(pair.first, pair.second.speaker, perf.emotionLabel, clean, korean, words.size)
                    }
                }
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                val idx = utteranceId?.removePrefix("line_")?.toIntOrNull() ?: return
                val pair = activeQueue.getOrNull(idx) ?: return
                val clean = pair.second.englishText.replace("**", "").trim()
                val korean = pair.second.koreanSubtitle.trim()
                val prefix = clean.take(end.coerceAtMost(clean.length))
                val wordCount = prefix.trim().split(Regex("\\s+")).count { it.isNotBlank() }
                val total = clean.split(Regex("\\s+")).count { it.isNotBlank() }
                val perf = performanceFor(pair.first, pair.second.speaker)
                scope.launch {
                    _subtitleState.value = SpokenSubtitleState(
                        beatId = pair.first,
                        speaker = pair.second.speaker,
                        emotionLabel = perf.emotionLabel,
                        fullCleanText = clean,
                        koreanSubtitle = korean,
                        spokenWordCount = wordCount.coerceIn(1, total.coerceAtLeast(1)),
                        totalWords = total
                    )
                }
            }

            override fun onDone(utteranceId: String?) {
                fallbackJob?.cancel()
                val idx = utteranceId?.removePrefix("line_")?.toIntOrNull() ?: return
                scope.launch {
                    if (idx == queueIndex && queueIndex + 1 < activeQueue.size) {
                        queueIndex++
                        speakCurrentQueueItem()
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                fallbackJob?.cancel()
            }
        })
    }

    fun speakBeat(beatId: Int, lines: List<DialogueLine>) {
        if (lines.isEmpty()) return
        activeQueue = lines.map { beatId to it }
        queueIndex = 0
        fallbackJob?.cancel()

        val firstLine = lines.first()
        val firstClean = firstLine.englishText.replace("**", "").trim()
        val firstKorean = firstLine.koreanSubtitle.trim()
        val firstWords = firstClean.split(Regex("\\s+")).filter { it.isNotBlank() }
        val firstPerf = performanceFor(beatId, firstLine.speaker)
        _subtitleState.value = SpokenSubtitleState(
            beatId = beatId,
            speaker = firstLine.speaker,
            emotionLabel = firstPerf.emotionLabel,
            fullCleanText = firstClean,
            koreanSubtitle = firstKorean,
            spokenWordCount = 1.coerceAtMost(firstWords.size),
            totalWords = firstWords.size
        )

        if (isReady) {
            speakCurrentQueueItem()
        } else {
            startFallbackSequence(beatId, lines)
        }
    }

    private fun speakCurrentQueueItem() {
        val pair = activeQueue.getOrNull(queueIndex) ?: return
        val beatId = pair.first
        val line = pair.second
        val clean = line.englishText.replace("**", "").trim()
        val korean = line.koreanSubtitle.trim()
        val perf = performanceFor(beatId, line.speaker)

        perf.voice?.let { tts?.voice = it }
        tts?.setPitch(perf.pitch)
        tts?.setSpeechRate(perf.rate)

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }
        tts?.speak(clean, TextToSpeech.QUEUE_FLUSH, params, "line_$queueIndex")

        val words = clean.split(Regex("\\s+")).count { it.isNotBlank() }
        startWordTimer(beatId, line.speaker, perf.emotionLabel, clean, korean, words)
    }

    private fun startWordTimer(
        beatId: Int,
        speaker: String,
        emotionLabel: String,
        clean: String,
        korean: String,
        totalWords: Int
    ) {
        fallbackJob?.cancel()
        if (totalWords <= 0) return
        fallbackJob = scope.launch {
            for (w in 1..totalWords) {
                if (_subtitleState.value.beatId == beatId &&
                    _subtitleState.value.fullCleanText == clean &&
                    _subtitleState.value.spokenWordCount < w
                ) {
                    _subtitleState.value = SpokenSubtitleState(
                        beatId = beatId,
                        speaker = speaker,
                        emotionLabel = emotionLabel,
                        fullCleanText = clean,
                        koreanSubtitle = korean,
                        spokenWordCount = w,
                        totalWords = totalWords
                    )
                }
                delay(280L)
            }
        }
    }

    private fun startFallbackSequence(beatId: Int, lines: List<DialogueLine>) {
        fallbackJob?.cancel()
        fallbackJob = scope.launch {
            for (line in lines) {
                val clean = line.englishText.replace("**", "").trim()
                val korean = line.koreanSubtitle.trim()
                val words = clean.split(Regex("\\s+")).count { it.isNotBlank() }.coerceAtLeast(1)
                val perf = performanceFor(beatId, line.speaker)
                for (w in 1..words) {
                    _subtitleState.value = SpokenSubtitleState(
                        beatId = beatId,
                        speaker = line.speaker,
                        emotionLabel = perf.emotionLabel,
                        fullCleanText = clean,
                        koreanSubtitle = korean,
                        spokenWordCount = w,
                        totalWords = words
                    )
                    delay(320L)
                }
                delay(350L)
            }
        }
    }

    fun stop() {
        fallbackJob?.cancel()
        tts?.stop()
    }

    fun release() {
        fallbackJob?.cancel()
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
