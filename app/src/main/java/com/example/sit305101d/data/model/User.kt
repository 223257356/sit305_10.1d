package com.example.sit305101d.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val email: String? = null // Add other relevant fields as needed
)
