package com.example.sit305101d.presentation.screens.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305101d.data.network.RetrofitClient
import com.example.sit305101d.data.network.UpdateInterestsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

data class Interest(
    val id: String,
    val name: String
)

// Represents the state of the Interests UI
data class InterestsUiState(
    val availableInterests: List<Interest> = emptyList(),
    val selectedInterests: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToHome: Boolean = false // Flag to trigger navigation
)

@KoinViewModel
class InterestsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InterestsUiState())
    val uiState: StateFlow<InterestsUiState> = _uiState.asStateFlow()

    init {
        loadInterests()
    }

    private fun loadInterests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // TODO: Replace with actual API call
                // For now, using dummy data
                val dummyInterests = listOf(
                    Interest("1", "Android Development"),
                    Interest("2", "iOS Development"),
                    Interest("3", "Web Development"),
                    Interest("4", "Machine Learning"),
                    Interest("5", "Data Science"),
                    Interest("6", "Cloud Computing"),
                    Interest("7", "Cybersecurity"),
                    Interest("8", "Game Development"),
                    Interest("9", "UI/UX Design"),
                    Interest("10", "Mobile App Design")
                )
                _uiState.update {
                    it.copy(
                        availableInterests = dummyInterests,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load interests: ${e.message}"
                    )
                }
            }
        }
    }

    fun onInterestSelected(interest: Interest) {
        _uiState.update { currentState ->
            val newSelectedInterests = currentState.selectedInterests.toMutableSet()
            if (interest.id in newSelectedInterests) {
                newSelectedInterests.remove(interest.id)
            } else {
                if (newSelectedInterests.size < 10) {
                    newSelectedInterests.add(interest.id)
                }
            }
            currentState.copy(selectedInterests = newSelectedInterests)
        }
    }

    fun onContinueClicked(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val selectedInterestNames = _uiState.value.availableInterests
                    .filter { _uiState.value.selectedInterests.contains(it.id) }
                    .map { it.name }
                RetrofitClient.instance.updateInterests(
                    UpdateInterestsRequest(
                        user_id = userId,
                        interests = selectedInterestNames
                    )
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigateToHome = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to save interests: ${e.message}"
                    )
                }
            }
        }
    }

    // Call this after navigation has occurred
    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToHome = false) }
    }

    // Call this after error message has been shown
    fun onErrorMessageHandled() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
