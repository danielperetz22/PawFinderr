package org.example.project.data.firebase
interface FirebaseRepository {
    suspend fun signUp(email: String, password: String)
    suspend fun signIn(email: String, password: String)
    fun currentUserUid(): String?
    suspend fun saveUserProfile(uid: String, email: String)
    suspend fun signOut()
    fun currentUserEmail(): String?
    suspend fun updatePassword(newPassword: String)
}

