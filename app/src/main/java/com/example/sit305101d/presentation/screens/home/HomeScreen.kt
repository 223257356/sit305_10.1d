package com.example.sit305101d.presentation.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sit305101d.data.network.LoginResponse
import com.example.sit305101d.data.network.dto.QuizApiResponse
import com.example.sit305101d.data.network.dto.QuizHistoryItem
import com.example.sit305101d.data.network.dto.QuizQuestionDto
import com.example.sit305101d.data.repository.QuizRepository
import com.example.sit305101d.presentation.theme.AppPrimaryColor
import com.example.sit305101d.presentation.theme.SIT305101DTheme
import com.example.sit305101d.presentation.theme.TextColorPrimary
import com.example.sit305101d.presentation.theme.TextColorSecondary
import com.example.sit305101d.presentation.theme.appBackgroundGradient
import java.net.URLEncoder

@Composable
fun HomeScreen(
    onNavigateToTask: (taskTitle: String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel, // Remove default value to make it required
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorMessageHandled() // Reset the error message
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appBackgroundGradient) // Apply gradient
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp)) // Space for status bar

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Hello,",
                        fontSize = 28.sp,
                        color = TextColorSecondary
                    )
                    // Use user name from state, provide default if empty
                    Text(
                        text = uiState.userName.ifEmpty { "User" },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColorPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Filled.AccountCircle, // Replace with profile pic later
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(onClick = onProfileClick),
                    tint = TextColorPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Task Due Notification (Calculate from state)
            val tasksDueCount = uiState.tasks.count { !it.isComplete }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Notifications, // Bell Icon
                    contentDescription = "Tasks Due Notification",
                    tint = TextColorPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You have $tasksDueCount task${if (tasksDueCount != 1) "s" else ""} due",
                    color = TextColorPrimary,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Task List or Loading Indicator
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(
                        uiState.tasks,
                        key = { it.id }
                    ) { task -> // Use key for better performance
                        TaskCard(task = task, onClick = { onNavigateToTask(it) })
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) } // Padding at bottom
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: HomeTaskUiModel, // Updated data class
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                // URL encode the topic name to handle special characters
                val encodedTitle = URLEncoder.encode(task.title, "UTF-8")
                onClick(encodedTitle)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x4DFFFFFF)) // Semi-transparent white card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (task.isAiGenerated) {
                    Text(
                        text = "✨ Generated by AI", // Simple AI indicator
                        fontSize = 12.sp,
                        color = TextColorSecondary
                    )
                }
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColorPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    fontSize = 14.sp,
                    color = TextColorSecondary,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Status Indicator (Green circle like mockup)
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(if (task.isComplete) Color.Gray else AppPrimaryColor, CircleShape)
            ) // Simple colored circle. Add icons/progress later if needed.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SIT305101DTheme {
        // Create a mock HomeViewModel for preview
        val mockViewModel = HomeViewModel(
            repository = object : QuizRepository {
                override suspend fun login(username: String, password: String): LoginResponse =
                    throw NotImplementedError("Preview only")

                override suspend fun getQuiz(topic: String, userId: String): QuizApiResponse =
                    throw NotImplementedError("Preview only")

                override suspend fun getAvailableTopics(userId: String): List<String> =
                    listOf()

                override suspend fun getQuizHistory(userId: String): List<QuizHistoryItem> =
                    listOf()

                override suspend fun submitQuiz(
                    userId: String,
                    topic: String,
                    score: Int,
                    totalQuestions: Int,
                    questions: List<QuizQuestionDto>,
                    userAnswers: List<String>
                ): Boolean = false
            }
        )

        HomeScreen(
            viewModel = mockViewModel,
            onNavigateToTask = {},
            onProfileClick = {}
        )
    }
}

// Preview for TaskCard uses the new model
@Preview(showBackground = true)
@Composable
fun TaskCardPreview() {
    val previewTask1 =
        HomeTaskUiModel(
            id = "p1",
            title = "Preview Task 1",
            description = "Desc 1",
            isAiGenerated = true,
            isComplete = false
        )
    val previewTask2 =
        HomeTaskUiModel(
            id = "p2",
            title = "Preview Task 2",
            description = "Desc 2",
            isAiGenerated = false,
            isComplete = true
        )
    SIT305101DTheme {
        Column(Modifier.padding(16.dp)) {
            TaskCard(task = previewTask1, onClick = {})
            Spacer(modifier = Modifier.height(16.dp))
            TaskCard(task = previewTask2, onClick = {})
        }
    }
}
