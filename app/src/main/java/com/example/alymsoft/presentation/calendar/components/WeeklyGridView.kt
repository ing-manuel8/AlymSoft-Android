package com.example.alymsoft.presentation.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.domain.model.Appointment
import com.example.alymsoft.domain.model.AppointmentStatus
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WeeklyGridView(
    selectedDate: Date,
    appointments: List<Appointment>,
    onDateSelected: (Date) -> Unit,
    onAppointmentClick: (Appointment) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNameFormat = remember { SimpleDateFormat("EEE", Locale.forLanguageTag("es")) }
    val dayNumFormat = remember { SimpleDateFormat("d", Locale.getDefault()) }
    val sameDayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val weekDays = remember(selectedDate) {
        val cal = Calendar.getInstance().apply {
            time = selectedDate
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        (0..6).map {
            val date = cal.time
            cal.add(Calendar.DAY_OF_WEEK, 1)
            date
        }
    }

    val selectedDateStr = remember(selectedDate) { sameDayFormat.format(selectedDate) }
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        weekDays.forEach { day ->
            val dayStr = sameDayFormat.format(day)
            val isSelected = dayStr == selectedDateStr
            val dayApps = appointments.filter { it.date.startsWith(dayStr) }

            Column(
                modifier = Modifier.width(130.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PrimaryBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onDateSelected(day) }
                        .padding(vertical = 8.dp, horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayNameFormat.format(day).replace(".", "").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(if (isSelected) PrimaryBlue else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNumFormat.format(day),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${dayApps.size} citas",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier
                            .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (dayApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sin citas",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    dayApps.forEach { app ->
                        WeeklyAppointmentCard(
                            appointment = app,
                            onClick = { onAppointmentClick(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyAppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit
) {
    val statusColor = when (appointment.status) {
        AppointmentStatus.CONFIRMED -> PrimaryBlue
        AppointmentStatus.PENDING -> Color(0xFFFF9800)
        AppointmentStatus.CANCELLED -> Color(0xFFF44336)
        AppointmentStatus.COMPLETED -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(statusColor, CircleShape)
            )
            Text(
                text = appointment.startTime,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
        Text(
            text = appointment.customerName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = appointment.serviceName,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
