package com.arekalov.cheater.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme

// Темно-серая палитра
private val DarkGray = Color(0xFF2C2C2C)
private val MediumGray = Color(0xFF424242)
private val LightGray = Color(0xFF616161)
private val VeryLightGray = Color(0xFF9E9E9E)
private val WhiteGray = Color(0xFFE0E0E0)

private val DarkColorPalette = androidx.wear.compose.material.Colors(
    primary = LightGray,              // Основной цвет - светло-серый
    primaryVariant = MediumGray,       // Вариант основного - средне-серый
    secondary = VeryLightGray,         // Вторичный - очень светло-серый
    secondaryVariant = LightGray,      // Вариант вторичного
    background = Color.Black,          // Фон - черный (стандарт для Wear OS)
    surface = DarkGray,                // Поверхности - темно-серый
    error = Color(0xFFCF6679),        // Ошибки - красноватый (оставляем)
    onPrimary = Color.White,          // Текст на основном цвете
    onSecondary = Color.Black,         // Текст на вторичном цвете
    onBackground = WhiteGray,          // Текст на фоне - светло-серый
    onSurface = WhiteGray,             // Текст на поверхностях
    onError = Color.Black              // Текст на ошибках
)

@Composable
fun CheaterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        content = content
    )
}
