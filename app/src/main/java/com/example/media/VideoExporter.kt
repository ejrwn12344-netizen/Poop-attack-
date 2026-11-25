package com.example.media

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import com.example.model.StoryBeat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "VideoExporter"

object VideoExporter {

    private const val WIDTH = 1280
    private const val HEIGHT = 720
    private const val FPS = 15
    private const val TOTAL_DURATION_SEC = 30
    private const val BIT_RATE = 2_500_000

    suspend fun exportMp4(
        context: Context,
        beats: List<StoryBeat>,
        subtitlesEnabled: Boolean,
        onProgress: (Float) -> Unit
    ): Pair<String, String> = withContext(Dispatchers.Default) {
        val fileName = "PoopAttack_Movie_${System.currentTimeMillis() / 1000}.mp4"
        val tempFile = File(context.cacheDir, fileName)
        if (tempFile.exists()) tempFile.delete()

        val bitmaps = beats.associate { beat ->
            val raw = BitmapFactory.decodeResource(context.resources, beat.imageRes)
            val scaled = Bitmap.createScaledBitmap(raw, WIDTH + 80, HEIGHT + 45, true)
            if (raw != scaled) raw.recycle()
            beat.id to scaled
        }

        try {
            encodeVideoFile(tempFile, beats, bitmaps, subtitlesEnabled, onProgress)
            val locationHint = saveToPublicStorage(context, tempFile, fileName)
            fileName to locationHint
        } finally {
            bitmaps.values.forEach { if (!it.isRecycled) it.recycle() }
            if (tempFile.exists()) tempFile.delete()
        }
    }

    private fun encodeVideoFile(
        outputFile: File,
        beats: List<StoryBeat>,
        bitmaps: Map<Int, Bitmap>,
        subtitlesEnabled: Boolean,
        onProgress: (Float) -> Unit
    ) {
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, WIDTH, HEIGHT).apply {
            setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )
            setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
            setInteger(MediaFormat.KEY_FRAME_RATE, FPS)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2)
        }

        val codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val inputSurface = codec.createInputSurface()
        codec.start()

        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var trackIndex = -1
        var muxerStarted = false
        val bufferInfo = MediaCodec.BufferInfo()

        val totalFrames = TOTAL_DURATION_SEC * FPS
        val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(6f, 0f, 3f, Color.BLACK)
        }
        val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(175, 20, 26, 26)
        }
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(210, 33, 122, 116)
        }
        val badgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        fun drainEncoder(endOfStream: Boolean) {
            if (endOfStream) {
                codec.signalEndOfInputStream()
            }
            while (true) {
                val outIndex = codec.dequeueOutputBuffer(bufferInfo, 10_000)
                when {
                    outIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                        if (!endOfStream) break
                    }
                    outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        if (!muxerStarted) {
                            trackIndex = muxer.addTrack(codec.outputFormat)
                            muxer.start()
                            muxerStarted = true
                        }
                    }
                    outIndex >= 0 -> {
                        val encodedData = codec.getOutputBuffer(outIndex)
                        if (encodedData != null && bufferInfo.size > 0 && muxerStarted) {
                            if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0) {
                                encodedData.position(bufferInfo.offset)
                                encodedData.limit(bufferInfo.offset + bufferInfo.size)
                                muxer.writeSampleData(trackIndex, encodedData, bufferInfo)
                            }
                        }
                        codec.releaseOutputBuffer(outIndex, false)
                        if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            break
                        }
                    }
                }
            }
        }

        try {
            for (frame in 0 until totalFrames) {
                val timeSec = frame.toFloat() / FPS
                val activeBeat = beats.firstOrNull { timeSec >= it.startSec && timeSec < it.endSec }
                    ?: beats.last()
                val beatProgress = ((timeSec - activeBeat.startSec) /
                    (activeBeat.endSec - activeBeat.startSec).coerceAtLeast(1f)).coerceIn(0f, 1f)

                val canvas: Canvas = inputSurface.lockCanvas(null)
                try {
                    canvas.drawColor(Color.BLACK)
                    val bmp = bitmaps[activeBeat.id]
                    if (bmp != null) {
                        val offsetX = (-40f * beatProgress).toInt()
                        val offsetY = (-20f * beatProgress).toInt()
                        val srcRect = Rect(0, 0, bmp.width, bmp.height)
                        val dstRect = Rect(offsetX, offsetY, WIDTH + 80 + offsetX, HEIGHT + 45 + offsetY)
                        canvas.drawBitmap(bmp, srcRect, dstRect, null)
                    }

                    // Top-left Beat badge
                    val badgeText = "${activeBeat.title} (${activeBeat.timeRange})"
                    val textWidth = badgeTextPaint.measureText(badgeText)
                    val badgeRect = RectF(32f, 28f, 64f + textWidth, 74f)
                    canvas.drawRoundRect(badgeRect, 20f, 20f, badgePaint)
                    canvas.drawText(badgeText, 48f, 60f, badgeTextPaint)

                    if (subtitlesEnabled && activeBeat.lines.isNotEmpty()) {
                        val lineIdx = if (activeBeat.lines.size > 1 && beatProgress >= 0.5f) 1 else 0
                        val cleanSubtitle = activeBeat.lines[lineIdx].englishText.replace("**", "").trim()

                        val maxSubWidth = WIDTH - 140
                        val layout = StaticLayout.Builder
                            .obtain(cleanSubtitle, 0, cleanSubtitle.length, subtitlePaint, maxSubWidth)
                            .setAlignment(Layout.Alignment.ALIGN_CENTER)
                            .setMaxLines(2)
                            .build()

                        val boxHeight = layout.height + 28f
                        val boxTop = HEIGHT - boxHeight - 32f
                        val bgRect = RectF(56f, boxTop, WIDTH - 56f, HEIGHT - 32f)
                        canvas.drawRoundRect(bgRect, 18f, 18f, boxPaint)

                        canvas.save()
                        canvas.translate(70f, boxTop + 14f)
                        layout.draw(canvas)
                        canvas.restore()
                    }
                } finally {
                    inputSurface.unlockCanvasAndPost(canvas)
                }

                drainEncoder(false)
                if (frame % 5 == 0) {
                    onProgress((frame.toFloat() / totalFrames).coerceIn(0f, 0.98f))
                }
            }
            drainEncoder(true)
            onProgress(1f)
        } finally {
            try {
                codec.stop()
            } catch (e: Exception) {
                Log.w(TAG, "Codec stop warning", e)
            }
            codec.release()
            inputSurface.release()
            try {
                if (muxerStarted) muxer.stop()
            } catch (e: Exception) {
                Log.w(TAG, "Muxer stop warning", e)
            }
            muxer.release()
        }
    }

    private fun saveToPublicStorage(
        context: Context,
        sourceFile: File,
        fileName: String
    ): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/Poop Attack")
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Could not create MediaStore video entry.")
            resolver.openOutputStream(uri)?.use { out ->
                sourceFile.inputStream().use { input -> input.copyTo(out) }
            } ?: throw IllegalStateException("Could not open video output stream.")
            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            "Movies/Poop Attack/$fileName"
        } else {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: context.filesDir
            if (!dir.exists()) dir.mkdirs()
            val dest = File(dir, fileName)
            sourceFile.copyTo(dest, overwrite = true)
            dest.absolutePath
        }
    }
}
