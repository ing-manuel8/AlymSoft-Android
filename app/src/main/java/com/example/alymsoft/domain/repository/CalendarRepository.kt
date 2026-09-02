package com.example.alymsoft.domain.repository

import com.example.alymsoft.data.dto.*
import com.example.alymsoft.domain.model.Appointment

interface CalendarRepository {
    suspend fun getAppointments(startDate: String, endDate: String): Result<List<Appointment>>
    suspend fun getUpcomingAppointments(): Result<List<Appointment>>
    suspend fun confirmAppointment(id: Int): Result<Boolean>
    suspend fun cancelAppointment(id: Int): Result<Boolean>
    suspend fun markAttendance(id: Int): Result<Boolean>
    suspend fun getProfessionals(): Result<List<CalendarProfessionalDTO>>
    suspend fun getServices(): Result<List<CalendarServiceDTO>>
    suspend fun getClients(searchTerm: String? = null): Result<List<CalendarClientDTO>>
    suspend fun getOccupiedSlots(date: String): Result<List<OccupiedSlotDTO>>
    suspend fun registerAppointment(request: RegisterAppointmentRequestDTO): Result<RegisterAppointmentResponseWrapper>
}
