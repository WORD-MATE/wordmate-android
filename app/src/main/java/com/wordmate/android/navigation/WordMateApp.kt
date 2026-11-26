package com.wordmate.android.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.toRoute
import com.wordmate.android.core.ui.rememberRepository
import com.wordmate.android.feature.auth.LoginScreen
import com.wordmate.android.feature.auth.TermDetailScreen
import com.wordmate.android.feature.auth.TermType
import com.wordmate.android.feature.auth.TermsScreen
import com.wordmate.android.feature.wordpack.PackDetailScreen
import com.wordmate.android.feature.wordpack.WordPacksScreen
import com.wordmate.android.data.model.QuizDirection
import com.wordmate.android.data.model.StudyMode
import com.wordmate.android.data.model.StudyOptions
import com.wordmate.android.feature.study.StudyResultScreen
import com.wordmate.android.feature.study.StudySessionScreen
import com.wordmate.android.feature.study.StudySetupScreen
import com.wordmate.android.feature.battle.BattleCodeScreen
import com.wordmate.android.feature.battle.BattleCreateScreen
import com.wordmate.android.feature.battle.BattleHistoryScreen
import com.wordmate.android.feature.battle.BattleHomeScreen
import com.wordmate.android.feature.battle.BattleLobbyScreen
import com.wordmate.android.feature.battle.BattlePlayScreen
import com.wordmate.android.feature.battle.BattleResultScreen
import com.wordmate.android.feature.battle.BattleWrongWordsScreen
import com.wordmate.android.feature.battle.BattleSettingsScreen
import com.wordmate.android.core.designsystem.theme.WmColors
import com.wordmate.android.core.designsystem.theme.WmTypography

@Composable
fun WordMateApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = backStackEntry?.destination?.currentTab()

    Column(Modifier.fillMaxSize().background(WmColors.Background)) {
        Box(Modifier.weight(1f).statusBarsPadding()) {
            NavHost(navController = navController, startDestination = LoginRoute) {
                authGraph(navController)
                navigation<MainGraph>(startDestination = HomeRoute) {
                    mainTabs(navController)
                }
                featureGraph(navController)
            }
        }
        if (currentTab != null) {
            WmBottomBar(selected = currentTab, onSelect = { navController.navigateToTab(it) })
        }
    }
}

private fun NavDestination.currentTab(): MainTab? =
    MainTab.entries.firstOrNull { tab -> hierarchy.any { it.hasRoute(tab.route::class) } }

