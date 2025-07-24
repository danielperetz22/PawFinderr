package org.example.project.data.report

data class ReportUiState(
    val type: ReportType = ReportType.LOST,
    val description: String = "",
    val name: String = "",
    val phone: String = "",
    val location: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

enum class ReportType {
    LOST, FOUND
}
