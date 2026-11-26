package com.wordmate.android.data.model

data class User(
    val name: String,
    val schoolInfo: String,
    val title: String,
    val level: Int,
    val xp: Int,
    val xpToNextLevel: Int,
    val todayXp: Int,
    val todayXpGoal: Int,
    val tier: String,
    val streakDays: Int,
    val totalCorrectWords: Int,
    val battleWins: Int,
)

enum class NotificationType { BattleInvite, DailySentence, FriendRequest }

data class AppNotification(
    val id: Long,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timeAgo: String,
    val read: Boolean,
)

enum class PackCategory(val label: String) {
    All("전체"), Suneung("수능"), Textbook("교과서"), It("IT"), Toeic("토익")
}

data class WordPack(
    val id: Long,
    val title: String,
    val description: String,
    val category: PackCategory,
    val words: List<Word>,
    val isMine: Boolean = false,
    val sourceLabel: String = if (isMine) "내 단어팩" else "기본 단어팩",
) {
    val wordCount: Int get() = words.size
    val learnedCount: Int get() = words.count { it.status == WordStatus.Memorized }
    val progress: Float get() = if (words.isEmpty()) 0f else learnedCount.toFloat() / words.size
    val isCompleted: Boolean get() = words.isNotEmpty() && learnedCount == words.size
}

enum class WordStatus(val label: String) { New("새 단어"), Learning("학습 중"), Memorized("외운 단어") }

data class Word(
    val id: Long,
    val term: String,
    val meaning: String,
    val pronunciation: String = "",
    val partOfSpeech: String = "",
    val example: String = "",
    val exampleMeaning: String = "",
    val status: WordStatus = WordStatus.New,
    val starred: Boolean = false,
)

enum class StudyMode(val title: String, val description: String, val xpHint: String) {
    Flashcard("플래시카드", "카드를 넘기며 뜻을 떠올려요. 가볍게 훑어보기 좋아요.", "XP +1 / 단어"),
    MultipleChoice("4지선다", "4개의 보기 중 알맞은 뜻을 골라요. 가장 기본적인 학습이에요.", "정답당 XP +2"),
    Typing("직접 입력", "단어를 보고 뜻을 직접 입력해요. 정확히 외웠는지 확인할 수 있어요.", "정답당 XP +3"),
}

enum class QuizDirection(val label: String) { EnToKo("영어 보고 뜻 맞히기"), KoToEn("뜻 보고 영어 맞히기") }

data class StudyOptions(
    val mode: StudyMode = StudyMode.MultipleChoice,
    val direction: QuizDirection = QuizDirection.EnToKo,
    val questionCount: Int = 10,
    val shuffle: Boolean = true,
)

data class StudyAnswer(val word: Word, val correct: Boolean)

data class StudyResult(
    val packTitle: String,
    val mode: StudyMode,
    val answers: List<StudyAnswer>,
    val earnedXp: Int,
) {
    val correctCount: Int get() = answers.count { it.correct }
    val wrongCount: Int get() = answers.size - correctCount
    val accuracy: Int get() = if (answers.isEmpty()) 0 else correctCount * 100 / answers.size
}

data class DailySentence(
    val dateLabel: String,
    val sentence: String,
    val underlined: List<String>,
    val translation: String,
)

data class DailyMission(
    val id: Long,
    val title: String,
    val current: Int,
    val goal: Int,
    val rewardXp: Int,
    val claimed: Boolean = false,
) {
    val isDone: Boolean get() = current >= goal
}

data class RankingEntry(
    val rank: Int,
    val name: String,
    val schoolInfo: String,
    val xp: Int,
    val delta: Int,
    val isMe: Boolean = false,
)

enum class RankingScope(val label: String) { School("학교 전체"), Grade("1학년"), Class("2학년 1반"), Friends("친구") }
enum class RankingPeriod(val label: String) { Weekly("주간"), Monthly("월간"), All("전체") }

enum class BattleMode(val label: String) { Speed("빠른 쪽이 점수 더 받기"), Accuracy("정확히 맞히면 점수") }

data class BattleSettings(
    val pack: WordPack,
    val mode: BattleMode = BattleMode.Speed,
    val questionCount: Int = 10,
    val secondsPerQuestion: Int = 10,
    val maxPlayers: Int = 8,
)

data class BattlePlayer(
    val name: String,
    val schoolInfo: String,
    val isHost: Boolean = false,
    val isMe: Boolean = false,
    val ready: Boolean = true,
)

data class BattleRoom(
    val code: String,
    val settings: BattleSettings,
    val players: List<BattlePlayer>,
)

data class BattleQuestion(val prompt: String, val choices: List<String>, val answerIndex: Int, val word: Word)

data class BattleStanding(val rank: Int, val name: String, val detail: String, val score: Int, val isMe: Boolean)

data class BattleResult(
    val packTitle: String,
    val myRank: Int,
    val myScore: Int,
    val correctCount: Int,
    val earnedXp: Int,
    val standings: List<BattleStanding>,
    val wrongWords: List<Word>,
)

data class BattleRecord(
    val id: Long,
    val packTitle: String,
    val dateLabel: String,
    val playerCount: Int,
    val rank: Int,
    val score: Int,
)

data class Badge(val title: String, val dateLabel: String, val medal: Medal, val isNew: Boolean = false)
enum class Medal { Gold, Silver, Bronze }
