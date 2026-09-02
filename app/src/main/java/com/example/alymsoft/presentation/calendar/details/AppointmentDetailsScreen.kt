package com.example.alymsoft.presentation.calendar.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.domain.model.Appointment
import com.example.alymsoft.domain.model.AppointmentStatus
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailsScreen(
    appointment: Appointment,
    onConfirmClick: (Int) -> Unit = {},
    onCancelClick: (Int) -> Unit = {},
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCancelConfirmation by remember { mutableStateOf(false) }

    val statusColor = when (appointment.status) {
        AppointmentStatus.CONFIRMED -> PrimaryBlue
        AppointmentStatus.PENDING -> Color(0xFFFF9800)
        AppointmentStatus.CANCELLED -> Color(0xFFF44336)
        AppointmentStatus.COMPLETED -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    val legibleDate = remember(appointment.date) {
        formatLegibleDate(appointment.date)
    }

    val formattedTime = remember(appointment.startTime, appointment.endTime) {
        "${formatTimeTo12Hour(appointment.startTime)} - ${formatTimeTo12Hour(appointment.endTime)}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la cita #${appointment.id}", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = appointment.status.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onConfirmClick(appointment.id)
                            onBackClick()
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (appointment.status == AppointmentStatus.CONFIRMED) Color.Gray.copy(alpha = 0.2f) else Color(0xFF4CAF50),
                            contentColor = if (appointment.status == AppointmentStatus.CONFIRMED) Color.Gray else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = appointment.status != AppointmentStatus.CONFIRMED
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(if (appointment.status == AppointmentStatus.CONFIRMED) "Confirmada" else "Confirmar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Button(
                        onClick = {
                            showCancelConfirmation = true
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (appointment.status == AppointmentStatus.CANCELLED) Color.Gray.copy(alpha = 0.2f) else Color(0xFFF44336),
                            contentColor = if (appointment.status == AppointmentStatus.CANCELLED) Color.Gray else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = appointment.status != AppointmentStatus.CANCELLED
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(if (appointment.status == AppointmentStatus.CANCELLED) "Cancelada" else "Cancelar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fecha y Horario
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Text(legibleDate, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Text(formattedTime, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Sección del Cliente
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Información del Cliente", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue)
                    }
                    Column {
                        Text(appointment.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        appointment.customerPhone?.let { phone ->
                            Text(phone, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                appointment.customerPhone?.takeIf { it.isNotBlank() }?.let { phone ->
                    val cleanPhone = phone.filter { it.isDigit() }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Llamar", fontSize = 13.sp, color = PrimaryBlue)
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanPhone"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp", fontSize = 13.sp, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }

            // Sección Profesional
            appointment.professionalName?.let { prof ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Atendido por", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Work, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Text(prof, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }

            // Servicios y Resumen de Pago
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Servicios y Pago", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(appointment.serviceName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    appointment.price?.let { price ->
                        Text(String.format("$%.2f MXN", price), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryBlue)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total a Pagar:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        String.format("$%.2f MXN", appointment.price ?: 0.0),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            // Notas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Notas", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    appointment.notes?.takeIf { it.isNotBlank() } ?: "Sin notas adicionales.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showCancelConfirmation) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmation = false },
            title = { Text("Cancelar Cita", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas cancelar la cita #${appointment.id}? Esta acción actualizará la agenda y liberará el horario.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelConfirmation = false
                        onCancelClick(appointment.id)
                        onBackClick()
                    }
                ) {
                    Text("Sí, Cancelar Cita", color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmation = false }) {
                    Text("No, Mantener Cita")
                }
            }
        )
    }
}

private fun formatLegibleDate(rawDate: String): String {
    if (rawDate.isBlank()) return "Fecha de Cita"
    val clean = rawDate.split("T").first()
    val parts = clean.split("-")
    if (parts.size == 3) {
        val y = parts[0].toIntOrNull() ?: 2026
        val m = parts[1].toIntOrNull() ?: 1
        val d = parts[2].toIntOrNull() ?: 1
        val cal = java.util.Calendar.getInstance().apply { set(y, m - 1, d) }
        val formatter = SimpleDateFormat("d 'de' MMMM yyyy", Locale.forLanguageTag("es"))
        return formatter.format(cal.time)
    }
    return rawDate
}

private fun formatTimeTo12Hour(rawTime: String): String {
    if (rawTime.isBlank()) return "09:00 AM"
    if (rawTime.contains("AM") || rawTime.contains("PM")) return rawTime
    val clean = rawTime.split("T").last()
    val parts = clean.split(":")
    var hour = parts.firstOrNull()?.toIntOrNull() ?: 9
    val min = if (parts.size > 1) parts[1] else "00"
    val period = if (hour >= 12) "PM" else "AM"
    if (hour == 0) hour = 12 else if (hour > 12) hour -= 12
    return String.format("%02d:%s %s", hour, min, period)
}
