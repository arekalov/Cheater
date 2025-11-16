package com.arekalov.cheater.data.datasource

import android.content.Context
import com.arekalov.cheater.data.model.AppData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {
    private var cachedData: AppData? = null
    
    suspend fun getAppData(): AppData = withContext(Dispatchers.IO) {
        cachedData ?: run {
            val jsonString = context.assets.open("questions.json")
                .bufferedReader().use { it.readText() }
            json.decodeFromString<AppData>(jsonString).also {
                cachedData = it
            }
        }
    }
}

