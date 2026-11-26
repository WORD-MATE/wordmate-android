package com.wordmate.android.feature.study

import androidx.lifecycle.ViewModel
import com.wordmate.android.data.model.QuizDirection
import com.wordmate.android.data.model.StudyAnswer
import com.wordmate.android.data.model.StudyMode
import com.wordmate.android.data.model.StudyOptions
import com.wordmate.android.data.model.StudyResult
import com.wordmate.android.data.model.Word
import com.wordmate.android.data.repository.WordMateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StudyQuestion(
    val word: Word,
    /** 화면에 크게 보여줄 문제 (영어 또는 뜻). */
    val prompt: String,
    /** 정답 텍스트. */
    val answer: String,
    /** 4지선다 보기. 다른 모드에서는 비어 있다. */
    val choices: List<String>,
)

sealed interface AnswerState {
    data object Waiting : AnswerState
    data class Answered(val correct: Boolean, val selectedIndex: Int? = null, val typed: String? = null) : AnswerState
}

data class StudyUiState(
    val packTitle: String,
    val mode: StudyMode,
    val direction: QuizDirection,
    val questions: List<StudyQuestion>,
    val index: Int = 0,
    val answerState: AnswerState = AnswerState.Waiting,
    val flipped: Boolean = false,
    val input: String = "",
    val answers: List<StudyAnswer> = emptyList(),
    val finished: Boolean = false,
) {
    val current: StudyQuestion? get() = questions.getOrNull(index)
    val progress: Float get() = if (questions.isEmpty()) 0f else (index + 1f) / questions.size
}

class StudyViewModel(
    private val repository: WordMateRepository,
    private val packId: Long,
    options: StudyOptions,
    onlyWrong: Boolean,
) : ViewModel() {

    private val _state: MutableStateFlow<StudyUiState>
    val state: StateFlow<StudyUiState>

    init {
        val pack = requireNotNull(repository.pack(packId))
        val wrongIds = repository.lastStudyResult?.answers?.filterNot { it.correct }?.map { it.word.id }?.toSet()
        val source = when {
            onlyWrong && wrongIds != null -> pack.words.filter { it.id in wrongIds }
            else -> pack.words
        }.let { if (options.shuffle) it.shuffled() else it }
            .take(options.questionCount)
        val pool = pack.words.ifEmpty { source }
        val questions = source.map { word -> buildQuestion(word, pool, options) }
        _state = MutableStateFlow(StudyUiState(pack.title, options.mode, options.direction, questions))
        state = _state.asStateFlow()
    }

    private fun buildQuestion(word: Word, pool: List<Word>, options: StudyOptions): StudyQuestion {
        val enToKo = options.direction == QuizDirection.EnToKo
        val prompt = if (enToKo) word.term else word.meaning
        val answer = if (enToKo) word.meaning else word.term
        val choices = if (options.mode == StudyMode.MultipleChoice) {
            val distractors = pool.filterNot { it.id == word.id }
                .map { if (enToKo) it.meaning else it.term }
                .distinct()
                .filterNot { it == answer }
                .shuffled()
                .take(3)
            (distractors + answer).shuffled()
        } else {
            emptyList()
        }
        return StudyQuestion(word, prompt, answer, choices)
    }

    fun flip() = _state.update { it.copy(flipped = !it.flipped) }

    /** 플래시카드: 알아요 / 몰라요. */
    fun markKnown(known: Boolean) {
        val q = _state.value.current ?: return
        _state.update { it.copy(answers = it.answers + StudyAnswer(q.word, known)) }
        next()
    }

    fun select(index: Int) {
        val s = _state.value
        val q = s.current ?: return
        if (s.answerState != AnswerState.Waiting) return
        val correct = q.choices[index] == q.answer
        _state.update {
            it.copy(
                answerState = AnswerState.Answered(correct, selectedIndex = index),
                answers = it.answers + StudyAnswer(q.word, correct),
            )
        }
    }

    fun onInputChange(value: String) = _state.update { it.copy(input = value) }

    fun submitTyped() {
        val s = _state.value
        val q = s.current ?: return
        if (s.answerState != AnswerState.Waiting || s.input.isBlank()) return
        val correct = isTypedCorrect(s.input, q.answer)
        _state.update {
            it.copy(
                answerState = AnswerState.Answered(correct, typed = s.input),
                answers = it.answers + StudyAnswer(q.word, correct),
            )
        }
    }

    fun next() {
        val s = _state.value
        if (s.index + 1 >= s.questions.size) {
            finish()
        } else {
            _state.update {
                it.copy(index = it.index + 1, answerState = AnswerState.Waiting, flipped = false, input = "")
            }
        }
    }

    private fun finish() {
        val s = _state.value
        val perCorrect = when (s.mode) {
            StudyMode.Flashcard -> 1
            StudyMode.MultipleChoice -> 2
            StudyMode.Typing -> 3
        }
        val result = StudyResult(
            packTitle = s.packTitle,
            mode = s.mode,
            answers = s.answers,
            earnedXp = s.answers.count { it.correct } * perCorrect,
        )
        repository.recordStudy(packId, result)
        _state.update { it.copy(finished = true) }
    }

    companion object {
        private fun normalize(text: String) = text.lowercase().replace(Regex("[\\s.,·~!?()]"), "")

        /** 쉼표로 구분된 뜻 중 하나와 일치하면 정답. */
        fun isTypedCorrect(input: String, answer: String): Boolean {
            val typed = normalize(input)
            if (typed.isEmpty()) return false
            return answer.split(',', '/', ';').map(::normalize).any { it == typed } || normalize(answer) == typed
        }
    }
}
