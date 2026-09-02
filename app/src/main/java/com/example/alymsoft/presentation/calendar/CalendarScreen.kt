package com.example.alymsoft.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alymsoft.domain.model.Appointment
import com.example.alymsoft.presentation.calendar.components.*
import com.example.alymsoft.presentation.calendar.details.AppointmentDetailsScreen
import com.example.alymsoft.presentation.calendar.newappointment.NewAppointmentScreen
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.util.Calendar
import java.util.Date

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedAppointmentDetails by remember { mutableStateOf<Appointment?>(null) }
    var showingDatePickerModal by remember { mutableStateOf(false) }
    var showingNewAppointmentScreen by remember { mutableStateOf(false) }
    var selectedSlotDate by remember { mutableStateOf(uiState.selectedDate) }

    if (selectedAppointmentDetails != null) {
        AppointmentDetailsScreen(
            appointment = selectedAppointmentDetails!!,
            onConfirmClick = { viewModel.confirmAppointment(it) },
            onCancelClick = { viewModel.cancelAppointment(it) },
            onBackClick = { selectedAppointmentDetails = null },
            modifier = modifier
        )
    } else if (showingNewAppointmentScreen) {
        NewAppointmentScreen(
            initialDate = selectedSlotDate,
            initialProfessionalId = uiState.selectedProfessionalId,
            onAppointmentCreated = {
                viewModel.refreshCurrentAgenda()
            },
            onBackClick = { showingNewAppointmentScreen = false },
            modifier = modifier
        )
    } else {
        Scaffold(
            topBar = {
                CalendarHeaderBar(
                    selectedDate = uiState.selectedDate,
                    viewMode = uiState.viewMode,
                    onViewModeSelected = { viewModel.onViewModeSelected(it) },
                    onOpenDatePicker = { showingDatePickerModal = true },
                    onRefreshClick = { viewModel.refreshCurrentAgenda() }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        selectedSlotDate = uiState.selectedDate
                        showingNewAppointmentScreen = true
                    },
                    containerColor = PrimaryBlue,
                    contentColor = androidx.compose.ui.graphics.Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Cita")
                }
            },
            modifier = modifier
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (uiState.viewMode == CalendarViewMode.DAY || uiState.viewMode == CalendarViewMode.WEEK) {
                    WeeklyMiniCalendarView(
                        selectedDate = uiState.selectedDate,
                        onDateSelected = { viewModel.onDateSelected(it) }
                    )
                }

                if (uiState.professionals.isNotEmpty()) {
                    ProfessionalFilterSelectorView(
                        professionals = uiState.professionals,
                        selectedId = uiState.selectedProfessionalId,
                        onSelect = { viewModel.onProfessionalSelected(it) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (uiState.isLoading && uiState.appointments.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = PrimaryBlue
                        )
                    } else {
                        when (uiState.viewMode) {
                            CalendarViewMode.DAY -> {
                                LazyColumn(
                                    contentPadding = PaddingValues(bottom = 80.dp)
                                ) {
                                    items(
                                        items = uiState.currentDynamicTimeSlots,
                                        key = { it }
                                    ) { slot ->
                                        val slotHour = parseSlotHour(slot)
                                        val slotApps = uiState.filteredAppointments.filter { app ->
                                            parseAppHour(app.startTime) == slotHour
                                        }

                                        TimeSlotRow(
                                            timeSlot = slot,
                                            selectedDate = uiState.selectedDate,
                                            appointments = slotApps,
                                            onSelectSlot = { tappedSlot ->
                                                selectedSlotDate = calculateSlotDate(uiState.selectedDate, tappedSlot)
                                                showingNewAppointmentScreen = true
                                            },
                                            onSelectAppointment = { selectedAppointmentDetails = it }
                                        )
                                    }
                                }
                            }
                            CalendarViewMode.WEEK -> {
                                WeeklyGridView(
                                    selectedDate = uiState.selectedDate,
                                    appointments = uiState.filteredAppointments,
                                    onDateSelected = { viewModel.onDateSelected(it) },
                                    onAppointmentClick = { selectedAppointmentDetails = it }
                                )
                            }
                            CalendarViewMode.MONTH -> {
                                MonthlyGridView(
                                    selectedDate = uiState.selectedDate,
                                    appointments = uiState.filteredAppointments,
                                    onDateSelected = { viewModel.onDateSelected(it) },
                                    onAppointmentClick = { selectedAppointmentDetails = it }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showingDatePickerModal) {
            DatePickerModal(
                selectedDate = uiState.selectedDate,
                onDateSelected = { viewModel.onDateSelected(it) },
                onDismiss = { showingDatePickerModal = false }
            )
        }
    }
}

private fun calculateSlotDate(baseDate: Date, timeSlot: String): Date {
    val hour = parseSlotHour(timeSlot)
    val parts = timeSlot.split(" ")
    val timeOnly = parts.firstOrNull() ?: ""
    val sub = timeOnly.split(":")
    val min = if (sub.size > 1) sub[1].toIntOrNull() ?: 0 else 0

    return Calendar.getInstance().apply {
        time = baseDate
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, min)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

private fun parseSlotHour(slot: String): Int {
    val parts = slot.split(" ")
    val timeStr = parts.firstOrNull() ?: return 8
    val period = parts.lastOrNull()
    val sub = timeStr.split(":")
    var h = sub.firstOrNull()?.toIntOrNull() ?: 8
    if (period == "PM" && h < 12) h += 12
    if (period == "AM" && h == 12) h = 0
    return h
}

private fun parseAppHour(time: String): Int {
    if (time.isBlank()) return -1
    val clean = time.trim()
    if (clean.contains("AM") || clean.contains("PM")) {
        return parseSlotHour(clean)
    }
    val timeOnly = clean.split("T").last()
    val parts = timeOnly.split(":")
    return parts.firstOrNull()?.toIntOrNull() ?: -1
}
