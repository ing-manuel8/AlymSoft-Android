package com.example.alymsoft.presentation.calendar.newappointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.ui.theme.PrimaryBlue

@Composable
fun NewAppointmentClientSection(
    selectedClientId: Int?,
    customerName: String,
    customerPhone: String,
    onCustomerNameChange: (String) -> Unit,
    onCustomerPhoneChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                text = "Información del Cliente",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenSearch,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buscar Cliente", fontSize = 11.sp, color = PrimaryBlue)
                }

                OutlinedButton(
                    onClick = onOpenQuickAdd,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nuevo Cliente", fontSize = 11.sp, color = PrimaryBlue)
                }
            }

            OutlinedTextField(
                value = customerName,
                onValueChange = onCustomerNameChange,
                label = { Text("Nombre del Cliente / Visitante") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = customerPhone,
                onValueChange = onCustomerPhoneChange,
                label = { Text("Teléfono / WhatsApp") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
