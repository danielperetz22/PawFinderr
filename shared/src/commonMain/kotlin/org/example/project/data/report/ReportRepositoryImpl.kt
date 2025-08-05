package org.example.project.data.report

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.*
import org.example.project.data.firebase.FirebaseRepository
import org.example.project.data.firebase.RemoteFirebaseRepository

class ReportRepositoryImpl(
    private val firebase: FirebaseRepository = RemoteFirebaseRepository()
) : ReportRepository {
    override suspend fun saveReport(
        description: String,
        name: String,
        phone: String,
        imageUrl: String,
        isLost: Boolean,
        location: String?
    ) {
        firebase.saveReport(description, name, phone, imageUrl, isLost, location)
    }

    override suspend fun getReportsForUser(userId: String): List<ReportModel> =
        firebase.getReportsForUser(userId)
}