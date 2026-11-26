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
    composable<LoginRoute> { PlaceholderScreen("로그인") }
    composable<TermsRoute> { PlaceholderScreen("약관 동의") }
}

private fun androidx.navigation.NavGraphBuilder.mainTabs(navController: NavHostController) {
    composable<HomeRoute> { PlaceholderScreen("홈") }
    composable<WordPacksRoute> { PlaceholderScreen("단어팩") }
    composable<BattleRoute> { PlaceholderScreen("대결") }
    composable<RankingRoute> { PlaceholderScreen("랭킹") }
    composable<MyPageRoute> { PlaceholderScreen("마이페이지") }
}

@Suppress("UNUSED_PARAMETER")
private fun androidx.navigation.NavGraphBuilder.featureGraph(navController: NavHostController) = Unit

@Composable
internal fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = WmTypography.Title1, color = WmColors.TextPrimary)
    }
}
