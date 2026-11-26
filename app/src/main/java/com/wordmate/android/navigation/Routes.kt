package com.wordmate.android.navigation

import kotlinx.serialization.Serializable

// 인증
@Serializable data object LoginRoute
@Serializable data object TermsRoute
@Serializable data class TermDetailRoute(val termType: String)

// 하단 탭
@Serializable data object MainGraph
@Serializable data object HomeRoute
@Serializable data object WordPacksRoute
@Serializable data object BattleRoute
@Serializable data object RankingRoute
@Serializable data object MyPageRoute

// 단어팩
@Serializable data class PackDetailRoute(val packId: Long)
@Serializable data object PackCreateRoute

// 학습
@Serializable data class StudySetupRoute(val packId: Long)
@Serializable data class StudySessionRoute(
    val packId: Long,
    val mode: String,
    val direction: String,
    val count: Int,
    val shuffle: Boolean,
    val onlyWrong: Boolean = false,
)
@Serializable data object StudyResultRoute

// 대결
@Serializable data object BattleCreateRoute
@Serializable data class BattleSettingsRoute(val packId: Long)
@Serializable data object BattleCodeRoute
@Serializable data object BattleLobbyRoute
@Serializable data object BattlePlayRoute
@Serializable data object BattleResultRoute
@Serializable data object BattleWrongWordsRoute
@Serializable data object BattleHistoryRoute

// 마이페이지
@Serializable data object BadgesRoute
@Serializable data object SettingsRoute
@Serializable data object NotificationSettingsRoute
@Serializable data object LoginManagementRoute
@Serializable data object NoticeRoute
@Serializable data object InquiryRoute
