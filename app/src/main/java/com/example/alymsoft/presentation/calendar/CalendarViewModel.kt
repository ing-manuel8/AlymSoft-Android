package com.example.alymsoft.presentation.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alymsoft.data.dto.CalendarProfessionalDTO
import com.example.alymsoft.data.repository.CalendarRepositoryImpl
import com.example.alymsoft.domain.model.Appointment
import com.example.alymsoft.domain.model.AppointmentStatus
import com.example.alymsoft.domain.repository.CalendarRepository
import com.example.alymsoft.presentation.calendar.components.CalendarViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarUiState(
    val selectedDate: Date = Date(),
    val viewMode: CalendarViewMode = CalendarViewMode.DAY,
    val selectedProfessionalId: Int? = null,
    val selectedFilterIndex: Int = 0, // 0: Todos, 1: Confirmadas, 2: Pendientes, 3: Canceladas
    val professionals: List<CalendarProfessionalDTO> = emptyList(),
    val appointments: List<Appointment> = emptyList(),
    val filteredAppointments: List<Appointment> = emptyList(),
    val currentDynamicTimeSlots: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val calendarRepository: CalendarRepository = CalendarRepositoryImpl(application)

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadProfessionals()
    }

    fun loadProfessionals() {
        viewModelScope.launch {
            val result = calendarRepository.getProfessionals()
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    professionals = list,
                    selectedProfessionalId = null
                )
                fetchAppointmentsForCurrentMode()
            }.onFailure {
                fetchAppointmentsForCurrentMode()
            }
        }
    }

    fun onDateSelected(date: Date) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        fetchAppointmentsForCurrentMode()
    }

    fun onViewModeSelected(mode: CalendarViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
        fetchAppointmentsForCurrentMode()
    }

    fun onProfessionalSelected(profId: Int?) {
        _uiState.value = _uiState.value.copy(selectedProfessionalId = profId)
        applyFilter()
    }

    fun onFilterSelected(index: Int) {
        _uiState.value = _uiState.value.copy(selectedFilterIndex = index)
        applyFilter()
    }

    fun refreshCurrentAgenda() {
        fetchAppointmentsForCurrentMode()
    }

    private fun fetchAppointmentsForCurrentMode() {
        val state = _uiState.value
        val cal = Calendar.getInstance().apply {
            time = state.selectedDate
            firstDayOfWeek = Calendar.MONDAY
        }

        val (startStr, endStr) = when (state.viewMode) {
            CalendarViewMode.DAY -> {
                val dStr = dateFormat.format(state.selectedDate)
                "${dStr}T00:00:00.000Z" to "${dStr}T23:59:59.000Z"
            }
            CalendarViewMode.WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val sStr = dateFormat.format(cal.time)
                cal.add(Calendar.DAY_OF_WEEK, 6)
                val eStr = dateFormat.format(cal.time)
                "${sStr}T00:00:00.000Z" to "${eStr}T23:59:59.000Z"
            }
            CalendarViewMode.MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val sStr = dateFormat.format(cal.time)
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                val eStr = dateFormat.format(cal.time)
                "${sStr}T00:00:00.000Z" to "${eStr}T23:59:59.000Z"
            }
        }
        fetchAppointments(startStr, endStr)
    }

    private fun fetchAppointments(startDateISO: String, endDateISO: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = calendarRepository.getAppointments(startDate = startDateISO, endDate = endDateISO)
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    appointments = list
                )
                applyFilter()
                generateDynamicTimeSlots()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Error al cargar la agenda"
                )
            }
        }
    }

    private fun generateDynamicTimeSlots() {
        val state = _uiState.value
        val apps = state.appointments
        var minStart = 8
        var maxEnd = 19

        val appHours = apps.mapNotNull { parseHour(it.startTime) }
        if (appHours.isNotEmpty()) {
            minStart = minOf(minStart, appHours.minOrNull() ?: 8)
            maxEnd = maxOf(maxEnd, appHours.maxOrNull() ?: 19)
        }

        val slots = mutableListOf<String>()
        for (hour in minStart..maxEnd) {
            val period = if (hour < 12) "AM" else "PM"
            val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            slots.add(String.format("%02d:00 %s", formattedHour, period))
        }
        _uiState.value = state.copy(currentDynamicTimeSlots = slots)
    }

    private fun parseHour(timeStr: String?): Int? {
        if (timeStr.isNullOrBlank()) return null
        val clean = timeStr.trim()
        if (clean.contains("AM") || clean.contains("PM")) {
            val parts = clean.split(" ")
            val timePart = parts.firstOrNull() ?: return null
            val period = parts.lastOrNull()
            val subParts = timePart.split(":")
            var hour = subParts.firstOrNull()?.toIntOrNull() ?: return null
            if (period == "PM" && hour < 12) hour += 12
            if (period == "AM" && hour == 12) hour = 0
            return hour
        }
        val subParts = clean.split("T").last().split(":")
        return subParts.firstOrNull()?.toIntOrNull()
    }

    private fun applyFilter() {
        val state = _uiState.value
        val all = state.appointments

        val statusFiltered = when (state.selectedFilterIndex) {
            1 -> all.filter { it.status == AppointmentStatus.CONFIRMED }
            2 -> all.filter { it.status == AppointmentStatus.PENDING }
            3 -> all.filter { it.status == AppointmentStatus.CANCELLED }
            else -> all
        }

        val profId = state.selectedProfessionalId
        val finalFiltered = if (profId == null) {
            statusFiltered
        } else {
            statusFiltered.filter { it.professionalId == profId }
        }

        _uiState.value = state.copy(filteredAppointments = finalFiltered)
    }

    fun confirmAppointment(appointmentId: Int) {
        viewModelScope.launch {
            val result = calendarRepository.confirmAppointment(appointmentId)
            result.onSuccess { fetchAppointmentsForCurrentMode() }
        }
    }

    fun cancelAppointment(appointmentId: Int) {
        viewModelScope.launch {
            val result = calendarRepository.cancelAppointment(appointmentId)
            result.onSuccess { fetchAppointmentsForCurrentMode() }
        }
    }
}
