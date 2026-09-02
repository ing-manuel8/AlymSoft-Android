package com.example.alymsoft.presentation.calendar.newappointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAppointmentProfessionalSection(
    selectedProfessionalId: Int,
    professionalsList: List<CalendarProfessionalDTO>,
    onSelectProfessional: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProf = professionalsList.firstOrNull { it.professionalId == selectedProfessionalId }
    val selectedName = selectedProf?.fullName ?: selectedProf?.name ?: "Seleccionar Profesional"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Profesional Asignado",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedName,
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Default.Work, contentDescription = null, tint = PrimaryBlue)
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    professionalsList.forEach { prof ->
                        val name = prof.fullName ?: prof.name ?: "Profesional ${prof.professionalId}"
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                onSelectProfessional(prof.professionalId)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
