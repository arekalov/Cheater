package com.arekalov.cheater.data.repository

import com.arekalov.cheater.data.datasource.LocalDataSource
import com.arekalov.cheater.data.model.Category
import com.arekalov.cheater.data.model.Question
import com.arekalov.cheater.data.search.SearchEngine
import com.arekalov.cheater.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val searchEngine: SearchEngine,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getAllCategories(): List<Category> = 
        withContext(ioDispatcher) {
            localDataSource.getAppData().categories
        }
    
    suspend fun getQuestionsByCategory(categoryId: String): List<Question> = 
        withContext(ioDispatcher) {
            localDataSource.getAppData().questions
                .filter { it.category == categoryId }
        }
    
    suspend fun searchQuestions(query: String): List<Question> = 
        withContext(ioDispatcher) {
            val allQuestions = localDataSource.getAppData().questions
            searchEngine.search(allQuestions, query)
        }
    
    suspend fun getQuestionById(questionId: Int): Question? =
        withContext(ioDispatcher) {
            localDataSource.getAppData().questions
                .find { it.id == questionId }
        }
}

