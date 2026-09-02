package com.example.alymsoft.presentation.calendar.newappointment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.data.dto.CalendarClientDTO
import com.example.alymsoft.ui.theme.PrimaryBlue

@Composable
fun ClientSearchDialog(
    clientsList: List<CalendarClientDTO>,
    onSelectClient: (CalendarClientDTO?) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredClients = remember(searchQuery, clientsList) {
        if (searchQuery.isBlank()) clientsList
        else clientsList.filter { client ->
            val nameMatch = (client.name ?: "").contains(searchQuery, ignoreCase = true)
            val phoneMatch = (client.phone ?: "").contains(searchQuery)
            nameMatch || phoneMatch
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Buscar Cliente", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nombre o teléfono...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    item {
                        ListItem(
                            headlineContent = { Text("Cliente Visitante", fontWeight = FontWeight.Bold) },
                            leadingContent = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue) },
                            modifier = Modifier.clickable {
                                onSelectClient(null)
                                onDismiss()
                            }
                        )
                        HorizontalDivider()
                    }

                    items(filteredClients, key = { it.clientId }) { client ->
                        ListItem(
                            headlineContent = { Text(client.name ?: "Sin Nombre", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { client.phone?.let { Text(it) } },
                            modifier = Modifier.clickable {
                                onSelectClient(client)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}
