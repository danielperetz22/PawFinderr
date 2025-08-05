package org.example.project.data.report

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReportViewModel(
    private val repo: ReportRepository = ReportRepositoryImpl(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    @Suppress("unused")
    constructor() : this(
        ReportRepositoryImpl(),
        CoroutineScope(Dispatchers.Default + SupervisorJob())
    )

    fun saveReport(
        description: String,
        name: String,
        phone: String,
        imageUrl: String,
        isLost: Boolean,
        location: String? = null
    ) {
        scope.launch {
            _uiState.value = ReportUiState.Saving
            try {
                repo.saveReport(description, name, phone, imageUrl, isLost, location)
                _uiState.value = ReportUiState.SaveSuccess
            } catch (e: Throwable) {
                _uiState.value = ReportUiState.SaveError(e)
            }
        }
    }

    fun loadReportsForUser(userId: String) {
        scope.launch {
            _uiState.value = ReportUiState.LoadingReports
            println("▶️ loading reports for $userId")
            try {
                val list = repo.getReportsForUser(userId)
                println("✅ loaded ${list.size} reports")
                _uiState.value = ReportUiState.ReportsLoaded(list)
            } catch (e: Throwable) {
                println("❌ load error: ${e.message}")
                _uiState.value = ReportUiState.LoadError(e)
            }
        }
    }
}