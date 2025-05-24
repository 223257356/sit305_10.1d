package com.example.sit305101d.presentation.screens.interests

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.sit305101d.presentation.theme.TextFieldBackgroundColor
import com.example.sit305101d.presentation.theme.TextFieldTextColor
import com.example.sit305101d.presentation.theme.appBackgroundGradient
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsScreen(
    onInterestsSelected: () -> Unit,
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: InterestsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val maxSelection = 10

    // Handle navigation trigger
    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            onInterestsSelected()
            viewModel.onNavigationHandled()
        }
    }

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onErrorMessageHandled()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appBackgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .then(
                    if (!uiState.isLoading) {
                        Modifier.verticalScroll(rememberScrollState())
                    } else {
                        Modifier
                    }
                )
        ) {
            if (uiState.isLoading && uiState.availableInterests.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = "Your Interests",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColorPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You may select up to $maxSelection topics",
                    fontSize = 16.sp,
                    color = TextColorSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(32.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 2
                ) {
                    uiState.availableInterests.forEach { interest ->
                        val isSelected = uiState.selectedInterests.contains(interest.id)
                        FilterChip(
                            selected = isSelected,
                            onClick = { if (!uiState.isLoading) viewModel.onInterestSelected(interest) },
                            label = { Text(interest.name) },
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = TextFieldBackgroundColor,
                                labelColor = TextFieldTextColor,
                                selectedContainerColor = AppPrimaryColor,
                                selectedLabelColor = Color.White,
                                disabledContainerColor = TextFieldBackgroundColor.copy(alpha = 0.5f),
                                disabledLabelColor = TextFieldTextColor.copy(alpha = 0.5f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = !uiState.isLoading,
                                selected = isSelected,
                                borderColor = Color.Gray,
                                selectedBorderColor = Color.Transparent,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 0.dp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.onContinueClicked(userId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = uiState.selectedInterests.isNotEmpty() && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
                ) {
                    if (uiState.isLoading && uiState.availableInterests.isNotEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text("Continue", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InterestsScreenPreview() {
    SIT305101DTheme {
        InterestsScreen(
            onInterestsSelected = {},
            userId = "",
            viewModel = InterestsViewModel()
        )
    }
}
