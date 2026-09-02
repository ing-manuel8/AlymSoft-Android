package com.example.alymsoft.presentation.appointments.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.domain.model.Appointment
import com.example.alymsoft.domain.model.AppointmentStatus
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AppointmentMobileCard(
    appointment: Appointment,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val statusColor = when (appointment.status) {
        AppointmentStatus.CONFIRMED -> PrimaryBlue
        AppointmentStatus.PENDING -> Color(0xFFFF9800)
        AppointmentStatus.CANCELLED -> Color(0xFFF44336)
        AppointmentStatus.COMPLETED -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    val legibleDate = remember(appointment.date) {
        if (appointment.date.isBlank()) "Fecha"
        else {
            try {
                val clean = appointment.date.split("T").first()
                val parts = clean.split("-")
                val y = parts[0].toInt()
                val m = parts[1].toInt()
                val d = parts[2].toInt()
                val cal = java.util.Calendar.getInstance().apply { set(y, m - 1, d) }
                SimpleDateFormat("d 'de' MMM", Locale.forLanguageTag("es")).format(cal.time)
            } catch (e: Exception) {
                appointment.date
            }
        }
    }

    val legibleTime = remember(appointment.startTime) {
        if (appointment.startTime.isBlank()) "09:00 AM"
        else if (appointment.startTime.contains("AM") || appointment.startTime.contains("PM")) appointment.startTime
        else {
            try {
                val clean = appointment.startTime.split("T").last()
                val parts = clean.split(":")
                var h = parts[0].toInt()
                val min = if (parts.size > 1) parts[1] else "00"
                val p = if (h >= 12) "PM" else "AM"
                if (h == 0) h = 12 else if (h > 12) h -= 12
                String.format("%02d:%s %s", h, min, p)
            } catch (e: Exception) {
                appointment.startTime
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTap() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Compacto (#102 • 11:30 AM | Pendiente ...)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "#${appointment.id}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
                    Text(
                        text = "$legibleDate • $legibleTime",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = appointment.status.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = statusColor, modifier = Modifier.size(16.dp))
                }
            }

            // Body de la Tarjeta
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = appointment.customerName.take(1).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = appointment.customerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Blue, modifier = Modifier.size(12.dp))
                                Text("Cliente", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            appointment.professionalName?.let { profName ->
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Work, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(12.dp))
                                    Text(profName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }

                    appointment.price?.let { price ->
                        Text(
                            text = String.format("$%.2f", price),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue
                        )
                    }
                }

                // Servicio & WhatsApp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (appointment.serviceName.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                            Text(
                                text = appointment.serviceName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    val phone = appointment.customerPhone ?: ""
                    if (phone.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                                .clickable {
                                    val cleanPhone = phone.filter { it.isDigit() }
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanPhone"))
                                    context.startActivity(intent)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(12.dp))
                            Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                    }
                }

                // Cuadro de notas
                val notes = appointment.notes.takeIf { !it.isNullOrBlank() } ?: "Confirmación enviada por WhatsApp"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = notes,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
