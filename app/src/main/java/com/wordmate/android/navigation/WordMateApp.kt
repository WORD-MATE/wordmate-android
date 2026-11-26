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
    composable<BattleRoute> { PlaceholderScreen("대결") }
    composable<RankingRoute> { PlaceholderScreen("랭킹") }
    composable<MyPageRoute> { PlaceholderScreen("마이페이지") }
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
    composable<PackCreateRoute> { PlaceholderScreen("단어팩 만들기") }
}

@Suppress("UNUSED_PARAMETER")
private fun androidx.navigation.NavGraphBuilder.studyGraph(navController: NavHostController) {
    composable<StudySetupRoute> { PlaceholderScreen("학습 설정") }
    composable<StudySessionRoute> { PlaceholderScreen("학습") }
    composable<StudyResultRoute> { PlaceholderScreen("학습 결과") }
}

@Suppress("UNUSED_PARAMETER")
private fun androidx.navigation.NavGraphBuilder.battleGraph(navController: NavHostController) {
    composable<BattleCreateRoute> { PlaceholderScreen("대결방 만들기") }
    composable<BattleSettingsRoute> { PlaceholderScreen("대결 설정") }
    composable<BattleCodeRoute> { PlaceholderScreen("코드 입력") }
    composable<BattleLobbyRoute> { PlaceholderScreen("대기실") }
    composable<BattlePlayRoute> { PlaceholderScreen("대결") }
    composable<BattleResultRoute> { PlaceholderScreen("대결 결과") }
    composable<BattleWrongWordsRoute> { PlaceholderScreen("틀린 단어") }
    composable<BattleHistoryRoute> { PlaceholderScreen("대결 기록") }
}

@Suppress("UNUSED_PARAMETER")
private fun androidx.navigation.NavGraphBuilder.myPageGraph(navController: NavHostController) {
    composable<BadgesRoute> { PlaceholderScreen("랭킹 배지") }
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
