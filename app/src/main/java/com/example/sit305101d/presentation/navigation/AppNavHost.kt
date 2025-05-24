package com.example.sit305101d.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sit305101d.data.repository.QuizRepositoryImpl
import com.example.sit305101d.data.repository.UserRepository
import com.example.sit305101d.presentation.screens.history.HistoryScreen
import com.example.sit305101d.presentation.screens.history.HistoryViewModel
import com.example.sit305101d.presentation.screens.home.HomeScreen
import com.example.sit305101d.presentation.screens.home.HomeViewModel
import com.example.sit305101d.presentation.screens.interests.InterestsScreen
import com.example.sit305101d.presentation.screens.login.LoginScreen
import com.example.sit305101d.presentation.screens.profile.ProfileScreen
import com.example.sit305101d.presentation.screens.profile.UserViewModel
import com.example.sit305101d.presentation.screens.quizreview.QuizReviewScreen
import com.example.sit305101d.presentation.screens.quizreview.QuizReviewViewModel
import com.example.sit305101d.presentation.screens.results.ResultsScreen
import com.example.sit305101d.presentation.screens.results.ResultsViewModel
import com.example.sit305101d.presentation.screens.signup.SignUpScreen
import com.example.sit305101d.presentation.screens.task.TaskScreen
import com.example.sit305101d.presentation.screens.task.TaskViewModel
import com.example.sit305101d.presentation.screens.upgrade.UpgradeScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.net.URLDecoder

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object Interests : Screen("interests")
    data object Home : Screen("home")
    data object Task : Screen("task/{taskId}") {
        fun createRoute(taskId: String) = "task/$taskId"
    }
    data object Results : Screen("results/{taskId}/{userId}") {
        fun createRoute(taskId: String, userId: String) = "results/$taskId/$userId"
    }
    data object Profile : Screen("profile")
    data object History : Screen("history")
    data object QuizReview : Screen("quiz_review")
    data object Upgrade : Screen("upgrade")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    userViewModel: UserViewModel = koinViewModel(),
    historyViewModel: HistoryViewModel = koinViewModel(),
    quizReviewViewModel: QuizReviewViewModel = koinViewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onLoginSuccess = { username ->
                    userViewModel.setCurrentUserId(username)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateBack = { navController.navigateUp() },
                onSignUpSuccess = {
                    navController.navigate(Screen.Interests.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Interests.route) {
            val userId = koinInject<UserRepository>().getLocalUser().getOrNull()?.username.orEmpty()

            InterestsScreen(
                userId = userId,
                onInterestsSelected = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Interests.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            val homeViewModel = remember {
                HomeViewModel(QuizRepositoryImpl())
            }

            LaunchedEffect(Unit) {
                val username = userViewModel.getCurrentUserId()
                if (username.isNotEmpty()) {
                    homeViewModel.initializeHome(username)
                }
            }

            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToTask = { taskId ->
                    Log.d("AppNavHost", "Navigating to TaskScreen with taskId: $taskId")
                    navController.navigate(Screen.Task.createRoute(taskId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(
            route = Screen.Task.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            val taskViewModel = remember {
                TaskViewModel(QuizRepositoryImpl())
            }
            val userId = koinInject<UserRepository>().getLocalUser().getOrNull()?.username.orEmpty()

            LaunchedEffect(taskId) {
                taskViewModel.initializeTask(taskId, userId)
            }

            TaskScreen(
                viewModel = taskViewModel,
                onSubmit = { encodedTaskId ->
                    navController.navigate(Screen.Results.createRoute(encodedTaskId, userId))
                },
                userId = userId
            )
        }
        composable(
            route = Screen.Results.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val resultsViewModel = remember {
                ResultsViewModel(QuizRepositoryImpl())
            }

            // Decode the task ID if it's encoded
            val decodedTaskId = try {
                URLDecoder.decode(taskId, "UTF-8")
            } catch (e: Exception) {
                taskId
            }

            LaunchedEffect(decodedTaskId, userId) {
                resultsViewModel.initializeResults(decodedTaskId, userId)
            }

            ResultsScreen(
                viewModel = resultsViewModel,
                onContinue = { navController.navigate(Screen.Home.route) }
            )
        }
        composable(Screen.History.route) {
            LaunchedEffect(Unit) {
                historyViewModel.setCurrentUserId(userViewModel.getCurrentUserId())
            }
            HistoryScreen(
                historyViewModel = historyViewModel,
                onQuizClick = { quizId ->
                    Log.d("AppNavHost", "Quiz clicked with ID: $quizId")
                    // Find the selected quiz history item by ID or by matching the fallback ID format
                    val selectedQuiz = historyViewModel.uiState.value.quizHistory.find { historyItem ->
                        val matches = historyItem.id == quizId ||
                            "${historyItem.topic}_${historyItem.timestamp}" == quizId
                        Log.d(
                            "AppNavHost",
                            "Checking quiz: ${historyItem.topic}, id: ${historyItem.id}, matches: $matches"
                        )
                        matches
                    }
                    if (selectedQuiz != null) {
                        Log.d("AppNavHost", "Found quiz: ${selectedQuiz.topic}")
                        quizReviewViewModel.setSelectedHistoryItem(selectedQuiz)
                        navController.navigate(Screen.QuizReview.route)
                    } else {
                        Log.e("AppNavHost", "Quiz not found for ID: $quizId")
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                userViewModel = userViewModel,
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToUpgrade = {
                    navController.navigate(Screen.Upgrade.route)
                }
            )
        }
        composable(Screen.Upgrade.route) {
            UpgradeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.QuizReview.route) {
            val selectedQuiz by quizReviewViewModel.selectedHistoryItem.collectAsState()
            selectedQuiz?.let { quiz ->
                QuizReviewScreen(
                    historyItem = quiz,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
