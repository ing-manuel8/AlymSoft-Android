package com.example.alymsoft.presentation.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.domain.model.Appointment
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MonthlyGridView(
    selectedDate: Date,
    appointments: List<Appointment>,
    onDateSelected: (Date) -> Unit,
    onAppointmentClick: (Appointment) -> Unit,
    modifier: Modifier = Modifier
) {
    val weekDaysHeaders = remember { listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom") }
    val dayNumFormat = remember { SimpleDateFormat("d", Locale.getDefault()) }
    val sameDayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val daysInMonth = remember(selectedDate) {
        val cal = Calendar.getInstance().apply {
            time = selectedDate
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val offset = (firstDayOfWeek - Calendar.MONDAY + 7) % 7
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val list = mutableListOf<Date?>()
        for (i in 0 until offset) {
            list.add(null)
        }
        for (d in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, d)
            list.add(cal.time)
        }
        list
    }

    val selectedDateStr = remember(selectedDate) { sameDayFormat.format(selectedDate) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            weekDaysHeaders.forEach { header ->
                Text(
                    text = header,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.heightIn(max = 380.dp)
        ) {
            items(daysInMonth) { date ->
                if (date != null) {
                    val dateStr = sameDayFormat.format(date)
                    val isSelected = dateStr == selectedDateStr
                    val dayApps = appointments.filter { it.date.startsWith(dateStr) }

                    MonthDayCell(
                        date = date,
                        dayNum = dayNumFormat.format(date),
                        isSelected = isSelected,
                        appointmentCount = dayApps.size,
                        onClick = { onDateSelected(date) }
                    )
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
private fun MonthDayCell(
    date: Date,
    dayNum: String,
    isSelected: Boolean,
    appointmentCount: Int,
    onClick: () -> Unit
) {
    val cellBg = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(cellBg)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayNum,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
        if (appointmentCount > 0) {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dotsToShow = minOf(appointmentCount, 3)
                for (i in 0 until dotsToShow) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(if (isSelected) Color.White else PrimaryBlue, CircleShape)
                    )
                }
            }
        }
    }
}
