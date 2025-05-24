package com.example.sit305101d.data.repository

import com.example.sit305101d.data.network.LoginRequest
import com.example.sit305101d.data.network.LoginResponse
import com.example.sit305101d.data.network.RetrofitClient
import com.example.sit305101d.data.network.SubmitQuizRequest
import com.example.sit305101d.data.network.dto.QuizApiResponse
import com.example.sit305101d.data.network.dto.QuizHistoryItem
import com.example.sit305101d.data.network.dto.QuizQuestionDto
import org.koin.core.annotation.Single

@Single(binds = [QuizRepository::class])
class QuizRepositoryImpl : QuizRepository {
    override suspend fun login(username: String, password: String): LoginResponse {
        return RetrofitClient.instance.login(LoginRequest(username, password))
    }

    override suspend fun getQuiz(topic: String, userId: String): QuizApiResponse {
        return RetrofitClient.instance.getQuiz(topic, userId)
    }

    override suspend fun getAvailableTopics(userId: String): List<String> {
        return RetrofitClient.instance.getAvailableTopics(userId).topics
    }

    override suspend fun getQuizHistory(userId: String): List<QuizHistoryItem> {
        return RetrofitClient.instance.getQuizHistory(userId).history
    }

    override suspend fun submitQuiz(
        userId: String,
        topic: String,
        score: Int,
        totalQuestions: Int,
        questions: List<QuizQuestionDto>,
        userAnswers: List<String>
    ): Boolean {
        return try {
            val response = RetrofitClient.instance.submitQuiz(
                SubmitQuizRequest(
                    user_id = userId,
                    topic = topic,
                    score = score,
                    total_questions = totalQuestions,
                    questions = questions,
                    user_answers = userAnswers
                )
            )
            response.message.contains("success", ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }
}
