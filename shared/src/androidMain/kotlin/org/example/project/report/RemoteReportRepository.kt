package org.example.project.report

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.example.project.data.report.ReportRepository
import org.example.project.data.report.ReportUiState
import kotlin.collections.mapOf

class RemoteReportRepository : ReportRepository {


    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun submitReport(report: ReportUiState): Result<Unit> {
        return try {
            val data = mapOf(
                "type" to report.type.name,
                "description" to report.description,
                "name" to report.name,
                "phone" to report.phone,
                "location" to report.location,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("reports")
                .add(data)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
