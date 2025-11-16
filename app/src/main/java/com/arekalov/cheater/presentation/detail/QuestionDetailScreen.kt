package com.arekalov.cheater.presentation.detail

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.arekalov.cheater.data.model.Question
import com.arekalov.cheater.presentation.components.ErrorMessage
import com.arekalov.cheater.presentation.components.LoadingIndicator
import com.arekalov.cheater.presentation.theme.CheaterTheme

@Composable
fun QuestionDetailScreen(
    navController: NavController,
    viewModel: QuestionDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorMessage(message = state.error ?: "Error")
        state.question != null -> QuestionDetail(question = state.question!!)
    }
}

@Composable
private fun QuestionDetail(question: Question) {
    val context = LocalContext.current
    val listState = rememberScalingLazyListState()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Текст вопроса
        item {
            Text(
                text = "Вопрос:",
                style = MaterialTheme.typography.caption1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
        item {
            Text(
                text = question.text,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        
        // Если есть изображения - показать картинку + ответ для каждой
        if (question.images.isNotEmpty()) {
            question.images.forEachIndexed { index, imageName ->
                // Картинка
                item {
                    ImageItem(imageName = imageName)
                }
                
                // Ответ к этой картинке (если есть)
                if (index < question.answers.size) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Ответ:",
                                style = MaterialTheme.typography.caption2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                            )
                            Text(
                                text = question.answers[index],
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onBackground,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Если нет картинок - просто показать все ответы
            if (question.answers.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ответы:",
                        style = MaterialTheme.typography.caption1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                question.answers.forEach { answer ->
                    item {
                        Text(
                            text = "• $answer",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
        
        // Дополнительное пространство для прокрутки
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ImageItem(imageName: String) {
    val context = LocalContext.current
    
    val bitmap = remember(imageName) {
        try {
            val inputStream = context.assets.open("images/$imageName")
            val bmp = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bmp
        } catch (e: Exception) {
            null
        }
    }
    
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Question image",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    } else {
        Text(
            text = "Изображение не загружено",
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.error
        )
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun QuestionDetailPreview() {
    CheaterTheme {
        QuestionDetail(
            question = Question(
                id = 1,
                category = "math_expectation",
                text = "Пусть xi - одно из n значений, которые может принимать дискретная случайная величина X. Укажите варианты для каждого графика:",
                images = listOf("O_1.JPG", "O_2.JPG"),
                answers = listOf(
                    "Математическое ожидание X.",
                    "Дисперсия X."
                ),
                keywords = listOf("значений", "дискретная", "случайная", "величина")
            )
        )
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun QuestionDetailNoImagesPreview() {
    CheaterTheme {
        QuestionDetail(
            question = Question(
                id = 2,
                category = "random_vars",
                text = "Как называются случайные величины, принимающие только отделенные друг от друга значения?",
                images = emptyList(),
                answers = listOf("дискретные"),
                keywords = listOf("случайные", "величины")
            )
        )
    }
}

