package com.wordmate.android

import android.app.Application
import com.wordmate.android.data.repository.InMemoryWordMateRepository
import com.wordmate.android.data.repository.WordMateRepository

class WordMateApplication : Application() {
    /** 간단한 수동 DI 컨테이너. */
    val repository: WordMateRepository by lazy { InMemoryWordMateRepository() }
}
