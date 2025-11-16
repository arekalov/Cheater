package com.arekalov.cheater.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.arekalov.cheater.presentation.theme.CheaterTheme

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeContent(
        onSearchClick = {
            viewModel.handleIntent(HomeIntent.NavigateToSearch)
            navController.navigate("global_search")
        },
        onCategoriesClick = {
            viewModel.handleIntent(HomeIntent.NavigateToCategories)
            navController.navigate("categories")
        }
    )
}

@Composable
private fun HomeContent(
    onSearchClick: () -> Unit,
    onCategoriesClick: () -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Поиск вопросов",
                style = MaterialTheme.typography.title3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
        
        item {
            Button(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Text(
                    text = "Поиск по тексту",
                    textAlign = TextAlign.Center
                )
            }
        }
        
        item {
            Button(
                onClick = onCategoriesClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Text(
                    text = "По категориям",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    CheaterTheme {
        HomeContent(
            onSearchClick = {},
            onCategoriesClick = {}
        )
    }
}

