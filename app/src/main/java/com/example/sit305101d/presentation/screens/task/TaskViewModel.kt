package com.example.sit305101d.presentation.screens.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305101d.data.network.dto.QuizQuestionDto
import com.example.sit305101d.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.net.URLDecoder

// Re-use or define data classes for task details and questions
data class TaskQuestionUiModel(
    val id: String,
    val questionText: String,
    val description: String?,
    val options: List<String>
)
data class TaskDetailsUiModel(
    val id: String,
    val title: String,
    val description: String,
    val isAiGenerated: Boolean,
    val questions: List<TaskQuestionUiModel>
)

// Represents the state of the Task UI
data class TaskUiState(
    val taskDetails: TaskDetailsUiModel? = null,
    val selectedAnswers: Map<String, Int> = emptyMap(), // Map<QuestionId, SelectedOptionIndex>
    val correctAnswers: Map<String, String> = emptyMap(), // Map<QuestionId, CorrectAnswerLetter>
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false, // Separate flag for submission loading state
    val errorMessage: String? = null,
    val navigateToResults: Boolean = false // Flag to trigger navigation
)

@KoinViewModel
class TaskViewModel(
    private val repository: QuizRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    fun initializeTask(taskId: String, userId: String) {
        Log.d("TaskViewModel", "Initializing task with ID: $taskId")
        // Decode the taskId if it's encoded
        val decodedTaskId = try {
            URLDecoder.decode(taskId, "UTF-8")
        } catch (e: Exception) {
            taskId
        }
        fetchTask(decodedTaskId, userId)
    }

    private fun fetchTask(taskId: String, userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.getQuiz(taskId, userId)
                val questions = response.quiz.mapIndexed { index, question ->
                    TaskQuestionUiModel(
                        id = index.toString(),
                        questionText = question.question,
                        description = null,
                        options = question.options
                    )
                }

                _uiState.update {
                    it.copy(
                        taskDetails = TaskDetailsUiModel(
                            id = taskId,
                            title = "Quiz: $taskId",
                            description = "Test your knowledge of $taskId",
                            isAiGenerated = true,
                            questions = questions
                        ),
                        correctAnswers = response.quiz.associate {
                            it.question to it.correctAnswerLetter
                        },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load quiz: ${e.message}"
                    )
                }
            }
        }
    }

    fun onAnswerSelected(questionId: String, optionIndex: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedAnswers = currentState.selectedAnswers + (questionId to optionIndex)
            )
        }
    }

    fun onSubmitClicked(userId: String) {
        val task = _uiState.value.taskDetails ?: return
        val selectedAnswers = _uiState.value.selectedAnswers
        val correctAnswers = _uiState.value.correctAnswers
        val questions = task.questions

        // Calculate score and collect user answers
        var score = 0
        val userAnswers = mutableListOf<String>()
        val quizQuestions = mutableListOf<QuizQuestionDto>()
        for ((index, question) in questions.withIndex()) {
            val selectedOptionIndex = selectedAnswers[question.id] ?: -1
            val selectedLetter = when (selectedOptionIndex) {
                0 -> "A"
                1 -> "B"
                2 -> "C"
                3 -> "D"
                else -> ""
            }
            userAnswers.add(selectedLetter)
            val correctLetter = correctAnswers[question.questionText] ?: ""
            if (selectedLetter == correctLetter) score++
            quizQuestions.add(
                QuizQuestionDto(
                    question = question.questionText,
                    options = question.options,
                    correctAnswerLetter = correctLetter
                )
            )
        }
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val success = repository.submitQuiz(
                userId = userId,
                topic = task.id,
                score = score,
                totalQuestions = questions.size,
                questions = quizQuestions,
                userAnswers = userAnswers
            )
            if (success) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        navigateToResults = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "Failed to submit quiz. Please try again."
                    )
                }
            }
        }
    }

    fun onSubmissionHandled() {
        _uiState.update { it.copy(navigateToResults = false) }
    }

    fun onErrorMessageHandled() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Dummy data function removed
    // private fun findDummyTaskById(taskId: String): TaskDetailsUiModel? { ... }
}
