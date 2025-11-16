package com.arekalov.cheater.presentation.global_search

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
class GlobalSearchViewModel @Inject constructor(
    private val repository: QuestionRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(GlobalSearchState())
    val state: StateFlow<GlobalSearchState> = _state.asStateFlow()
    
    fun handleIntent(intent: GlobalSearchIntent) {
        when (intent) {
            is GlobalSearchIntent.Search -> search(intent.query)
            is GlobalSearchIntent.SelectQuestion -> {
                // Navigation handled in Screen
            }
        }
    }
    
    private fun search(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(query = query, isLoading = true, error = null, showEmptyMessage = false) }
            try {
                if (query.isBlank()) {
                    _state.update { it.copy(questions = emptyList(), isLoading = false) }
                } else {
                    val questions = repository.searchQuestions(query)
                    _state.update { 
                        it.copy(
                            questions = questions, 
                            isLoading = false,
                            showEmptyMessage = questions.isEmpty()
                        ) 
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }
}

