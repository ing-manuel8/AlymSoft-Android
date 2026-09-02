package com.example.alymsoft.presentation.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WeeklyMiniCalendarView(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNameFormat = remember { SimpleDateFormat("EEE", Locale.forLanguageTag("es")) }
    val dayNumberFormat = remember { SimpleDateFormat("d", Locale.getDefault()) }
    val sameDayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val daysList = remember(selectedDate) {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance().apply { time = selectedDate }
        cal.add(Calendar.DAY_OF_YEAR, -14)
        for (i in 0..44) {
            list.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val selectedDateStr = remember(selectedDate) { sameDayFormat.format(selectedDate) }
    val selectedIndex = remember(daysList, selectedDateStr) {
        val idx = daysList.indexOfFirst { sameDayFormat.format(it) == selectedDateStr }
        if (idx >= 0) idx else 14
    }

    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < daysList.size) {
            listState.animateScrollToItem(index = maxOf(0, selectedIndex - 2))
        }
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        items(daysList) { date ->
            val dateStr = sameDayFormat.format(date)
            val isSelected = dateStr == selectedDateStr

            val cardBg = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surface
            val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            val borderColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

            Column(
                modifier = Modifier
                    .width(52.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardBg)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable { onDateSelected(date) }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dayNameFormat.format(date).replace(".", "").uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dayNumberFormat.format(date),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}
