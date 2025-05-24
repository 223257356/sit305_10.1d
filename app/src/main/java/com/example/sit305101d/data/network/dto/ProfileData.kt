package com.example.sit305101d.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileData(
    val username: String,
    val email: String?,
    val phone: String?,
    val interests: List<String>,
    val created_at: String,
    val quizzes_done: Int,
    val correct_answers: Int,
    val incorrect_answers: Int
)
