package org.example.project.data.report

import kotlinx.serialization.Serializable

@Serializable
data class ReportModel(
    val id: String = "",
    val userId: String = "",
    val description: String = "",
    val name: String = "",
    val phone: String = "",
    val imageUrl: String = "",
    val isLost: Boolean = false,
    val location: String? = null,
    val createdAt: Long = 0L
)