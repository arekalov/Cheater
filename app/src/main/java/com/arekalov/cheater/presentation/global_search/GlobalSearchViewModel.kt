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
    
    init {
        loadTotalCount()
    }
    
    private fun loadTotalCount() {
        viewModelScope.launch {
            try {
                val totalCount = repository.getTotalQuestionsCount()
                _state.update { it.copy(totalQuestionsCount = totalCount) }
            } catch (e: Exception) {
                // Игнорируем ошибку, счетчик останется 0
            }
        }
    }
    
    fun handleIntent(intent: GlobalSearchIntent) {
        when (intent) {
            is GlobalSearchIntent.Search -> search(intent.query)
            is GlobalSearchIntent.SelectQuestion -> {
                // Navigation handled in Screen
            }
            is GlobalSearchIntent.ToggleImagesFilter -> toggleImagesFilter(intent.enabled)
        }
    }
    
    private fun toggleImagesFilter(enabled: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(showOnlyWithImages = enabled) }
            // Повторно применить поиск с новым фильтром
            val currentQuery = _state.value.query
            if (currentQuery.isNotBlank()) {
                applyFilters(currentQuery)
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
                    applyFilters(query)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }
    
    private suspend fun applyFilters(query: String) {
        val allQuestions = repository.searchQuestions(query)
        val filteredQuestions = if (_state.value.showOnlyWithImages) {
            allQuestions.filter { it.images.isNotEmpty() }
        } else {
            allQuestions
        }
        _state.update { 
            it.copy(
                questions = filteredQuestions, 
                isLoading = false,
                showEmptyMessage = filteredQuestions.isEmpty()
            ) 
        }
    }
}

