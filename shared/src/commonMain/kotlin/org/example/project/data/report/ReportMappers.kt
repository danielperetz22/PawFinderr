package org.example.project.data.report

fun Reports.toModel() = ReportModel(
    id = id,
    userId = userId,
    description = description,
    name = name,
    phone = phone,
    imageUrl = imageUrl,
    isLost = isLost,
    location = location,
    lat = lat,
    lng = lng,
    createdAt = createdAt
)