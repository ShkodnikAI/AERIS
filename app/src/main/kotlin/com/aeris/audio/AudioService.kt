package com.aeris.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioService @Inject constructor(
    private val context: Context
) {
    private var audioTrack: AudioTrack? = null
    private var tts: TextToSpeech? = null
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Бинауральные биения: baseFreq + beatFreq
     * α=8Hz (расслабление), θ=6Hz (медитация), δ=4Hz (сон)
     */
    fun playBinaural(baseFreq: Double = 432.0, beatFreq: Double = 8.0) {
        stop()
        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        job = scope.launch {
            val buffer = ShortArray(1024)
            var phaseL = 0.0
            var phaseR = 0.0
            while (isActive) {
                for (i in buffer.indices step 2) {
                    phaseL += 2 * Math.PI * baseFreq / sampleRate
                    phaseR += 2 * Math.PI * (baseFreq + beatFreq) / sampleRate
                    buffer[i] = (Math.sin(phaseL) * 32767 * 0.25).toInt().toShort()
                    buffer[i + 1] = (Math.sin(phaseR) * 32767 * 0.25).toInt().toShort()
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
    }

    /**
     * TTS аффирмации для направления дыхания
     */
    fun speakAffirmation(text: String) {
        stopTts()
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru")
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    /**
     * Энергетические звуки (Tibetan bowls simulation)
     */
    fun playEnergetic(baseFreq: Double = 220.0) {
        stop()
        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        job = scope.launch {
            val buffer = ShortArray(1024)
            var phase = 0.0
            while (isActive) {
                for (i in buffer.indices step 2) {
                    phase += 2 * Math.PI * baseFreq / sampleRate
                    val envelope = 0.5 + 0.5 * Math.sin(phase * 0.1)
                    val sample = (Math.sin(phase) * 32767 * 0.2 * envelope).toInt().toShort()
                    buffer[i] = sample
                    buffer[i + 1] = sample
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
    }

    /**
     * Медитативные дроны
     */
    fun playMeditationDrone(baseFreq: Double = 110.0) {
        stop()
        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        job = scope.launch {
            val buffer = ShortArray(1024)
            var phase1 = 0.0
            var phase2 = 0.0
            while (isActive) {
                for (i in buffer.indices step 2) {
                    phase1 += 2 * Math.PI * baseFreq / sampleRate
                    phase2 += 2 * Math.PI * (baseFreq * 1.5) / sampleRate
                    val sample = (
                        (Math.sin(phase1) + Math.sin(phase2) * 0.5) * 32767 * 0.15
                    ).toInt().toShort()
                    buffer[i] = sample
                    buffer[i + 1] = sample
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        stopTts()
    }

    private fun stopTts() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    fun release() {
        stop()
        scope.cancel()
    }
}
