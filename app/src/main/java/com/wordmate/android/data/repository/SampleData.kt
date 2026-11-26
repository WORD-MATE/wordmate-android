package com.wordmate.android.data.repository

import com.wordmate.android.data.model.AppNotification
import com.wordmate.android.data.model.Badge
import com.wordmate.android.data.model.BattleRecord
import com.wordmate.android.data.model.DailyMission
import com.wordmate.android.data.model.DailySentence
import com.wordmate.android.data.model.Medal
import com.wordmate.android.data.model.NotificationType
import com.wordmate.android.data.model.PackCategory
import com.wordmate.android.data.model.RankingEntry
import com.wordmate.android.data.model.User
import com.wordmate.android.data.model.Word
import com.wordmate.android.data.model.WordPack
import com.wordmate.android.data.model.WordStatus

/** 서버 연동 전까지 사용하는 샘플 데이터. Figma 시안의 문구를 그대로 사용한다. */
internal object SampleData {

    val user = User(
        name = "한국",
        schoolInfo = "2학년 1반 10번",
        title = "꾸준한 학습자",
        level = 12,
        xp = 412,
        xpToNextLevel = 500,
        todayXp = 86,
        todayXpGoal = 500,
        tier = "실버",
        streakDays = 7,
        totalCorrectWords = 23,
        battleWins = 1,
    )

    val notifications = listOf(
        AppNotification(1, NotificationType.BattleInvite, "대결 초대", "양지우님이 대결방에 초대했어요. 코드 K7Q2MX", "55분 전", false),
        AppNotification(2, NotificationType.DailySentence, "오늘의 문장", "Small habits, repeated every day...", "11시간 전", false),
        AppNotification(3, NotificationType.FriendRequest, "친구 요청", "육몰겸님이 친구 요청을 보냈어요.", "2시간 전", false),
    )

    private var nextWordId = 1L
    private fun w(term: String, meaning: String, pron: String = "", pos: String = "", ex: String = "", exKo: String = "", status: WordStatus = WordStatus.New, starred: Boolean = false) =
        Word(nextWordId++, term, meaning, pron, pos, ex, exKo, status, starred)

    private val suneungWords = listOf(
        w("abandon", "버리다, 포기하다", "/əˈbændən/", "동사", "They had to abandon the car.", "그들은 차를 버려야 했다.", WordStatus.Memorized),
        w("accurate", "정확한", "/ˈækjərət/", "형용사", "The report was accurate.", "보고서는 정확했다.", WordStatus.Learning),
        w("achieve", "성취하다, 이루다", "/əˈtʃiːv/", "동사", "She achieved her goal.", "그녀는 목표를 이루었다.", WordStatus.Memorized, starred = true),
        w("adapt", "적응하다", "/əˈdæpt/", "동사", "Animals adapt to their environment.", "동물은 환경에 적응한다.", WordStatus.Memorized),
        w("adequate", "충분한, 적절한", "/ˈædɪkwət/", "형용사", "We need adequate time.", "우리는 충분한 시간이 필요하다."),
        w("analyze", "분석하다", "/ˈænəlaɪz/", "동사", "Scientists analyze the data.", "과학자들은 데이터를 분석한다.", WordStatus.Learning),
        w("anticipate", "예상하다, 기대하다", "/ænˈtɪsɪpeɪt/", "동사", "We anticipate a large crowd tonight.", "오늘 밤 많은 인파를 예상한다."),
        w("attempt", "시도하다", "/əˈtempt/", "동사", "He attempted to climb the wall.", "그는 벽을 오르려고 시도했다.", WordStatus.Learning),
        w("consequence", "결과, 영향", "/ˈkɑːnsɪkwens/", "명사", "Every action has a consequence.", "모든 행동에는 결과가 따른다.", WordStatus.Memorized),
        w("decline", "감소하다, 거절하다", "/dɪˈklaɪn/", "동사", "Sales declined last year.", "작년에 판매량이 감소했다.", WordStatus.Memorized),
        w("distinguish", "구별하다", "/dɪˈstɪŋɡwɪʃ/", "동사", "Can you distinguish the twins?", "쌍둥이를 구별할 수 있니?", WordStatus.Memorized),
        w("flexible", "유연한", "/ˈfleksəbl/", "형용사", "Our schedule is flexible.", "우리 일정은 유연하다.", WordStatus.Memorized),
        w("benefit", "이익, 혜택", "/ˈbenɪfɪt/", "명사", "Exercise has many benefits.", "운동에는 많은 이점이 있다.", WordStatus.Memorized),
        w("conclude", "결론짓다", "/kənˈkluːd/", "동사", "They concluded the meeting.", "그들은 회의를 마무리했다.", WordStatus.Memorized),
        w("emphasize", "강조하다", "/ˈemfəsaɪz/", "동사", "She emphasized the point.", "그녀는 그 점을 강조했다."),
        w("essential", "필수적인", "/ɪˈsenʃl/", "형용사", "Water is essential for life.", "물은 생명에 필수적이다."),
        w("evaluate", "평가하다", "/ɪˈvæljueɪt/", "동사", "Teachers evaluate students.", "교사는 학생을 평가한다."),
        w("frequent", "빈번한", "/ˈfriːkwənt/", "형용사", "He is a frequent visitor.", "그는 자주 오는 방문객이다."),
        w("generate", "발생시키다", "/ˈdʒenəreɪt/", "동사", "Wind can generate power.", "바람은 전력을 생산할 수 있다."),
        w("habit", "습관", "/ˈhæbɪt/", "명사", "Reading is a good habit.", "독서는 좋은 습관이다.", WordStatus.Memorized),
        w("identify", "확인하다, 식별하다", "/aɪˈdentɪfaɪ/", "동사", "Can you identify him?", "그를 알아볼 수 있니?"),
        w("maintain", "유지하다", "/meɪnˈteɪn/", "동사", "Maintain a healthy diet.", "건강한 식단을 유지해라.", WordStatus.Memorized),
        w("obtain", "얻다", "/əbˈteɪn/", "동사", "She obtained a license.", "그녀는 면허를 취득했다."),
        w("shape", "형성하다, 모양", "/ʃeɪp/", "동사", "Habits shape who we are.", "습관은 우리를 만든다.", WordStatus.Memorized),
    )

