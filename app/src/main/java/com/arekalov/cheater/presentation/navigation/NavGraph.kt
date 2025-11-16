package com.arekalov.cheater.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.arekalov.cheater.presentation.category.CategoryScreen
import com.arekalov.cheater.presentation.detail.QuestionDetailScreen
import com.arekalov.cheater.presentation.global_search.GlobalSearchScreen
import com.arekalov.cheater.presentation.home.HomeScreen
import com.arekalov.cheater.presentation.search.SearchScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        
        composable("global_search") {
            GlobalSearchScreen(navController = navController)
        }
        
        composable("categories") {
            CategoryScreen(navController = navController)
        }
        
        composable(
            route = "search/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) {
            SearchScreen(navController = navController)
        }
        
        composable(
            route = "question/{questionId}",
            arguments = listOf(navArgument("questionId") { type = NavType.StringType })
        ) {
            QuestionDetailScreen(navController = navController)
        }
    }
}

