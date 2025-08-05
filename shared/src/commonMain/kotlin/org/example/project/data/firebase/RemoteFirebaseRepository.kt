// commonMain
package org.example.project.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.firestore.*
import org.example.project.data.report.ReportModel

class RemoteFirebaseRepository : FirebaseRepository {

    override suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
    }

    override fun currentUserUid(): String? =
        Firebase.auth.currentUser?.uid

    override suspend fun saveUserProfile(uid: String, email: String) {
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
        val userId = Firebase.auth.currentUser
            ?.uid
            ?: throw IllegalStateException("No authenticated user!")

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
    override suspend fun getReportsForUser(userId: String): List<ReportModel> {
        val snapshot = Firebase.firestore
            .collection("reports")
            .where { "userId" equalTo userId }
            .get()

        println("⚙️ [KMM] Queried ${snapshot.documents.size} docs for $userId")
        return snapshot.documents.mapNotNull { doc ->
            try {
                doc.data<ReportModel>()
                    .copy(id = doc.id)
            } catch (_: Exception) {
                null
            }
        }
    }
}
