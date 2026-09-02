package com.example.alymsoft.presentation.calendar.newappointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.alymsoft.data.dto.*
import com.example.alymsoft.data.repository.CalendarRepositoryImpl
import com.example.alymsoft.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAppointmentSheet(
    initialDate: Date = Date(),
    initialProfessionalId: Int? = null,
    onAppointmentCreated: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { CalendarRepositoryImpl(context) }
    val scope = rememberCoroutineScope()

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var selectedClientId by remember { mutableStateOf<Int?>(null) }

    var selectedProfessionalId by remember { mutableStateOf(initialProfessionalId ?: 0) }
    var appointmentDate by remember { mutableStateOf(initialDate) }
    var startTime by remember { mutableStateOf(initialDate) }
    var notes by remember { mutableStateOf("") }

    var selectedServices by remember { mutableStateOf<List<SelectedServiceItem>>(emptyList()) }
    var servicesList by remember { mutableStateOf<List<CalendarServiceDTO>>(emptyList()) }
    var professionalsList by remember { mutableStateOf<List<CalendarProfessionalDTO>>(emptyList()) }
    var clientsList by remember { mutableStateOf<List<CalendarClientDTO>>(emptyList()) }

    var isLoadingCatalogs by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showingClientSearchDialog by remember { mutableStateOf(false) }
    var showingQuickAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            val sRes = repository.getServices()
            val pRes = repository.getProfessionals()
            val cRes = repository.getClients()

            servicesList = sRes.getOrDefault(emptyList())
            professionalsList = pRes.getOrDefault(emptyList())
            clientsList = cRes.getOrDefault(emptyList())

            if (selectedProfessionalId == 0) {
                selectedProfessionalId = professionalsList.firstOrNull()?.professionalId ?: 0
            }
            isLoadingCatalogs = false
        }
    }

    val totalDurationMinutes = remember(selectedServices, servicesList) {
        selectedServices.sumOf { item ->
            val s = servicesList.firstOrNull { it.serviceId == item.serviceId }
            (s?.durationMinutes ?: 45) * item.quantity
        }
    }

    val totalPrice = remember(selectedServices, servicesList) {
        selectedServices.sumOf { item ->
            val s = servicesList.firstOrNull { it.serviceId == item.serviceId }
            (s?.price ?: 0.0) * item.quantity
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nueva Cita", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    if (isLoadingCatalogs) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            errorMessage?.let { msg ->
                                Text(msg, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            NewAppointmentClientSection(
                                selectedClientId = selectedClientId,
                                customerName = customerName,
                                customerPhone = customerPhone,
                                onCustomerNameChange = { customerName = it },
                                onCustomerPhoneChange = { customerPhone = it },
                                onOpenSearch = { showingClientSearchDialog = true },
                                onOpenQuickAdd = { showingQuickAddDialog = true }
                            )

                            NewAppointmentProfessionalSection(
                                selectedProfessionalId = selectedProfessionalId,
                                professionalsList = professionalsList,
                                onSelectProfessional = { selectedProfessionalId = it }
                            )

                            NewAppointmentDateTimeSection(
                                appointmentDate = appointmentDate,
                                startTime = startTime,
                                totalDurationMinutes = totalDurationMinutes,
                                onDateSelected = { appointmentDate = it },
                                onTimeSelected = { startTime = it }
                            )

                            NewAppointmentServicesSection(
                                servicesList = servicesList,
                                selectedServices = selectedServices,
                                totalDurationMinutes = totalDurationMinutes,
                                totalPrice = totalPrice,
                                onAddService = { sId ->
                                    val list = selectedServices.toMutableList()
                                    val idx = list.indexOfFirst { it.serviceId == sId }
                                    if (idx >= 0) {
                                        list[idx] = list[idx].copy(quantity = list[idx].quantity + 1)
                                    } else {
                                        list.add(SelectedServiceItem(serviceId = sId, quantity = 1))
                                    }
                                    selectedServices = list
                                },
                                onRemoveService = { sId ->
                                    val list = selectedServices.toMutableList()
                                    val idx = list.indexOfFirst { it.serviceId == sId }
                                    if (idx >= 0) {
                                        if (list[idx].quantity > 1) {
                                            list[idx] = list[idx].copy(quantity = list[idx].quantity - 1)
                                        } else {
                                            list.removeAt(idx)
                                        }
                                    }
                                    selectedServices = list
                                }
                            )

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Notas u Observaciones") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }

                        Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Button(
                                    onClick = {
                                        if (selectedServices.isEmpty()) {
                                            errorMessage = "Selecciona al menos un servicio"
                                            return@Button
                                        }
                                        if (selectedProfessionalId == 0) {
                                            errorMessage = "Selecciona un profesional"
                                            return@Button
                                        }

                                        isSubmitting = true
                                        errorMessage = null

                                        scope.launch {
                                            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                                            val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

                                            val startStr = timeFormat.format(startTime)
                                            val calEnd = java.util.Calendar.getInstance().apply {
                                                time = startTime
                                                add(java.util.Calendar.MINUTE, if (totalDurationMinutes > 0) totalDurationMinutes else 45)
                                            }
                                            val endStr = timeFormat.format(calEnd.time)

                                            val req = RegisterAppointmentRequestDTO(
                                                clientId = selectedClientId,
                                                visitorName = if (selectedClientId == null) (customerName.ifEmpty { "Cliente Visitante" }) else "",
                                                visitorPhone = customerPhone,
                                                professionalId = selectedProfessionalId,
                                                appointmentDate = isoFormat.format(appointmentDate),
                                                startTime = startStr,
                                                endTime = endStr,
                                                services = selectedServices.map { AppointmentServiceItemDTO(it.serviceId, it.quantity) },
                                                clientNotes = notes
                                            )

                                            val res = repository.registerAppointment(req)
                                            isSubmitting = false
                                            res.onSuccess {
                                                onAppointmentCreated()
                                                onDismiss()
                                            }.onFailure { err ->
                                                errorMessage = err.localizedMessage ?: "Error al registrar cita"
                                            }
                                        }
                                    },
                                    enabled = !isSubmitting,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Agendar Cita", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showingClientSearchDialog) {
        ClientSearchDialog(
            clientsList = clientsList,
            onSelectClient = { client ->
                if (client != null) {
                    selectedClientId = client.clientId
                    customerName = client.name ?: ""
                    customerPhone = client.phone ?: ""
                } else {
                    selectedClientId = null
                    customerName = ""
                    customerPhone = ""
                }
            },
            onDismiss = { showingClientSearchDialog = false }
        )
    }

    if (showingQuickAddDialog) {
        QuickAddClientDialog(
            onSaveNewClient = { name, phone ->
                customerName = name
                customerPhone = phone
                selectedClientId = null
            },
            onDismiss = { showingQuickAddDialog = false }
        )
    }
}
