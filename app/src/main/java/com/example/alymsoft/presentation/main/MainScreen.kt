package com.example.alymsoft.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alymsoft.domain.model.User
import com.example.alymsoft.presentation.calendar.CalendarScreen
import com.example.alymsoft.presentation.calendar.CalendarViewModel
import com.example.alymsoft.presentation.customers.CustomersScreen
import com.example.alymsoft.presentation.profile.ProfileScreen
import com.example.alymsoft.presentation.services.ServicesScreen
import com.example.alymsoft.ui.theme.PrimaryBlue

import androidx.compose.material.icons.filled.List
import com.example.alymsoft.presentation.appointments.AppointmentsListScreen

sealed class NavigationTab(val route: String, val title: String, val icon: ImageVector) {
    object Agenda : NavigationTab("agenda", "Agenda", Icons.Default.CalendarMonth)
    object Appointments : NavigationTab("appointments", "Citas", Icons.Default.List)
    object Customers : NavigationTab("customers", "Clientes", Icons.Default.People)
    object Services : NavigationTab("services", "Servicios", Icons.Default.ContentCut)
    object Profile : NavigationTab("profile", "Perfil", Icons.Default.Person)
}

@Composable
fun MainScreen(
    user: User?,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf<NavigationTab>(NavigationTab.Agenda) }
    val calendarViewModel: CalendarViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    NavigationTab.Agenda,
                    NavigationTab.Appointments,
                    NavigationTab.Customers,
                    NavigationTab.Services,
                    NavigationTab.Profile
                )
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = PrimaryBlue.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                NavigationTab.Agenda -> CalendarScreen(viewModel = calendarViewModel)
                NavigationTab.Appointments -> AppointmentsListScreen(viewModel = calendarViewModel)
                NavigationTab.Customers -> CustomersScreen()
                NavigationTab.Services -> ServicesScreen()
                NavigationTab.Profile -> ProfileScreen(user = user, onLogoutClick = onLogoutClick)
            }
        }
    }
}
