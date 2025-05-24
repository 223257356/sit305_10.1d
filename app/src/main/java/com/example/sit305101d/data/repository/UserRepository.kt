package com.example.sit305101d.data.repository

import androidx.core.content.edit
import com.example.sit305101d.data.local.UserDataPref
import com.example.sit305101d.data.model.User
import com.example.sit305101d.data.network.RetrofitClient
import com.example.sit305101d.data.network.dto.ProfileData
import com.example.sit305101d.data.network.dto.QuizHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class UserRepository(
    private val userDataPref: UserDataPref,
    private val jsonParser: Json
) {
    private val apiService = RetrofitClient.instance

    suspend fun getProfile(userId: String): ProfileData = withContext(Dispatchers.IO) {
        apiService.getProfile(userId).profile
    }

    suspend fun getQuizHistory(userId: String): List<QuizHistoryItem> = withContext(Dispatchers.IO) {
        apiService.getQuizHistory(userId).history
    }

    fun saveLocalUser(user: User) {
        val json = jsonParser.encodeToString(user)
        userDataPref.edit { putString("user", json) }
    }

    fun getLocalUser(): Result<User> {
        return userDataPref.runCatching {
            requireNotNull(jsonParser.decodeFromString(getString("user", null).toString()))
        }
    }
}
