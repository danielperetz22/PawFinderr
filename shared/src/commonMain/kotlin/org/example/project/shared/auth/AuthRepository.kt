package org.example.project.shared.auth

import org.example.project.shared.models.User

interface AuthRepository {
    suspend fun register(email: String, password: String): User
    suspend fun login(email: String, password: String): User
    suspend fun getCurrentUser(): User?
}
