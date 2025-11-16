package com.arekalov.cheater.presentation.global_search

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import com.arekalov.cheater.data.model.Question
import com.arekalov.cheater.presentation.components.ErrorMessage
import com.arekalov.cheater.presentation.components.LoadingIndicator
import com.arekalov.cheater.presentation.components.QuestionCard
import com.arekalov.cheater.presentation.theme.CheaterTheme

@Composable
fun GlobalSearchScreen(
    navController: NavController,
    viewModel: GlobalSearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        // Автофокус на поиске при открытии
    }
    
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorMessage(message = state.error ?: "Error")
        else -> GlobalSearchContent(
            query = state.query,
            questions = state.questions,
            showEmptyMessage = state.showEmptyMessage,
            onQueryChange = { viewModel.handleIntent(GlobalSearchIntent.Search(it)) },
            onQuestionClick = { questionId ->
                viewModel.handleIntent(GlobalSearchIntent.SelectQuestion(questionId))
                navController.navigate("question/$questionId")
            }
        )
    }
}

@Composable
private fun GlobalSearchContent(
    query: String,
    questions: List<Question>,
    showEmptyMessage: Boolean,
    onQueryChange: (String) -> Unit,
    onQuestionClick: (Int) -> Unit
) {
    var searchText by remember { mutableStateOf(query) }
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        item {
            OutlinedButton(
                onClick = { 
                    // Открыть клавиатуру для ввода текста
                    // На Wear OS используется речевой ввод или клавиатура часов
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = if (searchText.isEmpty()) "Введите текст..." else searchText,
                    style = MaterialTheme.typography.caption1,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Простой text input для эмулятора/preview
        item {
            Text(
                text = "Найдено: ${questions.size}",
                style = MaterialTheme.typography.caption2,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        
        if (showEmptyMessage) {
            item {
                Text(
                    text = "Ничего не найдено",
                    style = MaterialTheme.typography.caption1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        
        items(questions) { question ->
            QuestionCard(
                question = question,
                onClick = { onQuestionClick(question.id) }
            )
        }
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun GlobalSearchContentPreview() {
    CheaterTheme {
        GlobalSearchContent(
            query = "дискретные",
            questions = listOf(
                Question(
                    id = 1,
                    category = "random_vars",
                    text = "Как называются случайные величины, принимающие только отделенные друг от друга значения, которые можно пронумеровать?",
                    images = listOf("O_1.JPG"),
                    answers = listOf("дискретные"),
                    keywords = listOf("случайные", "величины")
                ),
                Question(
                    id = 2,
                    category = "random_vars",
                    text = "Какие величины являются дискретными?",
                    images = emptyList(),
                    answers = listOf("число зрителей", "количество сообщений"),
                    keywords = listOf("величины", "дискретные")
                )
            ),
            showEmptyMessage = false,
            onQueryChange = {},
            onQuestionClick = {}
        )
    }
}

