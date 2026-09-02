package com.example.alymsoft.data.repository

import android.content.Context
import com.example.alymsoft.core.network.NetworkClient
import com.example.alymsoft.data.dto.*
import com.example.alymsoft.domain.model.Appointment
import com.example.alymsoft.domain.repository.CalendarRepository

class CalendarRepositoryImpl(context: Context) : CalendarRepository {

    private val apiService = NetworkClient.getApiService(context)

    override suspend fun getAppointments(startDate: String, endDate: String): Result<List<Appointment>> {
        return try {
            val response = apiService.getAppointments(startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                val dtoList = response.body()!!.appointments ?: emptyList()
                Result.success(dtoList.map { it.toDomain() })
            } else {
                Result.failure(Exception("Error al cargar citas: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingAppointments(): Result<List<Appointment>> {
        return try {
            val response = apiService.getUpcomingAppointments()
            if (response.isSuccessful && response.body() != null) {
                val dtoList = response.body()!!.appointments ?: emptyList()
                Result.success(dtoList.map { it.toDomain() })
            } else {
                Result.failure(Exception("Error al cargar pr\u00f3ximas citas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmAppointment(id: Int): Result<Boolean> {
        return try {
            val response = apiService.confirmAppointment(id)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAppointment(id: Int): Result<Boolean> {
        return try {
            val response = apiService.cancelAppointment(id)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAttendance(id: Int): Result<Boolean> {
        return try {
            val response = apiService.markAttendance(id)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfessionals(): Result<List<CalendarProfessionalDTO>> {
        return try {
            val response = apiService.getProfessionals()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.professionals ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar profesionales"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getServices(): Result<List<CalendarServiceDTO>> {
        return try {
            val response = apiService.getServices()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.services ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar servicios"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClients(searchTerm: String?): Result<List<CalendarClientDTO>> {
        return try {
            val response = apiService.getClients(searchTerm)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.clients ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar clientes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOccupiedSlots(date: String): Result<List<OccupiedSlotDTO>> {
        return try {
            val response = apiService.getOccupiedSlots(date)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.success(emptyList())
        }
    }

    override suspend fun registerAppointment(request: RegisterAppointmentRequestDTO): Result<RegisterAppointmentResponseWrapper> {
        return try {
            val response = apiService.registerAppointment(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error en servidor (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
