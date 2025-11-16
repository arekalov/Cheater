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
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.arekalov.cheater.data.model.Question
import com.arekalov.cheater.presentation.components.ErrorMessage
import com.arekalov.cheater.presentation.components.LoadingIndicator
import com.arekalov.cheater.presentation.components.QuestionCard
import com.arekalov.cheater.presentation.theme.CheaterTheme
import android.app.RemoteInput
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender

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
    val listState = rememberScalingLazyListState()
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results: Bundle = RemoteInput.getResultsFromIntent(data)
            val searchText: CharSequence? = results.getCharSequence("search_input")
            searchText?.let {
                onQueryChange(it.toString())
            }
        }
    }
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        item {
            Chip(
                onClick = { 
                    val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                    val remoteInputs = listOf(
                        RemoteInput.Builder("search_input")
                            .setLabel("Поиск вопроса")
                            .wearableExtender {
                                setEmojisAllowed(false)
                                setInputActionType(EditorInfo.IME_ACTION_DONE)
                            }
                            .build()
                    )
                    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                    
                    // Предзаполняем текущее значение запроса
                    if (query.isNotEmpty()) {
                        val bundle = Bundle()
                        bundle.putCharSequence("search_input", query)
                        RemoteInput.addResultsToIntent(remoteInputs.toTypedArray(), intent, bundle)
                    }
                    
                    launcher.launch(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                label = {
                    Text(
                        text = if (query.isEmpty()) "Поиск..." else query,
                        style = MaterialTheme.typography.caption1,
                        maxLines = 2
                    )
                },
                icon = {
                    Text("🔍")
                },
                colors = ChipDefaults.primaryChipColors()
            )
        }
        
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

