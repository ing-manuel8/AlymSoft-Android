package com.example.alymsoft.presentation.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
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
import java.util.Calendar
import java.util.Date

@Composable
fun TimeSlotRow(
    timeSlot: String,
    selectedDate: Date,
    appointments: List<Appointment>,
    onSelectSlot: (String) -> Unit,
    onSelectAppointment: (Appointment) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = remember { Date() }

    val isToday = remember(selectedDate) {
        val calSelected = Calendar.getInstance().apply { time = selectedDate }
        val calNow = Calendar.getInstance().apply { time = now }
        calSelected.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                calSelected.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR)
    }

    val slotHour = remember(timeSlot) { parseHour(timeSlot) }
    val currentHour = remember(now) {
        Calendar.getInstance().apply { time = now }.get(Calendar.HOUR_OF_DAY)
    }
    val isCurrentHourSlot = isToday && (slotHour == currentHour)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = timeSlot,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(60.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopStart
        ) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            )

            if (isCurrentHourSlot) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Red, CircleShape)
                    )
                    HorizontalDivider(
                        color = Color.Red,
                        thickness = 2.dp
                    )
                }
            }

            if (appointments.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(top = 4.dp)
                ) {
                    appointments.forEach { app ->
                        SlotAppointmentItem(
                            appointment = app,
                            onClick = { onSelectAppointment(app) }
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectSlot(timeSlot) }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Agendar cita",
                        tint = PrimaryBlue.copy(alpha = 0.35f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SlotAppointmentItem(
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, statusColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(52.dp)
                .background(statusColor)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.customerName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = appointment.status.label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }

            Text(
                text = appointment.serviceName,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = "${appointment.startTime} - ${appointment.endTime}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                appointment.professionalName?.let { prof ->
                    Text(
                        text = "• $prof",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun parseHour(slot: String): Int {
    val components = slot.split(" ")
    val timePart = components.firstOrNull() ?: return 8
    val period = components.lastOrNull()
    val parts = timePart.split(":")
    var hour = parts.firstOrNull()?.toIntOrNull() ?: 8
    if (period == "PM" && hour < 12) hour += 12
    if (period == "AM" && hour == 12) hour = 0
    return hour
}
