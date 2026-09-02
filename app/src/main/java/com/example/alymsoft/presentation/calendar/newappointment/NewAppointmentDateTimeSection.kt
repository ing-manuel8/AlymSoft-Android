package com.example.alymsoft.presentation.calendar.newappointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NewAppointmentDateTimeSection(
    appointmentDate: Date,
    startTime: Date,
    totalDurationMinutes: Int,
    onDateSelected: (Date) -> Unit,
    onTimeSelected: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("EEEE, d 'de' MMMM yyyy", Locale.forLanguageTag("es")) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.forLanguageTag("es")) }

    val calculatedEndTime = remember(startTime, totalDurationMinutes) {
        val cal = Calendar.getInstance().apply { time = startTime }
        val dur = if (totalDurationMinutes > 0) totalDurationMinutes else 45
        cal.add(Calendar.MINUTE, dur)
        cal.time
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Fecha y Horario",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Selector de Fecha
            OutlinedTextField(
                value = dateFormat.format(appointmentDate).replaceFirstChar { it.uppercase() },
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de la Cita") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.clickable {
                            val cal = Calendar.getInstance().apply { time = appointmentDate }
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val newCal = Calendar.getInstance().apply {
                                        set(y, m, d)
                                    }
                                    onDateSelected(newCal.time)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val cal = Calendar.getInstance().apply { time = appointmentDate }
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                val newCal = Calendar.getInstance().apply {
                                    set(y, m, d)
                                }
                                onDateSelected(newCal.time)
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
            )

            // Selector de Hora de Inicio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = timeFormat.format(startTime),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora Inicio") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val cal = Calendar.getInstance().apply { time = startTime }
                            TimePickerDialog(
                                context,
                                { _, hour, min ->
                                    val newCal = Calendar.getInstance().apply {
                                        time = startTime
                                        set(Calendar.HOUR_OF_DAY, hour)
                                        set(Calendar.MINUTE, min)
                                    }
                                    onTimeSelected(newCal.time)
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                false
                            ).show()
                        }
                )

                OutlinedTextField(
                    value = timeFormat.format(calculatedEndTime),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora Fin Estimada") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Duración total: ${if (totalDurationMinutes > 0) totalDurationMinutes else 45} minutos",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue
            )
        }
    }
}
