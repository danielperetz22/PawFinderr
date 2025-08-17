// commonMain
package org.example.project.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.firestore.*
import org.example.project.data.report.ReportModel




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
        location: String?,
        lat: Double,
        lng: Double
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
                    "location"    to location,
                    "lat"         to lat,
                    "lng"         to lng,
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
    override suspend fun getAllReports(): List<ReportModel> {
        val snapshot = Firebase.firestore
            .collection("reports")
            .get()

        fun anyToDouble(v: Any?): Double = when (v) {
            is Number -> v.toDouble()
            is String -> v.toDoubleOrNull() ?: Double.NaN
            else      -> Double.NaN
        }

        val results = mutableListOf<ReportModel>()
        for (doc in snapshot.documents) {
            try {
                val m = doc.data(ReportModel.serializer()).copy(id = doc.id)
                results += m
            } catch (_: Exception) {
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
                    createdAt   = (raw["createdAt"] as? Number)?.toLong() ?: 0L,
                    lat         = anyToDouble(raw["lat"]),
                    lng         = anyToDouble(raw["lng"])
                )
            }
        }
        return results.sortedByDescending { it.createdAt }
    }
  override suspend fun updateReport(
        reportId: String,
        description: String?,
        name: String?,
        phone: String?,
        imageUrl: String?,
        isLost: Boolean?,
        location: String?,
        lat: Double?,
        lng: Double?
    ) {
        // build a partial update map (only fields you pass != null will be updated)
        val data = mutableMapOf<String, Any>()
        description?.let { data["description"] = it }
        name?.let        { data["name"]        = it }
        phone?.let       { data["phone"]       = it }
        imageUrl?.let    { data["imageUrl"]    = it }
        isLost?.let      { data["isLost"]      = it }
        location?.let    { data["location"]    = it }
        lat?.let {data["lat"] = it}
        lng?.let {data["lng"] = it}


      if (data.isEmpty()) return // nothing to update

        Firebase.firestore
            .collection("reports")
            .document(reportId)
            .update(data)
    }

    override suspend fun deleteReport(reportId: String) {
        Firebase.firestore
            .collection("reports")
            .document(reportId)
            .delete()
    }

}
