// commonMain
package org.example.project.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.firestore.*
import dev.gitlive.firebase.initialize

class RemoteFirebaseRepository : FirebaseRepository {

    override suspend fun signUp(email: String, password: String) {
        // יוצר חשבון חדש ב‑Firebase Auth
        Firebase.auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun signIn(email: String, password: String) {
        // מתחבר חשבון קיים
        Firebase.auth.signInWithEmailAndPassword(email, password)
    }

    override fun currentUserUid(): String? =
        Firebase.auth.currentUser?.uid

    override suspend fun saveUserProfile(uid: String, email: String) {
        // שומר מסמך משתמש ב‑Firestore תחת collection “users”
        Firebase.firestore
            .collection("users")
            .document(uid)
            .set(mapOf(
                "uid" to uid,
                "email" to email
            ))
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun saveReport(
        description: String,
        name: String,
        phone: String,
        imageUrl: String,
        isLost: Boolean,
        location: String?
    ) {
        // ① get the current user’s UID
        val userId = Firebase.auth.currentUser
            ?.uid
            ?: throw IllegalStateException("No authenticated user!")

        // ② write a document that includes userId
        Firebase.firestore
            .collection("reports")
            .add(
                mapOf(
                    "userId"      to userId,
                    "description" to description,
                    "name"        to name,
                    "phone"       to phone,
                    "imageUrl"    to imageUrl,
                    "isLost"      to isLost,
                    "location"    to location
                )
            )
    }
}

    override fun currentUserEmail(): String? =
        Firebase.auth.currentUser?.email

    override suspend fun updatePassword(newPassword: String) {
        Firebase.auth.currentUser
            ?.updatePassword(newPassword)
            ?: throw IllegalStateException("No signed-in user")
    }}
