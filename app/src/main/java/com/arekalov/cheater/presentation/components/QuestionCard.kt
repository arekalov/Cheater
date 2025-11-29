package com.arekalov.cheater.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.arekalov.cheater.data.model.Question
import com.arekalov.cheater.presentation.theme.CheaterTheme

@Composable
fun QuestionCard(
    question: Question,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Текст вопроса (без обрезки, уменьшенный шрифт)
            Text(
                text = question.text,
                style = MaterialTheme.typography.caption1.copy(fontSize = 11.sp),
                modifier = Modifier.padding(bottom = if (question.images.isNotEmpty() || shouldShowAnswer(question)) 6.dp else 0.dp)
            )
            
            // Изображения если есть
            if (question.images.isNotEmpty()) {
                question.images.take(1).forEach { imageName ->
                    ImagePreview(imageName = imageName)
                }
            }
            
            // Показываем ответ сразу, если это вопрос с одним ответом (без множественных картинок)
            if (shouldShowAnswer(question)) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ответ:",
                    style = MaterialTheme.typography.caption2.copy(fontSize = 9.sp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                question.answers.forEach { answer ->
                    Text(
                        text = "• $answer",
                        style = MaterialTheme.typography.caption2.copy(fontSize = 10.sp),
                        color = MaterialTheme.colors.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }
        }
    }
}

/**
 * Определяет, нужно ли показывать ответ сразу в карточке
 * Показываем ответ, если это не вопрос с несколькими картинками (где каждой картинке соответствует свой ответ)
 */
private fun shouldShowAnswer(question: Question): Boolean {
    // Если нет картинок - показываем ответ
    if (question.images.isEmpty()) {
        return question.answers.isNotEmpty()
    }
    
    // Если картинок меньше или равно одной, и ответ один - показываем
    if (question.images.size <= 1 && question.answers.size == 1) {
        return true
    }
    
    // Если количество картинок не совпадает с количеством ответов - это не "вопрос с выбором для каждой картинки"
    // значит можно показать ответ
    if (question.images.size != question.answers.size) {
        return question.answers.isNotEmpty()
    }
    
    // Если картинок несколько и они совпадают с количеством ответов - не показываем (это вопрос с вариантами)
    return false
}

@Composable
private fun ImagePreview(imageName: String) {
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
                .height(80.dp)
                .padding(top = 4.dp)
        )
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun QuestionCardPreview() {
    CheaterTheme {
        QuestionCard(
            question = Question(
                id = 1,
                category = "random_vars",
                text = "Как называются случайные величины, принимающие только отделенные друг от друга значения, которые можно пронумеровать?",
                images = emptyList(),
                answers = listOf("дискретные"),
                keywords = listOf("случайные", "величины")
            ),
            onClick = {}
        )
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun QuestionCardWithImagePreview() {
    CheaterTheme {
        QuestionCard(
            question = Question(
                id = 2,
                category = "math_expectation",
                text = "Пусть xi - одно из n значений, которые может принимать дискретная случайная величина X. Укажите, что рассчитывается с помощью формулы:",
                images = listOf("O_1.JPG"),
                answers = listOf("Математическое ожидание X."),
                keywords = listOf("значений", "дискретная")
            ),
            onClick = {}
        )
    }
}


