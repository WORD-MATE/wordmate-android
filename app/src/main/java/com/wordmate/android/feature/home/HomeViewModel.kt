package com.wordmate.android.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordmate.android.data.model.AppNotification
import com.wordmate.android.data.model.DailyMission
import com.wordmate.android.data.model.DailySentence
import com.wordmate.android.data.model.RankingEntry
import com.wordmate.android.data.model.RankingPeriod
import com.wordmate.android.data.model.RankingScope
import com.wordmate.android.data.model.User
import com.wordmate.android.data.model.WordPack
import com.wordmate.android.data.model.WordStatus
import com.wordmate.android.data.repository.WordMateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val user: User,
    val reviewCount: Int,
    val wrongNoteCount: Int,
    val continuePack: WordPack?,
    val sentence: DailySentence,
    val missions: List<DailyMission>,
    val myRank: RankingEntry?,
    val nearbyRanking: List<RankingEntry>,
    val notifications: List<AppNotification>,
) {
    val unreadCount: Int get() = notifications.count { !it.read }
}

class HomeViewModel(private val repository: WordMateRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.user,
        repository.packs,
        repository.missions,
        repository.notifications,
    ) { user, packs, missions, notifications ->
        val ranking = repository.ranking(RankingScope.School, RankingPeriod.Weekly)
        val me = ranking.firstOrNull { it.isMe }
        val nearby = if (me == null) emptyList() else ranking.filter { it.rank in (me.rank - 1)..(me.rank + 1) }
        val allWords = packs.flatMap { it.words }
        HomeUiState(
            user = user,
            reviewCount = allWords.count { it.status == WordStatus.Memorized }.coerceAtMost(12),
            wrongNoteCount = allWords.count { it.status == WordStatus.Learning }.coerceAtLeast(0),
            continuePack = repository.lastStudyPackId?.let { id -> packs.firstOrNull { it.id == id } }
                ?: packs.firstOrNull { !it.isCompleted },
            sentence = repository.dailySentence,
            missions = missions,
            myRank = me,
            nearbyRanking = nearby,
            notifications = notifications,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial())

    private fun initial() = HomeUiState(
        user = repository.user.value,
        reviewCount = 0,
        wrongNoteCount = 0,
        continuePack = null,
        sentence = repository.dailySentence,
        missions = repository.missions.value,
        myRank = null,
        nearbyRanking = emptyList(),
        notifications = repository.notifications.value,
    )

    fun claimMission(id: Long) = repository.claimMission(id)
    fun markAllRead() = repository.markAllNotificationsRead()
    fun markRead(id: Long) = repository.markNotificationRead(id)
}
