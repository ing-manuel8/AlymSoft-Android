package com.example.alymsoft.data.dto

import com.example.alymsoft.domain.model.User
import com.google.gson.annotations.SerializedName

data class LoginRequestDTO(
    @SerializedName("Identificador", alternate = ["identificador", "email", "username"])
    val identificador: String,

    @SerializedName("Password", alternate = ["password", "clave"])
    val password: String
)

data class ModernLoginResponseDTO(
    @SerializedName("user", alternate = ["User"])
    val user: UserDataDTO = UserDataDTO(),

    @SerializedName("token", alternate = ["Token"])
    val token: String = "",

    @SerializedName("refreshToken", alternate = ["RefreshToken"])
    val refreshToken: String = ""
)

data class UserDataDTO(
    @SerializedName("userId", alternate = ["UserId", "id"])
    val userId: Int = 0,

    @SerializedName("firstName", alternate = ["FirstName"])
    val firstName: String = "",

    @SerializedName("lastName", alternate = ["LastName"])
    val lastName: String? = null,

    @SerializedName("name", alternate = ["Name"])
    val name: String = "",

    @SerializedName("email", alternate = ["Email"])
    val email: String = "",

    @SerializedName("phone", alternate = ["Phone"])
    val phone: String? = null,

    @SerializedName("role", alternate = ["Role"])
    val role: String = "owner",

    @SerializedName("companyId", alternate = ["CompanyId"])
    val companyId: Int = 0,

    @SerializedName("companyName", alternate = ["CompanyName"])
    val companyName: String? = null,

    @SerializedName("timeZone", alternate = ["TimeZone"])
    val timeZone: String? = null,

    @SerializedName("timeZoneIANA", alternate = ["TimeZoneIANA"])
    val timeZoneIANA: String? = null,

    @SerializedName("professionalId", alternate = ["ProfessionalId"])
    val professionalId: Int? = null,

    @SerializedName("photoUrl", alternate = ["PhotoUrl"])
    val photoUrl: String? = null,

    @SerializedName("licenseStartDate", alternate = ["LicenseStartDate"])
    val licenseStartDate: String? = null,

    @SerializedName("licenseExpirationDate", alternate = ["LicenseExpirationDate"])
    val licenseExpirationDate: String? = null,

    @SerializedName("licenseStatus", alternate = ["LicenseStatus"])
    val licenseStatus: Int? = null,

    @SerializedName("daysRemaining", alternate = ["DaysRemaining"])
    val daysRemaining: Int? = null,

    @SerializedName("planCode", alternate = ["PlanCode"])
    val planCode: String? = null,

    @SerializedName("autoRenewal", alternate = ["AutoRenewal"])
    val autoRenewal: String? = null
) {
    fun toDomainUser(): User {
        return User(
            id = userId,
            firstName = firstName,
            lastName = lastName,
            name = if (name.isNotBlank()) name else "$firstName ${lastName ?: ""}".trim(),
            email = email,
            phone = phone,
            role = role,
            companyId = companyId,
            companyName = companyName,
            timeZone = timeZone,
            timeZoneIANA = timeZoneIANA,
            professionalId = professionalId,
            photoUrl = photoUrl,
            licenseStartDate = licenseStartDate,
            licenseExpirationDate = licenseExpirationDate,
            licenseStatus = licenseStatus,
            daysRemaining = daysRemaining,
            planCode = planCode,
            autoRenewal = autoRenewal
        )
    }
}
