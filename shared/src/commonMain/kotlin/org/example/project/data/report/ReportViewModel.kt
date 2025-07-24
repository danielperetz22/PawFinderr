package org.example.project.data.report

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel(
    private val repository: ReportRepository
) {

    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _state = MutableStateFlow(ReportUiState())
    val state: StateFlow<ReportUiState> = _state

    fun setType(type: ReportType) {
        _state.value = _state.value.copy(type = type)
    }

    fun updateDescription(text: String) {
        _state.value = _state.value.copy(description = text)
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun updatePhone(phone: String) {
        _state.value = _state.value.copy(phone = phone)
    }

    fun updateLocation(location: String?) {
        _state.value = _state.value.copy(location = location)
    }

    fun submitReport() {
        val report = _state.value

        if (report.name.isBlank() || report.phone.isBlank() || report.description.isBlank()) {
            _state.value = report.copy(errorMessage = "All fields except location are required.")
            return
        }

        viewModelScope.launch {
            val result = repository.submitReport(report)
            _state.value = if (result.isSuccess) {
                report.copy(isSuccess = true, errorMessage = null)
            } else {
                report.copy(isSuccess = false, errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }
}
