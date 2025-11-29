package com.arekalov.cheater.presentation.global_search

sealed interface GlobalSearchIntent {
    data class Search(val query: String) : GlobalSearchIntent
    data class SelectQuestion(val questionId: Int) : GlobalSearchIntent
    data class ToggleImagesFilter(val enabled: Boolean) : GlobalSearchIntent
}

