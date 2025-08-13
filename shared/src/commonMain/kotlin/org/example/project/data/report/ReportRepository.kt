package org.example.project.data.report


interface ReportRepository {
    suspend fun saveReport(
        description: String,
        name: String,
        phone: String,
        imageUrl: String,
        isLost: Boolean,
        location: String? = null,
        lat: Double,
        lng: Double
    )

    suspend fun getReportsForUser(userId: String): List<ReportModel>
    suspend fun getAllReports(): List<ReportModel>
}