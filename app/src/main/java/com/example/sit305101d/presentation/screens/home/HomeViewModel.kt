package com.example.sit305101d.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305101d.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

// Re-use or define data class for tasks displayed on Home screen
data class HomeTaskUiModel(
    val id: String,
    val title: String,
    val description: String,
    val isComplete: Boolean = false,
    val isAiGenerated: Boolean = false
)

// Represents the state of the Home UI
data class HomeUiState(
    val userName: String = "",
    val tasks: List<HomeTaskUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@KoinViewModel
class HomeViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun initializeHome(username: String) {
        Log.d("HomeViewModel", "Initializing home with username: $username")
        _uiState.update { it.copy(userName = username) }
        loadTasks(username)
    }

    private fun loadTasks(userId: String) {
        Log.d("HomeViewModel", "Loading tasks for user: $userId")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                Log.d("HomeViewModel", "Fetching available topics from repository")
                val topics = repository.getAvailableTopics(userId)
                Log.d("HomeViewModel", "Received topics: $topics")
                _uiState.update {
                    it.copy(
                        tasks = topics.mapIndexed { index, topic ->
                            HomeTaskUiModel(
                                id = (index + 1).toString(),
                                title = topic,
                                description = "Test your knowledge of $topic",
                                isAiGenerated = true
                            )
                        },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading tasks", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load tasks: ${e.message}"
                    )
                }
            }
        }
    }

    // Call this after error message has been shown
    fun onErrorMessageHandled() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
