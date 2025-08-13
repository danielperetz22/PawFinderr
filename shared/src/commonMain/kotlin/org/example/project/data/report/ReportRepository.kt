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


    suspend fun updateReport(
        reportId: String,
        description: String? = null,
        name: String? = null,
        phone: String? = null,
        imageUrl: String? = null,
        isLost: Boolean? = null,
        location: String? = null,
        lat: Double? =null,
        lng: Double?=null
    )

    suspend fun deleteReport(reportId: String)
}