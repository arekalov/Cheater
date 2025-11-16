package com.arekalov.cheater.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arekalov.cheater.data.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionDetailViewModel @Inject constructor(
    private val repository: QuestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val questionId: Int = savedStateHandle.get<String>("questionId")?.toIntOrNull() ?: 0
    
    private val _state = MutableStateFlow(QuestionDetailState())
    val state: StateFlow<QuestionDetailState> = _state.asStateFlow()
    
    init {
        handleIntent(QuestionDetailIntent.LoadQuestion(questionId))
    }
    
    fun handleIntent(intent: QuestionDetailIntent) {
        when (intent) {
            is QuestionDetailIntent.LoadQuestion -> loadQuestion(intent.questionId)
        }
    }
    
    private fun loadQuestion(questionId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val question = repository.getQuestionById(questionId)
                if (question != null) {
                    _state.update { it.copy(question = question, isLoading = false) }
                } else {
                    _state.update { it.copy(error = "Вопрос не найден", isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }
}

