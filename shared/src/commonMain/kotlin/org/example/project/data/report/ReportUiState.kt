package org.example.project.data.report

sealed class ReportUiState {
    object Idle    : ReportUiState()
    object Saving  : ReportUiState()
    object Success : ReportUiState()
    data class Error(val throwable: Throwable) : ReportUiState()
}