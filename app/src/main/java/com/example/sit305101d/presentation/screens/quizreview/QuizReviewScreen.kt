package com.example.sit305101d.presentation.screens.quizreview

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sit305101d.data.network.dto.QuizHistoryItem
import com.example.sit305101d.data.network.dto.QuizQuestionDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizReviewScreen(
    historyItem: QuizHistoryItem,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("QuizReviewScreen", "Displaying quiz: ${historyItem.topic}")
    Log.d("QuizReviewScreen", "Questions: ${historyItem.questions.size}")
    Log.d("QuizReviewScreen", "User answers: ${historyItem.user_answers}")

    Scaffold(

        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Quiz Review") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Score summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = historyItem.topic,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Score: ${historyItem.score}/${historyItem.total_questions}",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }

            // Questions list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(historyItem.questions) { index, question ->
                    QuestionReviewCard(
                        questionNumber = index + 1,
                        question = question,
                        userAnswer = historyItem.user_answers.getOrNull(index)
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionReviewCard(
    questionNumber: Int,
    question: QuizQuestionDto,
    userAnswer: String?,
    modifier: Modifier = Modifier
) {
    Log.d("QuestionReviewCard", "Question $questionNumber: ${question.question}")
    Log.d("QuestionReviewCard", "Options: ${question.options}")
    Log.d("QuestionReviewCard", "Correct Answer: ${question.correctAnswerLetter}")
    Log.d("QuestionReviewCard", "User Answer: $userAnswer")

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Question $questionNumber",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question.question,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Options
            question.options.forEachIndexed { index, option ->
                val optionLetter = ('A' + index).toString()
                val correctLetter = question.correctAnswerLetter.trim().substringBefore(".")
                    .substringBefore(" ")
                val isCorrect = optionLetter == correctLetter
                val isUserAnswer = optionLetter == userAnswer

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            when {
                                isCorrect && isUserAnswer -> Color(0xFFE8F5E9) // Light green for correct answer
                                isUserAnswer -> Color(0xFFFFEBEE) // Light red for incorrect answer
                                else -> Color.Transparent
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$optionLetter.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(32.dp),
                            color = Color.Black
                        )
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            color = Color.Black
                        )
                    }
                    if (isCorrect || isUserAnswer) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = if (isCorrect) "Correct" else "Incorrect",
                            tint = if (isCorrect) Color.Green else Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                if (index < question.options.size - 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
