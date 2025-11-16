package com.arekalov.cheater.data.search

import com.arekalov.cheater.data.model.Question
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class SearchEngine @Inject constructor() {
    
    /**
     * Продвинутый поиск с поддержкой:
     * - Fuzzy search (поиск с опечатками)
     * - Поиск по нескольким словам
     * - Ранжирование по релевантности
     * - Игнорирование знаков препинания
     */
    fun search(questions: List<Question>, query: String): List<Question> {
        if (query.isBlank()) return emptyList()
        
        val normalizedQuery = normalizeText(query)
        val queryWords = normalizedQuery.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        
        // Быстрая предварительная фильтрация - только точные совпадения
        val candidateQuestions = questions.filter { question ->
            val questionText = normalizeText(question.text)
            val normalizedKeywords = question.keywords.map { normalizeText(it) }
            
            // Хотя бы одно слово из запроса должно точно совпасть
            queryWords.any { word ->
                questionText.contains(word) || 
                normalizedKeywords.any { it.contains(word) }
            }
        }
        
        // Если найдено слишком много кандидатов, используем только точный поиск
        val useFullScoring = candidateQuestions.size <= 500
        
        // Вычисляем релевантность только для кандидатов
        val scoredQuestions = candidateQuestions.mapNotNull { question ->
            val score = if (useFullScoring) {
                calculateRelevanceScore(question, normalizedQuery, queryWords)
            } else {
                calculateFastRelevanceScore(question, normalizedQuery, queryWords)
            }
            if (score > 0) {
                ScoredQuestion(question, score)
            } else {
                null
            }
        }
        
        // Сортируем по убыванию релевантности
        return scoredQuestions
            .sortedByDescending { it.score }
            .map { it.question }
    }
    
    /**
     * Нормализует текст: lowercase + удаление знаков препинания
     */
    private fun normalizeText(text: String): String {
        return text.lowercase()
            .trim()
            .replace(Regex("[^а-яёa-z0-9\\s]"), "") // Оставляем только буквы, цифры и пробелы
            .replace(Regex("\\s+"), " ") // Множественные пробелы заменяем на один
    }
    
    /**
     * Быстрый расчёт релевантности без fuzzy search
     */
    private fun calculateFastRelevanceScore(
        question: Question,
        fullQuery: String,
        queryWords: List<String>
    ): Double {
        val questionText = normalizeText(question.text)
        var score = 0.0
        
        // 1. Точное совпадение полного запроса
        if (questionText.contains(fullQuery)) {
            score += 100.0
        }
        
        // 2. Проверка каждого слова из запроса
        for (word in queryWords) {
            // Точное совпадение слова в тексте вопроса
            if (questionText.contains(word)) {
                score += 50.0
            }
            
            // Точное совпадение в ключевых словах
            val exactKeywordMatch = question.keywords.any { 
                normalizeText(it).contains(word) 
            }
            if (exactKeywordMatch) {
                score += 40.0
            }
        }
        
        // 3. Бонус за совпадение всех слов
        val allWordsFound = queryWords.all { word ->
            questionText.contains(word) || 
            question.keywords.any { normalizeText(it).contains(word) }
        }
        if (allWordsFound && queryWords.size > 1) {
            score += 30.0
        }
        
        // 4. Бонус за совпадение в начале текста
        if (questionText.startsWith(fullQuery) || 
            queryWords.isNotEmpty() && questionText.startsWith(queryWords[0])) {
            score += 20.0
        }
        
        return score
    }
    
    private fun calculateRelevanceScore(
        question: Question,
        fullQuery: String,
        queryWords: List<String>
    ): Double {
        val questionText = normalizeText(question.text)
        var score = 0.0
        
        // 1. Точное совпадение полного запроса (максимальный вес)
        if (questionText.contains(fullQuery)) {
            score += 100.0
        }
        
        // 2. Проверка каждого слова из запроса
        for (word in queryWords) {
            // Пропускаем очень короткие слова для fuzzy search
            if (word.length < 4) {
                // Только точное совпадение для коротких слов
                if (questionText.contains(word)) {
                    score += 50.0
                }
                val exactKeywordMatch = question.keywords.any { 
                    normalizeText(it).contains(word) 
                }
                if (exactKeywordMatch) {
                    score += 40.0
                }
                continue
            }
            
            // 2.1 Точное совпадение слова в тексте вопроса
            if (questionText.contains(word)) {
                score += 50.0
            }
            
            // 2.2 Точное совпадение в ключевых словах (высокий приоритет)
            val exactKeywordMatch = question.keywords.any { 
                normalizeText(it).contains(word) 
            }
            if (exactKeywordMatch) {
                score += 40.0
            }
            
            // Fuzzy search только если нет точного совпадения
            if (!questionText.contains(word) && !exactKeywordMatch) {
                // 2.3 Fuzzy match в тексте вопроса (ограниченный)
                val fuzzyTextScore = findBestFuzzyMatchOptimized(word, questionText)
                if (fuzzyTextScore > 0.75) {
                    score += fuzzyTextScore * 30.0
                }
                
                // 2.4 Fuzzy match в ключевых словах (ограниченный)
                val fuzzyKeywordScore = question.keywords
                    .filter { it.length >= word.length - 2 && it.length <= word.length + 2 }
                    .maxOfOrNull { keyword ->
                        levenshteinSimilarity(word, normalizeText(keyword))
                    } ?: 0.0
                if (fuzzyKeywordScore > 0.75) {
                    score += fuzzyKeywordScore * 25.0
                }
            }
        }
        
        // 3. Бонус за совпадение всех слов из запроса
        val allWordsFound = queryWords.all { word ->
            questionText.contains(word) || 
            question.keywords.any { normalizeText(it).contains(word) }
        }
        if (allWordsFound && queryWords.size > 1) {
            score += 30.0
        }
        
        // 4. Бонус за совпадение в начале текста
        if (questionText.startsWith(fullQuery) || 
            queryWords.isNotEmpty() && questionText.startsWith(queryWords[0])) {
            score += 20.0
        }
        
        return score
    }
    
    /**
     * Оптимизированная версия fuzzy match - только для слов похожей длины
     */
    private fun findBestFuzzyMatchOptimized(word: String, text: String): Double {
        val textWords = text.split("\\s+".toRegex())
        return textWords
            .filter { it.length >= word.length - 2 && it.length <= word.length + 2 }
            .take(50) // Ограничиваем количество проверяемых слов
            .maxOfOrNull { textWord ->
                levenshteinSimilarity(word, textWord)
            } ?: 0.0
    }
    
    /**
     * Находит лучшее fuzzy совпадение слова в тексте
     */
    private fun findBestFuzzyMatch(word: String, text: String): Double {
        val textWords = text.split("\\s+".toRegex())
        return textWords.maxOfOrNull { textWord ->
            levenshteinSimilarity(word, textWord)
        } ?: 0.0
    }
    
    /**
     * Вычисляет схожесть двух строк используя расстояние Левенштейна
     * Возвращает значение от 0.0 (совсем не похожи) до 1.0 (идентичны)
     */
    private fun levenshteinSimilarity(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        if (s1.isEmpty() || s2.isEmpty()) return 0.0
        
        val distance = levenshteinDistance(s1, s2)
        val maxLen = max(s1.length, s2.length)
        return 1.0 - (distance.toDouble() / maxLen)
    }
    
    /**
     * Вычисляет расстояние Левенштейна между двумя строками
     * (минимальное количество операций вставки/удаления/замены)
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length
        
        // Оптимизация: если строки слишком разные по длине
        if (kotlin.math.abs(len1 - len2) > max(len1, len2) / 2) {
            return max(len1, len2)
        }
        
        val dp = Array(len1 + 1) { IntArray(len2 + 1) }
        
        for (i in 0..len1) dp[i][0] = i
        for (j in 0..len2) dp[0][j] = j
        
        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // удаление
                    dp[i][j - 1] + 1,      // вставка
                    dp[i - 1][j - 1] + cost // замена
                )
            }
        }
        
        return dp[len1][len2]
    }
    
    private data class ScoredQuestion(
        val question: Question,
        val score: Double
    )
}

