package com.arekalov.cheater.presentation.category

sealed interface CategoryIntent {
    object LoadCategories : CategoryIntent
    data class SelectCategory(val categoryId: String) : CategoryIntent
}

