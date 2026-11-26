package com.wordmate.android

import android.speech.tts.TextToSpeech
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.util.Locale

class MainActivity : FlutterActivity() {
    private var tts: TextToSpeech? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        tts = TextToSpeech(applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US
        }
        // 단어 발음 재생: Flutter 쪽 lib/utils/speaker.dart 와 짝을 이룬다.
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "wordmate/tts").setMethodCallHandler { call, result ->
            when (call.method) {
                "speak" -> {
                    val text = call.argument<String>("text").orEmpty()
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }

    override fun onDestroy() {
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
