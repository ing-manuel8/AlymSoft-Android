package com.example.alymsoft.presentation.appointments.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.data.dto.CalendarProfessionalDTO
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class QuickDateFilter(val title: String) {
    TODAY("Hoy"),
    YESTERDAY("Ayer"),
    THIS_WEEK("Esta Semana"),
    THIS_MONTH("Este Mes"),
    LAST_MONTH("Mes Anterior")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodFilterSheet(
    initialStartDate: Date,
    initialEndDate: Date,
    initialProfessionalId: Int?,
    initialQuickFilter: QuickDateFilter?,
    professionals: List<CalendarProfessionalDTO>,
    onApply: (Date, Date, Int?, QuickDateFilter?) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    var tempStartDate by remember { mutableStateOf(initialStartDate) }
    var tempEndDate by remember { mutableStateOf(initialEndDate) }
    var tempProfessionalId by remember { mutableStateOf(initialProfessionalId) }
    var tempQuickFilter by remember { mutableStateOf(initialQuickFilter) }
    
    var isUpdatingFromQuickFilter by remember { mutableStateOf(false) }

    var expandedProfDropdown by remember { mutableStateOf(false) }

    fun applyQuickFilter(filter: QuickDateFilter) {
        isUpdatingFromQuickFilter = true
        val cal = Calendar.getInstance()
        val now = Date()

        when (filter) {
            QuickDateFilter.TODAY -> {
                tempEndDate = now
                cal.time = now
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                tempStartDate = cal.time
            }
            QuickDateFilter.YESTERDAY -> {
                cal.time = now
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = cal.time
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                tempEndDate = cal.time
                cal.time = yesterday
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                tempStartDate = cal.time
            }
            QuickDateFilter.THIS_WEEK -> {
                tempEndDate = now
                cal.time = now
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                tempStartDate = cal.time
            }
            QuickDateFilter.THIS_MONTH -> {
                tempEndDate = now
                cal.time = now
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                tempStartDate = cal.time
            }
            QuickDateFilter.LAST_MONTH -> {
                cal.time = now
                cal.add(Calendar.MONTH, -1)
                val lastMonth = cal.time
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                tempEndDate = cal.time
                cal.time = lastMonth
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                tempStartDate = cal.time
            }
        }
        tempQuickFilter = filter
        isUpdatingFromQuickFilter = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onClear(); onDismiss() }) {
                    Text("Limpiar", color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                }
                Text("Filtrar Citas", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                TextButton(onClick = { onApply(tempStartDate, tempEndDate, tempProfessionalId, tempQuickFilter); onDismiss() }) {
                    Text("Aplicar", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }

            // Quick Filters
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Rango Rápido", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                // Display as a wrapping layout (or simply rows)
                val filters = QuickDateFilter.values()
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        filters.take(3).forEach { filter ->
                            val isSelected = tempQuickFilter == filter
                            Button(
                                onClick = { applyQuickFilter(filter) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(filter.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        filters.drop(3).forEach { filter ->
                            val isSelected = tempQuickFilter == filter
                            Button(
                                onClick = { applyQuickFilter(filter) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(filter.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            // Custom Dates
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Personalizar Fechas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    Column {
                        Text("Desde", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedButton(onClick = { /* TODO: Open DatePicker for tempStartDate */ }) {
                            Text(sdf.format(tempStartDate), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Column {
                        Text("Hasta", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedButton(onClick = { /* TODO: Open DatePicker for tempEndDate */ }) {
                            Text(sdf.format(tempEndDate), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            // Professional Filter
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Profesional", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                            .clickable { expandedProfDropdown = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.PersonSearch, contentDescription = null, tint = PrimaryBlue)
                        Text(
                            text = professionals.firstOrNull { it.professionalId == tempProfessionalId }?.name ?: "Todos los profesionales",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedProfDropdown,
                        onDismissRequest = { expandedProfDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos los profesionales") },
                            onClick = { tempProfessionalId = null; expandedProfDropdown = false },
                            trailingIcon = { if (tempProfessionalId == null) Icon(Icons.Default.Check, contentDescription = null) }
                        )
                        HorizontalDivider()
                        professionals.forEach { prof ->
                            DropdownMenuItem(
                                text = { Text(prof.name ?: "") },
                                onClick = { tempProfessionalId = prof.professionalId; expandedProfDropdown = false },
                                trailingIcon = { if (tempProfessionalId == prof.professionalId) Icon(Icons.Default.Check, contentDescription = null) }
                            )
                        }
                    }
                }
            }
        }
    }
}
