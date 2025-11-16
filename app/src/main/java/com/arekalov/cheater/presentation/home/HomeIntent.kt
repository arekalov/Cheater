package com.arekalov.cheater.presentation.home

sealed interface HomeIntent {
    object NavigateToSearch : HomeIntent
    object NavigateToCategories : HomeIntent
}

