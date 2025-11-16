package com.arekalov.cheater.presentation.detail

import com.arekalov.cheater.data.model.Question

data class QuestionDetailState(
    val question: Question? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