fun NavHostController.navigateToTab(tab: MainTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.navigateToMain() {
    navigate(MainGraph) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

private fun androidx.navigation.NavGraphBuilder.authGraph(navController: NavHostController) {
    composable<LoginRoute> {
        val repository = rememberRepository()
        LoginScreen(onLoginClick = {
            repository.login()
            navController.navigate(TermsRoute)
        })
    }
    composable<TermsRoute> {
        val user by rememberRepository().user.collectAsStateWithLifecycle()
        TermsScreen(
            userName = user.name,
            onBack = { navController.popBackStack() },
            onOpenDetail = { navController.navigate(TermDetailRoute(it.name)) },
            onAgree = { navController.navigateToMain() },
        )
    }
    composable<TermDetailRoute> { entry ->
        val route = entry.toRoute<TermDetailRoute>()
        TermDetailScreen(term = TermType.valueOf(route.termType), onBack = { navController.popBackStack() })
    }
}

private fun androidx.navigation.NavGraphBuilder.mainTabs(navController: NavHostController) {
    composable<HomeRoute> {
        com.wordmate.android.feature.home.HomeRoute(
            onStudyClick = { navController.navigate(StudySetupRoute(it)) },
            onBattleClick = { navController.navigateToTab(MainTab.Battle) },
            onPackClick = { navController.navigate(PackDetailRoute(it)) },
            onAllPacksClick = { navController.navigateToTab(MainTab.WordPacks) },
            onRankingClick = { navController.navigateToTab(MainTab.Ranking) },
            onBattleCodeClick = { navController.navigate(BattleCodeRoute) },
        )
    }
    composable<WordPacksRoute> {
        WordPacksScreen(
            onPackClick = { navController.navigate(PackDetailRoute(it)) },
            onCreateClick = { navController.navigate(PackCreateRoute) },
        )
    }
    composable<BattleRoute> {
        BattleHomeScreen(
            onCreateClick = { navController.navigate(BattleCreateRoute) },
            onCodeClick = { navController.navigate(BattleCodeRoute) },
            onHistoryClick = { navController.navigate(BattleHistoryRoute) },
            onMatched = { navController.navigate(BattleLobbyRoute) },
        )
    }
    composable<RankingRoute> { com.wordmate.android.feature.ranking.RankingScreen() }
    composable<MyPageRoute> {
        com.wordmate.android.feature.mypage.MyPageScreen(
            onBadgesClick = { navController.navigate(BadgesRoute) },
            onStudyClick = { navController.navigateToTab(MainTab.WordPacks) },
            onBattleHistoryClick = { navController.navigate(BattleHistoryRoute) },
            onSettingsClick = { navController.navigate(SettingsRoute) },
            onNoticeClick = { navController.navigate(NoticeRoute) },
            onInquiryClick = { navController.navigate(InquiryRoute) },
        )
    }
}

private fun androidx.navigation.NavGraphBuilder.featureGraph(navController: NavHostController) {
    wordPackGraph(navController)
    studyGraph(navController)
    battleGraph(navController)
    myPageGraph(navController)
}

private fun androidx.navigation.NavGraphBuilder.wordPackGraph(navController: NavHostController) {
    composable<PackDetailRoute> { entry ->
        PackDetailScreen(
            packId = entry.toRoute<PackDetailRoute>().packId,
            onBack = { navController.popBackStack() },
            onStudyClick = { navController.navigate(StudySetupRoute(it)) },
        )
    }
    composable<PackCreateRoute> {
        com.wordmate.android.feature.wordpack.PackCreateScreen(
            onBack = { navController.popBackStack() },
            onCreated = { id ->
                navController.navigate(PackDetailRoute(id)) {
                    popUpTo<PackCreateRoute> { inclusive = true }
                }
            },
        )
    }
}

private fun androidx.navigation.NavGraphBuilder.studyGraph(navController: NavHostController) {
    composable<StudySetupRoute> { entry ->
        val packId = entry.toRoute<StudySetupRoute>().packId
        StudySetupScreen(
            packId = packId,
            onBack = { navController.popBackStack() },
            onStart = { options ->
                navController.navigate(
                    StudySessionRoute(packId, options.mode.name, options.direction.name, options.questionCount, options.shuffle),
                )
            },
        )
    }
    composable<StudySessionRoute> { entry ->
        val route = entry.toRoute<StudySessionRoute>()
        val options = StudyOptions(
            mode = StudyMode.valueOf(route.mode),
            direction = QuizDirection.valueOf(route.direction),
            questionCount = route.count,
            shuffle = route.shuffle,
        )
        StudySessionScreen(
            packId = route.packId,
            options = options,
            onlyWrong = route.onlyWrong,
            onExit = { navController.popBackStack() },
            onFinished = {
                navController.navigate(StudyResultRoute(route.packId, route.mode, route.direction, route.count, route.shuffle)) {
                    popUpTo<StudySessionRoute> { inclusive = true }
                }
            },
        )
    }
    composable<StudyResultRoute> { entry ->
        val route = entry.toRoute<StudyResultRoute>()
        fun retry(onlyWrong: Boolean) {
            navController.navigate(
                StudySessionRoute(route.packId, route.mode, route.direction, route.count, route.shuffle, onlyWrong),
            ) { popUpTo<StudyResultRoute> { inclusive = true } }
        }
        StudyResultScreen(
            onClose = { navController.navigateToTab(MainTab.Home) },
            onRetryWrong = { retry(onlyWrong = true) },
            onRetryAll = { retry(onlyWrong = false) },
        )
    }
}

private fun androidx.navigation.NavGraphBuilder.battleGraph(navController: NavHostController) {
    composable<BattleCreateRoute> {
        BattleCreateScreen(
            onBack = { navController.popBackStack() },
            onNext = { navController.navigate(BattleSettingsRoute(it)) },
        )
    }
    composable<BattleSettingsRoute> { entry ->
        BattleSettingsScreen(
            packId = entry.toRoute<BattleSettingsRoute>().packId,
            onBack = { navController.popBackStack() },
            onCreated = {
                navController.navigate(BattleLobbyRoute) { popUpTo<BattleCreateRoute> { inclusive = true } }
            },
        )
    }
    composable<BattleCodeRoute> {
        BattleCodeScreen(
            onBack = { navController.popBackStack() },
            onJoined = {
                navController.navigate(BattleLobbyRoute) { popUpTo<BattleCodeRoute> { inclusive = true } }
            },
        )
    }
    composable<BattleLobbyRoute> {
        BattleLobbyScreen(
            onBack = { navController.popBackStack() },
            onStart = {
                navController.navigate(BattlePlayRoute) { popUpTo<BattleLobbyRoute> { inclusive = true } }
            },
        )
    }
    composable<BattlePlayRoute> {
        BattlePlayScreen(
            onExit = { navController.popBackStack() },
            onFinished = {
                navController.navigate(BattleResultRoute) { popUpTo<BattlePlayRoute> { inclusive = true } }
            },
        )
    }
    composable<BattleResultRoute> {
        BattleResultScreen(
            onClose = { navController.navigateToTab(MainTab.Battle) },
            onWrongWordsClick = { navController.navigate(BattleWrongWordsRoute) },
        )
    }
    composable<BattleWrongWordsRoute> {
        BattleWrongWordsScreen(
            onBack = { navController.popBackStack() },
            onGoToPack = { navController.navigate(PackDetailRoute(it)) },
        )
    }
    composable<BattleHistoryRoute> { BattleHistoryScreen(onBack = { navController.popBackStack() }) }
}

@Suppress("UNUSED_PARAMETER")
private fun androidx.navigation.NavGraphBuilder.myPageGraph(navController: NavHostController) {
    composable<BadgesRoute> { com.wordmate.android.feature.mypage.BadgesScreen(onBack = { navController.popBackStack() }) }
    composable<SettingsRoute> { PlaceholderScreen("설정") }
    composable<NotificationSettingsRoute> { PlaceholderScreen("알림 설정") }
    composable<LoginManagementRoute> { PlaceholderScreen("로그인 관리") }
    composable<NoticeRoute> { PlaceholderScreen("공지사항") }
    composable<InquiryRoute> { PlaceholderScreen("문의하기") }
}

@Composable
internal fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = WmTypography.Title1, color = WmColors.TextPrimary)
    }
}
