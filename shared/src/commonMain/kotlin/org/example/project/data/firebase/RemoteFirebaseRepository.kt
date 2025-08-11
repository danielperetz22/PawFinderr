// commonMain
package org.example.project.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.firestore.*
import org.example.project.data.report.ReportModel
import kotlinx.datetime.Clock




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
            .set(
                mapOf(
                    "uid" to uid,
                    "email" to email
                )
            )
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override fun currentUserEmail(): String? =
        Firebase.auth.currentUser?.email

    override suspend fun updatePassword(newPassword: String) {
        Firebase.auth.currentUser
            ?.updatePassword(newPassword)
            ?: throw IllegalStateException("No signed-in user")
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
                    "userId" to userId,
                    "description" to description,
                    "name"        to name,
                    "phone"       to phone,
                    "imageUrl"    to imageUrl,
                    "isLost"      to isLost,
                    "location"    to location
                )
            )
    }

    // shared RemoteFirebaseRepository
    override suspend fun getReportsForUser(userId: String): List<ReportModel> {
        val snapshot = Firebase.firestore
            .collection("reports")
            .where { "userId" equalTo userId }
            // .orderBy("createdAt", Direction.DESCENDING)   // TEMPORARILY DISABLE
            .get()

        val results = mutableListOf<ReportModel>()
        for (doc in snapshot.documents) {
            try {
                val m = doc.data(ReportModel.serializer()).copy(id = doc.id)
                results += m
            } catch (e: Exception) {
                val raw = try { doc.data() as? Map<String, Any?> ?: emptyMap() } catch (_: Throwable) { emptyMap() }
                results += ReportModel(
                    id          = doc.id,
                    userId      = raw["userId"]?.toString().orEmpty(),
                    description = raw["description"]?.toString().orEmpty(),
                    name        = raw["name"]?.toString().orEmpty(),
                    phone       = raw["phone"]?.toString().orEmpty(),
                    imageUrl    = raw["imageUrl"]?.toString().orEmpty(),
                    isLost      = (raw["isLost"] as? Boolean) ?: false,
                    location    = raw["location"]?.toString(),
                    createdAt   = (raw["createdAt"] as? Number)?.toLong() ?: 0L
                )
            }
        }

        // newest first on client
        return results.sortedByDescending { it.createdAt }
    }


}
