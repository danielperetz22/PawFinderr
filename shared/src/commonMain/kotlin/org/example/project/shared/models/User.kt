package org.example.project.shared.models

data class User(
    val id: String,
    val email: String,
    val displayName: String? = null
)