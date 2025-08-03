package org.example.project.data.report

import org.example.project.data.firebase.FirebaseRepository
import org.example.project.data.firebase.RemoteFirebaseRepository
import org.example.project.data.report.ReportRepository

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
}