package com.example.sit305101d.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305101d.data.network.dto.QuizHistoryItem
import com.example.sit305101d.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

// Data class for user profile
data class UserProfileState(
    val username: String = "",
    val email: String? = null,
    val phone: String? = null,
    val interests: List<String> = emptyList(),
    val created_at: String = "",
    val quizzes_done: Int = 0,
    val correct_answers: Int = 0,
    val incorrect_answers: Int = 0
)

@KoinViewModel
class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfileState())
    val userProfile: StateFlow<UserProfileState> = _userProfile.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentUserId: String = ""

    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }

    fun getCurrentUserId(): String = currentUserId

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.getProfile(currentUserId)
                _userProfile.value = UserProfileState(
                    username = profile.username,
                    email = profile.email,
                    phone = profile.phone,
                    interests = profile.interests,
                    created_at = profile.created_at,
                    quizzes_done = profile.quizzes_done,
                    correct_answers = profile.correct_answers,
                    incorrect_answers = profile.incorrect_answers
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            }
        }
    }

    fun setUserProfile(profile: UserProfileState) {
        _userProfile.value = profile
    }

    fun updateStatsFromHistory(history: List<QuizHistoryItem>) {
        val totalQuizzes = history.size
        val correctAnswers = history.sumOf { it.score }
        val incorrectAnswers = history.sumOf { it.total_questions - it.score }

        _userProfile.value = _userProfile.value.copy(
            quizzes_done = totalQuizzes,
            correct_answers = correctAnswers,
            incorrect_answers = incorrectAnswers
        )
    }

    fun clearError() {
        _error.value = null
    }
}
