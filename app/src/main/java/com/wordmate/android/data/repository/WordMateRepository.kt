package com.wordmate.android.data.repository

import com.wordmate.android.data.model.AppNotification
import com.wordmate.android.data.model.Badge
import com.wordmate.android.data.model.BattlePlayer
import com.wordmate.android.data.model.BattleQuestion
import com.wordmate.android.data.model.BattleRecord
import com.wordmate.android.data.model.BattleResult
import com.wordmate.android.data.model.BattleRoom
import com.wordmate.android.data.model.BattleSettings
import com.wordmate.android.data.model.BattleStanding
import com.wordmate.android.data.model.DailyMission
import com.wordmate.android.data.model.DailySentence
import com.wordmate.android.data.model.PackCategory
import com.wordmate.android.data.model.RankingEntry
import com.wordmate.android.data.model.RankingPeriod
import com.wordmate.android.data.model.RankingScope
import com.wordmate.android.data.model.StudyResult
import com.wordmate.android.data.model.User
import com.wordmate.android.data.model.Word
import com.wordmate.android.data.model.WordPack
import com.wordmate.android.data.model.WordStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

/**
 * 앱 전역 데이터 소스. 현재는 메모리 기반이며, 서버 API가 준비되면
 * 같은 인터페이스를 유지한 채 원격 구현으로 교체한다.
 */
interface WordMateRepository {
    val isLoggedIn: StateFlow<Boolean>
    val user: StateFlow<User>
    val notifications: StateFlow<List<AppNotification>>
    val packs: StateFlow<List<WordPack>>
    val missions: StateFlow<List<DailyMission>>
    val dailySentence: DailySentence
    val battleRecords: StateFlow<List<BattleRecord>>
    val badges: List<Badge>

    fun login()
    fun logout()

    fun markNotificationRead(id: Long)
    fun markAllNotificationsRead()

    fun pack(id: Long): WordPack?
    fun toggleStar(packId: Long, wordId: Long)
    fun createPack(title: String, description: String): WordPack
    fun deletePack(packId: Long)
    fun addWord(packId: Long, term: String, meaning: String)
    fun updateWord(packId: Long, wordId: Long, term: String, meaning: String)
    fun deleteWord(packId: Long, wordId: Long)

    fun claimMission(id: Long)

    fun recordStudy(packId: Long, result: StudyResult)
    var lastStudyResult: StudyResult?
    var lastStudyPackId: Long?

    fun ranking(scope: RankingScope, period: RankingPeriod): List<RankingEntry>

    fun createRoom(settings: BattleSettings): BattleRoom
    fun joinRoom(code: String): BattleRoom?
    var currentRoom: BattleRoom?
    fun battleQuestions(room: BattleRoom): List<BattleQuestion>
    fun finishBattle(room: BattleRoom, myScore: Int, correct: Int, wrongWords: List<Word>): BattleResult
    var lastBattleResult: BattleResult?
}

class InMemoryWordMateRepository : WordMateRepository {

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _user = MutableStateFlow(SampleData.user)
    override val user: StateFlow<User> = _user.asStateFlow()

    private val _notifications = MutableStateFlow(SampleData.notifications)
    override val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _packs = MutableStateFlow(SampleData.packs)
    override val packs: StateFlow<List<WordPack>> = _packs.asStateFlow()

    private val _missions = MutableStateFlow(SampleData.missions)
    override val missions: StateFlow<List<DailyMission>> = _missions.asStateFlow()

    override val dailySentence: DailySentence = SampleData.dailySentence

    private val _battleRecords = MutableStateFlow(SampleData.battleRecords)
    override val battleRecords: StateFlow<List<BattleRecord>> = _battleRecords.asStateFlow()

    override val badges: List<Badge> = SampleData.badges

    override var lastStudyResult: StudyResult? = null
    override var lastStudyPackId: Long? = null
    override var currentRoom: BattleRoom? = null
    override var lastBattleResult: BattleResult? = null

    override fun login() {
        _isLoggedIn.value = true
    }

    override fun logout() {
        _isLoggedIn.value = false
    }

    override fun markNotificationRead(id: Long) = _notifications.update { list ->
        list.map { if (it.id == id) it.copy(read = true) else it }
    }

    override fun markAllNotificationsRead() = _notifications.update { list -> list.map { it.copy(read = true) } }

    override fun pack(id: Long): WordPack? = _packs.value.firstOrNull { it.id == id }

    private fun updatePack(packId: Long, transform: (WordPack) -> WordPack) = _packs.update { list ->
        list.map { if (it.id == packId) transform(it) else it }
    }

    private fun nextWordId(): Long = (_packs.value.flatMap { it.words }.maxOfOrNull { it.id } ?: 0L) + 1

    override fun toggleStar(packId: Long, wordId: Long) = updatePack(packId) { pack ->
        pack.copy(words = pack.words.map { if (it.id == wordId) it.copy(starred = !it.starred) else it })
    }

    override fun createPack(title: String, description: String): WordPack {
        val pack = WordPack(
            id = (_packs.value.maxOfOrNull { it.id } ?: 0L) + 1,
            title = title,
            description = description.ifBlank { "내 단어팩" },
            category = PackCategory.All,
            words = emptyList(),
            isMine = true,
        )
        _packs.update { it + pack }
        return pack
    }

    override fun deletePack(packId: Long) = _packs.update { list -> list.filterNot { it.id == packId } }

