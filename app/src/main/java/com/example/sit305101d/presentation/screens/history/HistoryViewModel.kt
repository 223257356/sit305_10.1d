package com.example.sit305101d.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305101d.data.network.dto.QuizHistoryItem
import com.example.sit305101d.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class HistoryUiState(
    val quizHistory: List<QuizHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@KoinViewModel
class HistoryViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun setCurrentUserId(userId: String) {
        currentUserId = userId
        // Load history when user ID is set
        loadQuizHistory(userId)
    }

    fun getCurrentUserId(): String = currentUserId

    fun loadQuizHistory(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val history = repository.getQuizHistory(userId)
                _uiState.value = _uiState.value.copy(
                    quizHistory = history,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load quiz history"
                )
            }
        }
    }

    fun onErrorMessageHandled() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
