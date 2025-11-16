package com.arekalov.cheater.presentation.search

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
class SearchViewModel @Inject constructor(
    private val repository: QuestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val categoryId: String = savedStateHandle.get<String>("categoryId") ?: ""
    
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()
    
    init {
        handleIntent(SearchIntent.LoadQuestions(categoryId))
    }
    
    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.LoadQuestions -> loadQuestions(intent.categoryId)
            is SearchIntent.Search -> search(intent.query)
            is SearchIntent.SelectQuestion -> {
                // Navigation handled in Screen
            }
        }
    }
    
    private fun loadQuestions(categoryId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val questions = repository.getQuestionsByCategory(categoryId)
                _state.update { it.copy(questions = questions, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }
    
    private fun search(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(query = query, isLoading = true, error = null) }
            try {
                val questions = if (query.isBlank()) {
                    repository.getQuestionsByCategory(categoryId)
                } else {
                    repository.searchQuestions(query)
                }
                _state.update { it.copy(questions = questions, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }
}

