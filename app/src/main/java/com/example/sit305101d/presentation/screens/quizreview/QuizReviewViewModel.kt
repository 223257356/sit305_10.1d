package com.example.sit305101d.presentation.screens.quizreview

import androidx.lifecycle.ViewModel
import com.example.sit305101d.data.network.dto.QuizHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class QuizReviewViewModel : ViewModel() {
    private val _selectedHistoryItem = MutableStateFlow<QuizHistoryItem?>(null)
    val selectedHistoryItem: StateFlow<QuizHistoryItem?> = _selectedHistoryItem.asStateFlow()

    fun setSelectedHistoryItem(item: QuizHistoryItem) {
        _selectedHistoryItem.value = item
    }
}
