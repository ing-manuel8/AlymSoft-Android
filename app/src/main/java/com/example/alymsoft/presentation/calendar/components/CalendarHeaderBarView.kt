package com.example.alymsoft.presentation.calendar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CalendarHeaderBarView(
    selectedDate: Date,
    viewMode: CalendarViewMode,
    isLoading: Boolean,
    onViewModeSelected: (CalendarViewMode) -> Unit,
    onOpenDatePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es")) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Izquierda: Titulo Mes y Año + Icono de Calendario (Abrir DatePicker)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.clickable { onOpenDatePicker() }
        ) {
            Text(
                text = monthYearFormat.format(selectedDate).replaceFirstChar { it.uppercase() },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = PrimaryBlue
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Seleccionar Fecha",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Derecha: Control Segmentado de Vista (Dia, Semana, Mes)
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.width(190.dp)
        ) {
            CalendarViewMode.values().forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = (viewMode == mode),
                    onClick = { onViewModeSelected(mode) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = CalendarViewMode.values().size
                    )
                ) {
                    Text(
                        text = mode.label,
                        fontSize = 12.sp,
                        fontWeight = if (viewMode == mode) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
