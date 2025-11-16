package com.arekalov.cheater.presentation.search

sealed interface SearchIntent {
    data class LoadQuestions(val categoryId: String) : SearchIntent
    data class Search(val query: String) : SearchIntent
    data class SelectQuestion(val questionId: Int) : SearchIntent
}

