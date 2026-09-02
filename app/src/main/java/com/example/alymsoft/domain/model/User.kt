package com.example.alymsoft.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId", alternate = ["id"])
    val id: Int,

    val name: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val role: String = "owner",
    val companyId: Int? = null,
    val companyName: String? = null,
    val timeZone: String? = "Central Standard Time (Mexico)",
    val timeZoneIANA: String? = "America/Mexico_City",
    val professionalId: Int? = null,
    val photoUrl: String? = null,
    val licenseStartDate: String? = null,
    val licenseExpirationDate: String? = null,
    val licenseStatus: Int? = null,
    val daysRemaining: Int? = null,
    val planCode: String? = null,
    val autoRenewal: String? = null
)
