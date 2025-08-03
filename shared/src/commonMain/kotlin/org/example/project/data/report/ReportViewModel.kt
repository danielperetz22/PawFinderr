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
    @Suppress("unused")  // so the compiler won’t warn
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
                _uiState.value = ReportUiState.Success
            } catch (e: Throwable) {
                _uiState.value = ReportUiState.Error(e)
            }
        }
    }
}