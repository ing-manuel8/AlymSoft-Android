package com.example.alymsoft.presentation.calendar.newappointment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alymsoft.data.dto.CalendarServiceDTO

@Composable
fun NewAppointmentServicesSection(
    servicesList: List<CalendarServiceDTO>,
    selectedServices: List<SelectedServiceItem>,
    totalDurationMinutes: Int,
    totalPrice: Double,
    onAddService: (Int) -> Unit,
    onRemoveService: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedServices = remember(servicesList) {
        servicesList.groupBy { it.categoryName ?: "General" }
    }

    var expandedCategories by remember(groupedServices) {
        mutableStateOf(groupedServices.keys.toSet())
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SelectedServicesList(
            selectedServices = selectedServices,
            servicesList = servicesList,
            totalDurationMinutes = totalDurationMinutes,
            totalPrice = totalPrice,
            onAddQuantity = { sId -> onAddService(sId) },
            onRemoveQuantity = { sId -> onRemoveService(sId) },
            onDeleteService = { sId ->
                val item = selectedServices.firstOrNull { it.serviceId == sId }
                item?.let {
                    repeat(it.quantity) { onRemoveService(sId) }
                }
            }
        )

        Text(
            text = "Catálogo de Servicios",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
        )

        groupedServices.keys.sorted().forEach { category ->
            val categoryServices = groupedServices[category] ?: emptyList()
            val isExpanded = expandedCategories.contains(category)

            ServiceCatalogCategoryCard(
                category = category,
                categoryServices = categoryServices,
                isExpanded = isExpanded,
                onToggleExpand = {
                    expandedCategories = if (isExpanded) {
                        expandedCategories - category
                    } else {
                        expandedCategories + category
                    }
                },
                onAddService = { sId -> onAddService(sId) }
            )
        }
    }
}
