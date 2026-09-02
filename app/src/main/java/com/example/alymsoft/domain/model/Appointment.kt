package com.example.alymsoft.domain.model

import androidx.compose.ui.graphics.Color

data class Appointment(
    val id: Int,
    val title: String,
    val customerName: String,
    val serviceName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus,
    val notes: String? = null,
    val price: Double? = null,
    val professionalId: Int? = null,
    val professionalName: String? = null,
    val customerPhone: String? = null
)

enum class AppointmentStatus(val label: String, val color: Color) {
    CONFIRMED("Confirmada", Color(0xFF2563EB)),
    PENDING("Pendiente", Color(0xFFD97706)),
    CANCELLED("Cancelada", Color(0xFFDC2626)),
    COMPLETED("Completada", Color(0xFF16A34A)),
    UNKNOWN("Desconocido", Color(0xFF6B7280));

    companion object {
        fun mapFrom(rawStatus: String?): AppointmentStatus {
            if (rawStatus.isNull_or_empty()) return PENDING
            return when (rawStatus!!.lowercase().trim()) {
                "confirmada", "confirmed" -> CONFIRMED
                "pendiente", "pending" -> PENDING
                "completada", "completed", "asistida", "attended" -> COMPLETED
                "cancelada", "cancelled" -> CANCELLED
                else -> PENDING
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