    private fun simplePack(id: Long, title: String, desc: String, cat: PackCategory, terms: List<Pair<String, String>>, learned: Int) =
        WordPack(
            id = id,
            title = title,
            description = desc,
            category = cat,
            words = terms.mapIndexed { i, (t, m) ->
                w(t, m, status = if (i < learned) WordStatus.Memorized else WordStatus.New)
            },
        )

    val packs: List<WordPack> = listOf(
        WordPack(1, "수능 필수 영단어 Day 12", "고2 수준 핵심 어휘 24개", PackCategory.Suneung, suneungWords),
        simplePack(
            2, "교과서 2학년 3단원", "Lesson 3. The Power of Habits", PackCategory.Textbook,
            listOf("routine" to "일상", "repeat" to "반복하다", "quietly" to "조용히", "improve" to "향상시키다", "effort" to "노력", "progress" to "진전", "goal" to "목표", "daily" to "매일의"),
            8,
        ),
        simplePack(
            3, "IT 기술 영어", "개발 문서에 자주 나오는 표현", PackCategory.It,
            listOf("deploy" to "배포하다", "compile" to "컴파일하다", "debug" to "디버그하다", "framework" to "프레임워크", "latency" to "지연 시간", "repository" to "저장소", "query" to "질의", "cache" to "캐시", "thread" to "스레드", "variable" to "변수"),
            1,
        ),
        simplePack(
            4, "토익 빈출 동사", "비즈니스 상황 필수 동사 30개", PackCategory.Toeic,
            listOf("negotiate" to "협상하다", "submit" to "제출하다", "postpone" to "연기하다", "attend" to "참석하다", "reimburse" to "변제하다", "renew" to "갱신하다", "launch" to "출시하다", "assign" to "배정하다"),
            0,
        ),
        simplePack(
            5, "중간고사 대비", "시험 범위 핵심 단어", PackCategory.Textbook,
            listOf("abandon" to "버리다", "accurate" to "정확한", "achieve" to "성취하다", "adapt" to "적응하다", "adequate" to "충분한", "analyze" to "분석하다", "anticipate" to "예상하다", "attempt" to "시도하다"),
            3,
        ).copy(isMine = true, sourceLabel = "내 단어팩"),
        simplePack(
            6, "헷갈리는 단어 모음", "affect·effect 같은 혼동 어휘", PackCategory.Suneung,
            listOf("affect" to "영향을 미치다", "effect" to "효과", "adopt" to "채택하다", "adapt" to "적응하다"),
            1,
        ).copy(isMine = true, sourceLabel = "내 단어팩"),
    )

    val dailySentence = DailySentence(
        dateLabel = "오늘의 문장 · 11월 8일",
        sentence = "Small habits, repeated every day, quietly shape who we become.",
        underlined = listOf("habits", "repeated", "shape"),
        translation = "매일 반복되는 작은 습관이 조용히 우리를 만들어 간다.",
    )

    val missions = listOf(
        DailyMission(1, "대결 1회 참여", 1, 1, 15),
        DailyMission(2, "단어 20개 학습", 14, 20, 20),
        DailyMission(3, "오늘의 문장 퀴즈", 0, 1, 10),
    )

    private val names = listOf("양지우", "김도윤", "이서연", "박하준", "최유나", "정민재", "강예린", "윤시우", "장서윤", "임건우")

    fun ranking(seed: Int): List<RankingEntry> {
        val top = (1..30).map { rank ->
            RankingEntry(
                rank = rank,
                name = names[(rank + seed) % names.size],
                schoolInfo = "${(rank % 3) + 1}학년 ${(rank % 5) + 1}반",
                xp = 1_640 - rank * 40 + seed * 7,
                delta = listOf(3, -1, 0, 2, -4, 1)[(rank + seed) % 6],
            )
        }.toMutableList()
        top[22] = RankingEntry(23, "한국", "2학년 1반", top[22].xp, 5, isMe = true)
        return top
    }

    val battleRecords = listOf(
        BattleRecord(1, "수능 필수 영단어 Day 12", "오늘 · 8명 참여", 8, 1, 1_320),
        BattleRecord(2, "교과서 2학년 3단원", "어제 · 4명 참여", 4, 2, 1_116),
        BattleRecord(3, "IT 기술 영어", "11월 5일 · 6명 참여", 6, 3, 1_050),
        BattleRecord(4, "토익 빈출 동사", "11월 2일 · 5명 참여", 5, 4, 890),
    )

    val badges = listOf(
        Badge("10월 3주차 주간 상위 10%", "학교 전체 · 10월 21일", Medal.Silver),
        Badge("10월 3주차 주간 1등", "2학년 1반 · 10월 21일", Medal.Gold, isNew = true),
        Badge("10월 2주차 주간 상위 10%", "학교 전체 · 10월 14일", Medal.Silver),
        Badge("9월 월간 3등", "2학년 1반 · 9월 30일", Medal.Bronze),
        Badge("9월 4주차 주간 상위 10%", "학교 전체 · 9월 28일", Medal.Silver),
    )
}
