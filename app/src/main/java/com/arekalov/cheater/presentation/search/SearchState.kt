package com.arekalov.cheater.presentation.search

import com.arekalov.cheater.data.model.Question

data class SearchState(
    val questions: List<Question> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

