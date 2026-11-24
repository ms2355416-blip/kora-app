package com.kora.challenge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kora.challenge.ui.common.ComingSoonScreen
import com.kora.challenge.ui.daily.DailyScreen
import com.kora.challenge.ui.trivia.TriviaScreen
import com.kora.challenge.ui.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val GAME = "game/{id}"
    fun game(id: String) = "game/$id"
}

@Composable
fun AppNav(dark: Boolean, onToggleTheme: () -> Unit) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                dark = dark,
                onToggleTheme = onToggleTheme,
                onOpenGame = { nav.navigate(Routes.game(it)) }
            )
        }
        // كل لعبة بعد ما تتحول بتتضاف هنا. اللي لسه ما اتحولتش بتروح ComingSoonScreen.
        composable(Routes.GAME) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            val back = { nav.popBackStack(); Unit }
            when (id) {
                "daily" -> DailyScreen(onBack = back)
                "trivia" -> TriviaScreen(onBack = back)
                else -> ComingSoonScreen(gameId = id, onBack = back)
            }
        }
    }
}