    override fun addWord(packId: Long, term: String, meaning: String) {
        val id = nextWordId()
        updatePack(packId) { it.copy(words = it.words + Word(id, term.trim(), meaning.trim())) }
    }

    override fun updateWord(packId: Long, wordId: Long, term: String, meaning: String) = updatePack(packId) { pack ->
        pack.copy(words = pack.words.map { if (it.id == wordId) it.copy(term = term.trim(), meaning = meaning.trim()) else it })
    }

    override fun deleteWord(packId: Long, wordId: Long) = updatePack(packId) { pack ->
        pack.copy(words = pack.words.filterNot { it.id == wordId })
    }

    override fun claimMission(id: Long) {
        val mission = _missions.value.firstOrNull { it.id == id } ?: return
        if (!mission.isDone || mission.claimed) return
        _missions.update { list -> list.map { if (it.id == id) it.copy(claimed = true) else it } }
        addXp(mission.rewardXp)
    }

    private fun addXp(amount: Int) = _user.update { it.copy(xp = it.xp + amount, todayXp = it.todayXp + amount) }

    override fun recordStudy(packId: Long, result: StudyResult) {
        val correctIds = result.answers.filter { it.correct }.map { it.word.id }.toSet()
        val wrongIds = result.answers.filterNot { it.correct }.map { it.word.id }.toSet()
        updatePack(packId) { pack ->
            pack.copy(
                words = pack.words.map {
                    when (it.id) {
                        in correctIds -> it.copy(status = WordStatus.Memorized)
                        in wrongIds -> it.copy(status = WordStatus.Learning)
                        else -> it
                    }
                },
            )
        }
        addXp(result.earnedXp)
        _missions.update { list ->
            list.map { if (it.id == 2L) it.copy(current = (it.current + result.answers.size).coerceAtMost(it.goal)) else it }
        }
        lastStudyResult = result
        lastStudyPackId = packId
    }

    override fun ranking(scope: RankingScope, period: RankingPeriod): List<RankingEntry> {
        val me = _user.value
        return SampleData.ranking(scope.ordinal * 3 + period.ordinal)
            .map { if (it.isMe) it.copy(name = me.name, xp = me.xp) else it }
            .sortedByDescending { it.xp }
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
    }

    override fun createRoom(settings: BattleSettings): BattleRoom {
        val code = buildString { repeat(6) { append(CODE_CHARS[Random.nextInt(CODE_CHARS.length)]) } }
        val me = _user.value
        val room = BattleRoom(
            code = code,
            settings = settings,
            players = listOf(
                BattlePlayer(me.name, "2학년 1반", isHost = true, isMe = true),
                BattlePlayer("양지우", "2학년 1반"),
            ),
        )
        currentRoom = room
        return room
    }

    override fun joinRoom(code: String): BattleRoom? {
        if (code.length != 6) return null
        val pack = _packs.value.first()
        val me = _user.value
        val room = BattleRoom(
            code = code.uppercase(),
            settings = BattleSettings(pack),
            players = listOf(
                BattlePlayer("양지우", "2학년 1반", isHost = true),
                BattlePlayer("김도윤", "2학년 3반"),
                BattlePlayer(me.name, "2학년 1반", isMe = true),
            ),
        )
        currentRoom = room
        return room
    }

    override fun battleQuestions(room: BattleRoom): List<BattleQuestion> {
        val pool = room.settings.pack.words.ifEmpty { _packs.value.first().words }
        val allTerms = _packs.value.flatMap { it.words }.map { it.term }.distinct()
        return pool.shuffled().take(room.settings.questionCount).map { word ->
            val distractors = (pool.map { it.term } + allTerms).distinct().filterNot { it == word.term }.shuffled().take(3)
            val choices = (distractors + word.term).shuffled()
            BattleQuestion(word.meaning, choices, choices.indexOf(word.term), word)
        }
    }

    override fun finishBattle(room: BattleRoom, myScore: Int, correct: Int, wrongWords: List<Word>): BattleResult {
        val me = _user.value
        val others = room.players.filterNot { it.isMe }.map { p ->
            p.name to (myScore + Random.nextInt(-400, 250)).coerceAtLeast(0)
        }
        val ranked = (others + (me.name to myScore)).sortedByDescending { it.second }
        val standings = ranked.mapIndexed { i, (name, score) ->
            BattleStanding(
                rank = i + 1,
                name = name,
                detail = if (name == me.name) "정답 ${correct}개" else "정답 ${(score / 140).coerceAtMost(room.settings.questionCount)}개",
                score = score,
                isMe = name == me.name,
            )
        }
        val myRank = standings.first { it.isMe }.rank
        val earnedXp = when (myRank) { 1 -> 30; 2 -> 20; 3 -> 15; else -> 10 }
        addXp(earnedXp)
        _missions.update { list -> list.map { if (it.id == 1L) it.copy(current = it.goal) else it } }
        val result = BattleResult(room.settings.pack.title, myRank, myScore, correct, earnedXp, standings, wrongWords)
        _battleRecords.update {
            listOf(
                BattleRecord(
                    id = (it.maxOfOrNull { r -> r.id } ?: 0L) + 1,
                    packTitle = room.settings.pack.title,
                    dateLabel = "방금 · ${room.players.size}명 참여",
                    playerCount = room.players.size,
                    rank = myRank,
                    score = myScore,
                ),
            ) + it
        }
        if (myRank == 1) _user.update { it.copy(battleWins = it.battleWins + 1) }
        lastBattleResult = result
        return result
    }

    private companion object {
        const val CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    }
}
