package com.example.sit305101d.data.network

import com.example.sit305101d.data.network.dto.ProfileData
import com.example.sit305101d.data.network.dto.QuizApiResponse
import com.example.sit305101d.data.network.dto.QuizHistoryItem
import com.example.sit305101d.data.network.dto.QuizHistoryResponse
import com.example.sit305101d.data.network.dto.QuizQuestionDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("getQuiz")
    suspend fun getQuiz(
        @Query("topic") topic: String,
        @Query("user_id") userId: String
    ): QuizApiResponse

    @GET("getAvailableTopics")
    suspend fun getAvailableTopics(
        @Query("user_id") userId: String
    ): TopicsResponse

    @POST("updateInterests")
    suspend fun updateInterests(
        @Body request: UpdateInterestsRequest
    ): UpdateInterestsResponse

    @GET("getQuizHistory")
    suspend fun getQuizHistory(
        @Query("user_id") userId: String
    ): QuizHistoryResponse

    @POST("submitQuiz")
    suspend fun submitQuiz(
        @Body request: SubmitQuizRequest
    ): SubmitQuizResponse

    @GET("getPerformance")
    suspend fun getPerformance(
        @Query("user_id") userId: String,
        @Query("topic") topic: String?
    ): PerformanceResponse

    @GET("getPendingTopics")
    suspend fun getPendingTopics(
        @Query("user_id") userId: String
    ): PendingTopicsResponse

    @GET("profile")
    suspend fun getProfile(
        @Query("user_id") userId: String
    ): ProfileResponse

    // Add other endpoints here later (e.g., login, signup, submit) if they are created
}

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String? = null,
    val phone: String? = null
)

@Serializable
data class RegisterResponse(
    val message: String
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val message: String,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    val username: String,
    val email: String?,
    val interests: List<String>,
    val isPremium: Boolean = false
)

@Serializable
data class TopicsResponse(
    @SerialName("topics")
    val topics: List<String>
)

@Serializable
data class UpdateInterestsRequest(
    val user_id: String,
    val interests: List<String>
)

@Serializable
data class UpdateInterestsResponse(
    val message: String
)

@Serializable
data class SubmitQuizRequest(
    val user_id: String,
    val topic: String,
    val score: Int,
    val total_questions: Int,
    val questions: List<QuizQuestionDto>,
    val user_answers: List<String>
)

@Serializable
data class SubmitQuizResponse(
    val message: String
)

@Serializable
data class PerformanceResponse(
    val performance: List<QuizHistoryItem>
)

@Serializable
data class PendingTopicsResponse(
    val pending_topics: List<String>
)

@Serializable
data class ProfileResponse(
    val profile: ProfileData
)
