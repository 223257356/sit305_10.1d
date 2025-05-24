package com.example.sit305101d.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizApiResponse(
    @SerialName("quiz") // Matches the JSON key
    val quiz: List<QuizQuestionDto>
)

@Serializable
data class QuizQuestionDto(
    @SerialName("question")
    val question: String,

    @SerialName("options")
    val options: List<String>, // The JSON returns an array/list of strings

    @SerialName("correct_answer")
    val correctAnswerLetter: String // e.g., "A", "B"
)

@Serializable
data class QuizHistoryResponse(
    val history: List<QuizHistoryItem>
)

@Serializable
data class QuizHistoryItem(
    val id: String? = null, // Make id optional with a default value of null
    val topic: String,
    val score: Int,
    val total_questions: Int,
    val questions: List<QuizQuestionDto>,
    val user_answers: List<String>,
    val timestamp: String
)
