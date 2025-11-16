package com.arekalov.cheater.presentation.category

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Text
import com.arekalov.cheater.data.model.Category
import com.arekalov.cheater.presentation.components.ErrorMessage
import com.arekalov.cheater.presentation.components.LoadingIndicator
import com.arekalov.cheater.presentation.theme.CheaterTheme

@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorMessage(message = state.error ?: "Error")
        else -> CategoryList(
            categories = state.categories,
            onCategoryClick = { categoryId ->
                viewModel.handleIntent(CategoryIntent.SelectCategory(categoryId))
                navController.navigate("search/$categoryId")
            }
        )
    }
}

@Composable
private fun CategoryList(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(categories) { category ->
            Card(
                onClick = { onCategoryClick(category.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = category.name,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun CategoryListPreview() {
    CheaterTheme {
        CategoryList(
            categories = listOf(
                Category("random_vars", "Случайные величины"),
                Category("distributions", "Распределения"),
                Category("math_expectation", "Мат. ожидание"),
                Category("dispersion", "Дисперсия"),
                Category("other", "Другое")
            ),
            onCategoryClick = {}
        )
    }
}

