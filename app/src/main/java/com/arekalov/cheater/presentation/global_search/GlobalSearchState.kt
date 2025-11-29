package com.arekalov.cheater.presentation.global_search

import com.arekalov.cheater.data.model.Question

data class GlobalSearchState(
    val questions: List<Question> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val showEmptyMessage: Boolean = false,
    val totalQuestionsCount: Int = 0,
    val showOnlyWithImages: Boolean = false
)

