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
        location: String? = null,
        lat: Double,
        lng: Double
    ) {
        scope.launch {
            _uiState.value = ReportUiState.Saving
            try {
                repo.saveReport(description, name, phone, imageUrl, isLost, location, lat, lng)
                _uiState.value = ReportUiState.SaveSuccess
            } catch (e: Throwable) {
                _uiState.value = ReportUiState.SaveError(e)
            }
        }
    }

    fun loadReportsForUser(userId: String) {
        scope.launch {
            _uiState.value = ReportUiState.LoadingReports
            try {
                val list = repo.getReportsForUser(userId)
                _uiState.value = ReportUiState.ReportsLoaded(list)
            } catch (e: Throwable) {
                _uiState.value = ReportUiState.LoadError(e)
            }
        }
    }

    fun loadAllReports() {
        scope.launch {
            _uiState.value = ReportUiState.LoadingReports
            try {
                val list = repo.getAllReports()
                _uiState.value = ReportUiState.ReportsLoaded(list)
            } catch (e: Throwable) {
                _uiState.value = ReportUiState.LoadError(e)
            }
        }
    }
    fun updateReport(
        reportId: String,
        description: String? = null,
        name: String? = null,
        phone: String? = null,
        imageUrl: String? = null,
        isLost: Boolean? = null,
        location: String? = null,
        lat: Double? = null,
        lng: Double? = null
    ) {
        scope.launch {
            _uiState.value = ReportUiState.Saving
            try {
                repo.updateReport(reportId, description, name, phone, imageUrl, isLost, location, lat, lng)
                _uiState.value = ReportUiState.UpdateSuccess
            } catch (e: Throwable) {
                _uiState.value = ReportUiState.UpdateError(e)
            }
        }
    }

    fun deleteReport(reportId: String) {
        scope.launch {
            _uiState.value = ReportUiState.Saving
            try {
                repo.deleteReport(reportId)
                _uiState.value = ReportUiState.DeleteSuccess
            } catch (e: Throwable) {
                _uiState.value = ReportUiState.DeleteError(e)
            }
        }
    }
}