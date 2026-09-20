package com.pratik.aiassistant

import android.app.*
import android.content.*
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.*

class AssistantService : Service(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var recognizer: SpeechRecognizer? = null
    private var ready = false

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(77, notification("Pratik AI is ready"))
        tts = TextToSpeech(this, this)
        startListening()
    }

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            tts.setSpeechRate(0.94f)
            speak("Namaste sir. Pratik AI ready cha. Hajur j bhannu huncha, ma bujhne kosis garchu.")
        }
    }

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            speak("Sir, yo phone ma speech recognition available chaina.")
            return
        }

        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull().orEmpty()
                if (text.isNotBlank()) CommandEngine(this@AssistantService).handle(text)
                Handler(Looper.getMainLooper()).postDelayed({ startListening() }, 500)
            }
            override fun onError(error: Int) {
                Handler(Looper.getMainLooper()).postDelayed({ startListening() }, 900)
            }
            override fun onReadyForSpeech(p0: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(p0: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        recognizer?.startListening(intent)
    }

    fun speak(text: String) {
        if (!ready) return
        val locale = LanguageDetector.detect(text)
        tts.language = locale
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PRATIK_AI")
    }

    override fun onDestroy() {
        recognizer?.destroy()
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    private fun createChannel() {
        val channel = NotificationChannel(
            "pratik_ai", "Pratik AI", NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun notification(text: String): Notification {
        return Notification.Builder(this, "pratik_ai")
            .setContentTitle("Pratik AI Assistant")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }
}

object LanguageDetector {
    fun detect(text: String): Locale {
        val devanagari = text.any { it in '\u0900'..'\u097F' }
        return when {
            devanagari -> Locale("ne", "NP")
            text.matches(Regex(".*\\b(ho|ma|timilai|malai|gara|kaha|kina|sir)\\b.*", RegexOption.IGNORE_CASE))
                -> Locale("ne", "NP")
            else -> Locale.US
        }
    }
}
