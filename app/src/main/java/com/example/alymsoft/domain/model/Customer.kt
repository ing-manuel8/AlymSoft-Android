package com.example.alymsoft.domain.model

import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("customerId", alternate = ["id"])
    val id: Int,

    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val notes: String? = null,
    val totalAppointments: Int? = 0
)
