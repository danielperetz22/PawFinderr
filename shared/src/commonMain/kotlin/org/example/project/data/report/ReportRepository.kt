package org.example.project.data.report

import org.example.project.data.report.ReportUiState

interface ReportRepository {
    suspend fun submitReport(report: ReportUiState): Result<Unit>
}
