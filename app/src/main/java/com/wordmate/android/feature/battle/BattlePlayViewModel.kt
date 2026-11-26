package com.wordmate.android.feature.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordmate.android.data.model.BattleMode
import com.wordmate.android.data.model.BattleQuestion
import com.wordmate.android.data.model.BattleRoom
import com.wordmate.android.data.model.Word
import com.wordmate.android.data.repository.WordMateRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class BattlePlayState(
    val room: BattleRoom,
    val questions: List<BattleQuestion>,
    val index: Int = 0,
    val remainingMs: Long,
    val selectedIndex: Int? = null,
    val revealed: Boolean = false,
    val score: Int = 0,
    val lastGain: Int = 0,
    val correctCount: Int = 0,
    val wrongWords: List<Word> = emptyList(),
    val reaction: String? = null,
    val finished: Boolean = false,
) {
    val question: BattleQuestion? get() = questions.getOrNull(index)
    val totalMs: Long get() = room.settings.secondsPerQuestion * 1_000L
    val timeFraction: Float get() = remainingMs.toFloat() / totalMs
}

class BattlePlayViewModel(private val repository: WordMateRepository) : ViewModel() {

    private val room = requireNotNull(repository.currentRoom)
    private val _state = MutableStateFlow(
        BattlePlayState(
            room = room,
            questions = repository.battleQuestions(room),
            remainingMs = room.settings.secondsPerQuestion * 1_000L,
        ),
    )
    val state: StateFlow<BattlePlayState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var reactionJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _state.value.remainingMs > 0) {
                delay(TICK_MS)
                _state.update { it.copy(remainingMs = (it.remainingMs - TICK_MS).coerceAtLeast(0)) }
            }
            if (!_state.value.revealed) reveal(selected = null)
        }
    }

    fun select(index: Int) {
        if (_state.value.revealed) return
        reveal(index)
    }

    private fun reveal(selected: Int?) {
        timerJob?.cancel()
        val s = _state.value
        val q = s.question ?: return
        val correct = selected == q.answerIndex
        val gain = when {
            !correct -> 0
            room.settings.mode == BattleMode.Accuracy -> 100
            else -> 100 + (50 * s.timeFraction).toInt()
        }
        _state.update {
            it.copy(
                selectedIndex = selected,
                revealed = true,
                score = it.score + gain,
                lastGain = gain,
                correctCount = it.correctCount + if (correct) 1 else 0,
                wrongWords = if (correct) it.wrongWords else it.wrongWords + q.word,
            )
        }
        viewModelScope.launch {
            delay(REVEAL_MS)
            next()
        }
    }

    private fun next() {
        val s = _state.value
        if (s.index + 1 >= s.questions.size) {
            repository.finishBattle(room, s.score, s.correctCount, s.wrongWords)
            _state.update { it.copy(finished = true) }
            return
        }
        _state.update {
            it.copy(index = it.index + 1, selectedIndex = null, revealed = false, lastGain = 0, remainingMs = it.totalMs)
        }
        startTimer()
    }

    fun react(emoji: String) {
        reactionJob?.cancel()
        _state.update { it.copy(reaction = emoji) }
        reactionJob = viewModelScope.launch {
            delay(1_200)
            _state.update { it.copy(reaction = null) }
        }
    }

    private companion object {
        const val TICK_MS = 100L
        const val REVEAL_MS = 1_200L
    }
}
