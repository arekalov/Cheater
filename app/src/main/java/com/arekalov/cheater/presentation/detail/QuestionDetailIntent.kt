package com.arekalov.cheater.presentation.detail

sealed interface QuestionDetailIntent {
    data class LoadQuestion(val questionId: Int) : QuestionDetailIntent
}

