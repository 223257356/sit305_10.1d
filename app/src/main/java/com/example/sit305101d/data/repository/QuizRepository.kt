package com.example.sit305101d.data.repository

import com.example.sit305101d.data.network.LoginResponse
import com.example.sit305101d.data.network.dto.QuizApiResponse
import com.example.sit305101d.data.network.dto.QuizHistoryItem
import com.example.sit305101d.data.network.dto.QuizQuestionDto

interface QuizRepository {
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun getQuiz(topic: String, userId: String): QuizApiResponse
    suspend fun getAvailableTopics(userId: String): List<String>
    suspend fun getQuizHistory(userId: String): List<QuizHistoryItem>
    suspend fun submitQuiz(
        userId: String,
        topic: String,
        score: Int,
        totalQuestions: Int,
        questions: List<QuizQuestionDto>,
        userAnswers: List<String>
    ): Boolean
}
