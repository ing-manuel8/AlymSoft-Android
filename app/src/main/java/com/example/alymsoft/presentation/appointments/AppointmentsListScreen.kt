package com.example.alymsoft.presentation.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.domain.model.Appointment
import com.example.alymsoft.domain.model.AppointmentStatus
import com.example.alymsoft.presentation.appointments.components.*
import com.example.alymsoft.presentation.calendar.CalendarViewModel
import com.example.alymsoft.presentation.calendar.details.AppointmentDetailsScreen
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppointmentFilterOption(val title: String) {
    ALL("Todas"),
    PENDING("Pendientes"),
    CONFIRMED("Confirmadas"),
    COMPLETED("Completadas"),
    CANCELLED("Canceladas")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsListScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var selectedFilter by remember { mutableStateOf(AppointmentFilterOption.ALL) }
    var searchText by remember { mutableStateOf("") }
    
    var filterStartDate by remember { mutableStateOf(Date()) }
    var filterEndDate by remember { 
        mutableStateOf(Calendar.getInstance().apply { add(Calendar.MONTH, 1) }.time)
    }
    var filterProfessionalId by remember { mutableStateOf<Int?>(null) }
    var activeQuickFilter by remember { mutableStateOf<QuickDateFilter?>(QuickDateFilter.THIS_MONTH) }
    
    var showingFilterSheet by remember { mutableStateOf(false) }
    var selectedAppointmentForDetail by remember { mutableStateOf<Appointment?>(null) }

    val filteredAppointments = remember(uiState.appointments, selectedFilter, searchText) {
        var apps = uiState.appointments
        
        when (selectedFilter) {
            AppointmentFilterOption.ALL -> {}
            AppointmentFilterOption.PENDING -> apps = apps.filter { it.status == AppointmentStatus.PENDING }
            AppointmentFilterOption.CONFIRMED -> apps = apps.filter { it.status == AppointmentStatus.CONFIRMED }
            AppointmentFilterOption.COMPLETED -> apps = apps.filter { it.status == AppointmentStatus.COMPLETED }
            AppointmentFilterOption.CANCELLED -> apps = apps.filter { it.status == AppointmentStatus.CANCELLED }
        }
        
        if (searchText.isNotBlank()) {
            val query = searchText.trim().lowercase()
            apps = apps.filter {
                it.customerName.lowercase().contains(query) ||
                it.serviceName.lowercase().contains(query) ||
                (it.professionalName?.lowercase()?.contains(query) == true)
            }
        }
        apps
    }

    val dateRangeString = remember(filterStartDate, filterEndDate) {
        val sdf = SimpleDateFormat("d MMM", Locale("es", "MX"))
        val sdfSingle = SimpleDateFormat("d 'de' MMMM", Locale("es", "MX"))
        val calStart = Calendar.getInstance().apply { time = filterStartDate }
        val calEnd = Calendar.getInstance().apply { time = filterEndDate }
        
        if (calStart.get(Calendar.DAY_OF_YEAR) == calEnd.get(Calendar.DAY_OF_YEAR) &&
            calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR)) {
            sdfSingle.format(filterStartDate)
        } else {
            "${sdf.format(filterStartDate)} - ${sdf.format(filterEndDate)}"
        }
    }

    if (selectedAppointmentForDetail != null) {
        AppointmentDetailsScreen(
            appointment = selectedAppointmentForDetail!!,
            onConfirmClick = { viewModel.confirmAppointment(it) },
            onCancelClick = { viewModel.cancelAppointment(it) },
            onBackClick = { selectedAppointmentForDetail = null },
            modifier = modifier
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Listado de Citas", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            modifier = modifier
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Metric Cards (Horizontal Scroll)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(title = "Total Citas", value = "${uiState.appointments.size}", icon = Icons.Default.Event, color = PrimaryBlue)
                    MetricCard(title = "Confirmadas", value = "${uiState.appointments.count { it.status == AppointmentStatus.CONFIRMED }}", icon = Icons.Default.CheckCircle, color = PrimaryBlue)
                    MetricCard(title = "Pendientes", value = "${uiState.appointments.count { it.status == AppointmentStatus.PENDING }}", icon = Icons.Default.Schedule, color = Color(0xFFFF9800))
                    MetricCard(title = "Completadas", value = "${uiState.appointments.count { it.status == AppointmentStatus.COMPLETED }}", icon = Icons.Default.TaskAlt, color = Color(0xFF4CAF50))
                    MetricCard(title = "Canceladas", value = "${uiState.appointments.count { it.status == AppointmentStatus.CANCELLED }}", icon = Icons.Default.Cancel, color = Color(0xFFF44336))
                }

                // Filter Tabs (Horizontal Scroll)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppointmentFilterOption.values().forEach { option ->
                        val isSelected = selectedFilter == option
                        Button(
                            onClick = { selectedFilter = option },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surface,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
                        ) {
                            Text(option.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                }

                // Search Bar
                AppointmentsSearchBar(
                    searchText = searchText,
                    onSearchTextChanged = { searchText = it },
                    dateRangeString = dateRangeString,
                    onOpenFilterSheet = { showingFilterSheet = true },
                    onClearSearch = { searchText = "" },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else if (filteredAppointments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = 60.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.EventBusy, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(60.dp))
                            Text("No hay citas registradas para este filtro", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredAppointments) { appointment ->
                            AppointmentMobileCard(
                                appointment = appointment,
                                onTap = { selectedAppointmentForDetail = appointment }
                            )
                        }
                    }
                }
            }
        }

        if (showingFilterSheet) {
            PeriodFilterSheet(
                initialStartDate = filterStartDate,
                initialEndDate = filterEndDate,
                initialProfessionalId = filterProfessionalId,
                initialQuickFilter = activeQuickFilter,
                professionals = uiState.professionals,
                onApply = { start, end, profId, quickFilter ->
                    filterStartDate = start
                    filterEndDate = end
                    filterProfessionalId = profId
                    activeQuickFilter = quickFilter
                    // TODO: Refresh via ViewModel API call using these parameters if needed
                },
                onClear = {
                    filterStartDate = Date()
                    filterEndDate = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }.time
                    filterProfessionalId = null
                    activeQuickFilter = QuickDateFilter.THIS_MONTH
                },
                onDismiss = { showingFilterSheet = false }
            )
        }
    }
}
