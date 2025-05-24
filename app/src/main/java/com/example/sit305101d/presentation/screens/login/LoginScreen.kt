package com.example.sit305101d.presentation.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sit305101d.presentation.theme.AppPrimaryColor
import com.example.sit305101d.presentation.theme.LinkColor
import com.example.sit305101d.presentation.theme.TextColorPrimary
import com.example.sit305101d.presentation.theme.TextColorSecondary
import com.example.sit305101d.presentation.theme.TextFieldBackgroundColor
import com.example.sit305101d.presentation.theme.TextFieldPlaceholderColor
import com.example.sit305101d.presentation.theme.TextFieldTextColor
import com.example.sit305101d.presentation.theme.appBackgroundGradient
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess(uiState.username)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
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
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, Student!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextColorPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Lets Start Learning!",
                fontSize = 18.sp,
                color = TextColorSecondary
            )
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = { viewModel.updateUsername(it) },
                label = { Text("Username", color = TextFieldPlaceholderColor) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppPrimaryColor,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = AppPrimaryColor,
                    focusedTextColor = TextFieldTextColor,
                    unfocusedTextColor = TextFieldTextColor,
                    focusedContainerColor = TextFieldBackgroundColor,
                    unfocusedContainerColor = TextFieldBackgroundColor
                ),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Password", color = TextFieldPlaceholderColor) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppPrimaryColor,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = AppPrimaryColor,
                    focusedTextColor = TextFieldTextColor,
                    unfocusedTextColor = TextFieldTextColor,
                    focusedContainerColor = TextFieldBackgroundColor,
                    unfocusedContainerColor = TextFieldBackgroundColor
                ),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text("Login", color = Color.White, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = onNavigateToSignUp,
                enabled = !uiState.isLoading
            ) {
                Text("Need an Account?", color = LinkColor)
            }
        }
    }
}
