package com.arekalov.cheater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.arekalov.cheater.presentation.navigation.AppNavGraph
import com.arekalov.cheater.presentation.theme.CheaterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            CheaterTheme {
                val navController = rememberSwipeDismissableNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}

