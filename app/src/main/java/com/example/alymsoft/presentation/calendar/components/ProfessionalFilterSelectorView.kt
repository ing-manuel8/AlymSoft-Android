package com.example.alymsoft.presentation.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.data.dto.CalendarProfessionalDTO
import com.example.alymsoft.ui.theme.PrimaryBlue

@Composable
fun ProfessionalFilterSelectorView(
    professionals: List<CalendarProfessionalDTO>,
    selectedId: Int?,
    onSelect: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            val isSelected = (selectedId == null)
            ProfessionalChip(
                name = "Todos los profesionales",
                isSelected = isSelected,
                onClick = { onSelect(null) }
            )
        }

        items(professionals, key = { it.professionalId }) { prof ->
            val isSelected = (selectedId == prof.professionalId)
            val displayName = prof.fullName ?: prof.name ?: "Profesional"
            ProfessionalChip(
                name = displayName,
                isSelected = isSelected,
                onClick = { onSelect(prof.professionalId) }
            )
        }
    }
}

@Composable
private fun ProfessionalChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = if (isSelected) Color.White else PrimaryBlue,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            softWrap = false
        )
    }
}
