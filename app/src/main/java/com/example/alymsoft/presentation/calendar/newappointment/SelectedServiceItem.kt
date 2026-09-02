package com.example.alymsoft.presentation.calendar.newappointment

import java.util.UUID

data class SelectedServiceItem(
    val id: String = UUID.randomUUID().toString(),
    val serviceId: Int,
    var quantity: Int = 1
)
