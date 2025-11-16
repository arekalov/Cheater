package com.arekalov.cheater.presentation.search

import androidx.compose.foundation.layout.*
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
import androidx.wear.compose.material.*
import com.arekalov.cheater.data.model.Question
import com.arekalov.cheater.presentation.components.ErrorMessage
import com.arekalov.cheater.presentation.components.LoadingIndicator
import com.arekalov.cheater.presentation.components.QuestionCard
import com.arekalov.cheater.presentation.theme.CheaterTheme

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorMessage(message = state.error ?: "Error")
        else -> QuestionList(
            questions = state.questions,
            onQuestionClick = { questionId ->
                viewModel.handleIntent(SearchIntent.SelectQuestion(questionId))
                navController.navigate("question/$questionId")
            }
        )
    }
}

@Composable
private fun QuestionList(
    questions: List<Question>,
    onQuestionClick: (Int) -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
    ) {
        item {
            Text(
                text = "Вопросы: ${questions.size}",
                style = MaterialTheme.typography.caption1,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
        
        items(questions) { question ->
            QuestionCard(
                text = question.text,
                onClick = { onQuestionClick(question.id) }
            )
        }
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun QuestionListPreview() {
    CheaterTheme {
        QuestionList(
            questions = listOf(
                Question(
                    id = 1,
                    category = "random_vars",
                    text = "Как называются случайные величины, принимающие только отделенные друг от друга значения?",
                    answers = listOf("дискретные"),
                    keywords = listOf("случайные", "величины", "значения")
                ),
                Question(
                    id = 2,
                    category = "random_vars",
                    text = "Какие величины являются непрерывными?",
                    answers = listOf("время ожидания", "температура воздуха"),
                    keywords = listOf("величины", "непрерывные")
                )
            ),
            onQuestionClick = {}
        )
    }
}

