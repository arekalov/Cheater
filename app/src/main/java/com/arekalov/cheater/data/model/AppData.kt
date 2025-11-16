package com.arekalov.cheater.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppData(
    val categories: List<Category>,
    val questions: List<Question>
)

@Serializable
data class Category(
    val id: String,
    val name: String
)

@Serializable
data class Question(
    val id: Int,
    val category: String,
    val text: String,
    val images: List<String> = emptyList(),
    val answers: List<String>,
    val keywords: List<String>
)

