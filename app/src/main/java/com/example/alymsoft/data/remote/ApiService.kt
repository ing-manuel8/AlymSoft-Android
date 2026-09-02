package com.example.alymsoft.data.remote

import com.example.alymsoft.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDTO): Response<ModernLoginResponseDTO>

    @GET("auth/check-session")
    suspend fun checkSession(): Response<ModernLoginResponseDTO>

    @POST("auth/logout")
    suspend fun logout(): Response<Map<String, String>>

    @GET("calendar/appointments")
    suspend fun getAppointments(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<CalendarAppointmentListWrapper>

    @GET("calendar/appointments/upcoming")
    suspend fun getUpcomingAppointments(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<CalendarAppointmentListWrapper>

    @POST("calendar/appointments")
    suspend fun registerAppointment(@Body request: RegisterAppointmentRequestDTO): Response<RegisterAppointmentResponseWrapper>

    @GET("calendar/occupied-slots")
    suspend fun getOccupiedSlots(@Query("date") date: String): Response<List<OccupiedSlotDTO>>

    @PATCH("calendar/appointments/{id}/confirm")
    suspend fun confirmAppointment(@Path("id") id: Int): Response<Unit>

    @PATCH("calendar/appointments/{id}/cancel")
    suspend fun cancelAppointment(@Path("id") id: Int): Response<Unit>

    @PATCH("calendar/appointments/{id}/mark-attendance")
    suspend fun markAttendance(@Path("id") id: Int): Response<Unit>

    @GET("calendar/professionals")
    suspend fun getProfessionals(): Response<CalendarProfessionalListWrapper>

    @GET("calendar/services")
    suspend fun getServices(): Response<CalendarServiceListWrapper>

    @GET("calendar/clients")
    suspend fun getClients(@Query("searchTerm") searchTerm: String? = null): Response<CalendarClientListWrapper>
}
