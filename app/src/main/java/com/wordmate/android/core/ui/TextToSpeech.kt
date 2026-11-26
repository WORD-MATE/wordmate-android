package com.wordmate.android.core.ui

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/** 단어 발음 재생용 TTS 래퍼. */
class WordSpeaker internal constructor(private val tts: TextToSpeech) {
    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
    }
}

@Composable
fun rememberWordSpeaker(): WordSpeaker {
    val context = LocalContext.current
    val holder = remember {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) engine.language = Locale.US
        }
        engine
    }
    DisposableEffect(holder) { onDispose { holder.shutdown() } }
    return remember(holder) { WordSpeaker(holder) }
}
