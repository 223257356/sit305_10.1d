package com.example.sit305101d.presentation.screens.results

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sit305101d.presentation.theme.AppPrimaryColor
import com.example.sit305101d.presentation.theme.SIT305101DTheme
import com.example.sit305101d.presentation.theme.TextColorPrimary
import com.example.sit305101d.presentation.theme.TextColorSecondary
import com.example.sit305101d.presentation.theme.appBackgroundGradient

@Composable
fun ResultsScreen(
    onContinue: () -> Unit,
    viewModel: ResultsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
            .background(appBackgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 32.dp, bottom = 16.dp)
            ) {
                // Header
                item {
                    Text(
                        text = "✨ Quiz Review", // Changed header slightly
                        fontSize = 14.sp,
                        color = TextColorSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = uiState.quizTitle.ifEmpty { "Quiz Results" },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColorPrimary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Loading Indicator or Results List
                if (uiState.isLoading) {
                    item { // Display loading within the LazyColumn bounds
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (uiState.results.isEmpty()) {
                    item {
                        Text(
                            "No results available.",
                            color = TextColorSecondary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    itemsIndexed(uiState.results, key = { _, item -> item.questionNumber }) { _, result ->
                        ResultItemCard(result = result)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Continue Button Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
                ) {
                    Text("Continue", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ResultItemCard(result: ResultItemUiModel, modifier: Modifier = Modifier) {
    // Map Answer Letter ('A', 'B', 'C', 'D') to Index (0, 1, 2, 3)
    val correctAnswerIndex = when (result.correctAnswerLetter.uppercase()) {
        "A" -> 0
        "B" -> 1
        "C" -> 2
        "D" -> 3
        else -> -1 // Handle unexpected letter
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x4DFFFFFF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${result.questionNumber}. ${result.questionText}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextColorPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display options, highlighting the correct one
            result.options.forEachIndexed { index, option ->
                val isCorrect = index == correctAnswerIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            color = if (isCorrect) AppPrimaryColor.copy(alpha = 0.3f) else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${('A' + index)}. $option", // Show A, B, C, D
                        color = TextColorPrimary,
                        fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultsScreenPreview() {
    val previewResult = ResultItemUiModel(1, "Preview Q1", listOf("Opt A", "Opt B Correct", "Opt C"), "B")
    SIT305101DTheme {
        Column(Modifier.background(appBackgroundGradient).padding(16.dp)) {
            ResultItemCard(result = previewResult)
        }
    }
}
