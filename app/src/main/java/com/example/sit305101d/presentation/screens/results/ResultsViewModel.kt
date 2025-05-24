package com.example.sit305101d.presentation.screens.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305101d.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class ResultItemUiModel(
    val questionNumber: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerLetter: String
)

data class ResultsUiState(
    val quizTitle: String = "",
    val results: List<ResultItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@KoinViewModel
class ResultsViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    private var topic: String = ""
    private var username: String = ""

    fun initializeResults(taskId: String, userId: String) {
        if (taskId.isBlank() || userId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Missing topic or user ID") }
            return
        }
        if (this.topic == taskId && this.username == userId) return
        this.topic = taskId
        this.username = userId
        loadResultsData(taskId, userId)
    }

    private fun loadResultsData(topicToLoad: String, userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val history = repository.getQuizHistory(userId)
                val lastQuiz = history.findLast { it.topic == topicToLoad }
                if (lastQuiz != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            quizTitle = "Results: ${lastQuiz.topic}",
                            results = lastQuiz.questions.mapIndexed { idx, q ->
                                ResultItemUiModel(
                                    questionNumber = idx + 1,
                                    questionText = q.question,
                                    options = q.options,
                                    correctAnswerLetter = q.correctAnswerLetter
                                )
                            }
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "No results found for this quiz.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load results: ${e.message}") }
            }
        }
    }

    fun onErrorMessageHandled() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
