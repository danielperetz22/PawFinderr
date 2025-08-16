package org.example.project.data.report


import org.example.project.data.firebase.FirebaseRepository
import org.example.project.data.firebase.RemoteFirebaseRepository

class ReportRepositoryImpl(
    private val firebase: FirebaseRepository
) : ReportRepository {
    constructor() : this(RemoteFirebaseRepository())
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
        firebase.saveReport(description, name, phone, imageUrl, isLost, location, lat, lng)
    }

    override suspend fun getReportsForUser(userId: String): List<ReportModel> =
        firebase.getReportsForUser(userId)

    override suspend fun getAllReports(): List<ReportModel> =
        firebase.getAllReports()


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
    ) = firebase.updateReport(reportId, description, name, phone, imageUrl, isLost, location, lat, lng)

    override suspend fun deleteReport(reportId: String) =
        firebase.deleteReport(reportId)

}
