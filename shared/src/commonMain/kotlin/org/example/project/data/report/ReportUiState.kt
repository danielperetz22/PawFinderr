package org.example.project.data.report

sealed class ReportUiState {
    // --- saving a report ---
    object Idle           : ReportUiState()
    object Saving         : ReportUiState()
    object SaveSuccess    : ReportUiState()
    data class SaveError(val throwable: Throwable) : ReportUiState()

    // --- loading the list of reports ---
    object LoadingReports : ReportUiState()
    data class ReportsLoaded(val reports: List<ReportModel>) : ReportUiState()
    data class LoadError(val throwable: Throwable) : ReportUiState()

    // --- updating or deleting a report ---
    object UpdateSuccess : ReportUiState()
    object DeleteSuccess : ReportUiState()
    data class UpdateError(val throwable: Throwable) : ReportUiState()
    data class DeleteError(val throwable: Throwable) : ReportUiState()
}