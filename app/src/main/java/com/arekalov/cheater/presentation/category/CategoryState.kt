package com.arekalov.cheater.presentation.category

import com.arekalov.cheater.data.model.Category

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

