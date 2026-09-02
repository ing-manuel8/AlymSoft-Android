package com.example.alymsoft.data.dto

import com.example.alymsoft.domain.model.Appointment
import com.example.alymsoft.domain.model.AppointmentStatus
import com.google.gson.annotations.SerializedName

data class CalendarAppointmentListWrapper(
    @SerializedName("appointments", alternate = ["Appointments", "data", "Data"])
    val appointments: List<CalendarAppointmentDTO>? = null
)

data class CalendarAppointmentDTO(
    @SerializedName("appointmentId", alternate = ["AppointmentId", "idCita", "id"])
    val appointmentId: Int = 0,

    @SerializedName("appointmentDate", alternate = ["AppointmentDate", "fechaCita"])
    val appointmentDate: String? = null,

    @SerializedName("startTime", alternate = ["StartTime", "horaInicio"])
    val startTime: String? = null,

    @SerializedName("endTime", alternate = ["EndTime", "horaFin"])
    val endTime: String? = null,

    @SerializedName("clientName", alternate = ["ClientName"])
    val clientName: String? = null,

    @SerializedName("clientLastName", alternate = ["ClientLastName"])
    val clientLastName: String? = null,

    @SerializedName("clientPhone", alternate = ["ClientPhone"])
    val clientPhone: String? = null,

    @SerializedName("professionalName", alternate = ["ProfessionalName"])
    val professionalName: String? = null,

    @SerializedName("professionalLastName", alternate = ["ProfessionalLastName"])
    val professionalLastName: String? = null,

    @SerializedName("professionalId", alternate = ["ProfessionalId"])
    val professionalId: Int? = null,

    @SerializedName("status", alternate = ["Status"])
    val status: String? = null,

    @SerializedName("isBlockedSlot", alternate = ["IsBlockedSlot"])
    val isBlockedSlot: Boolean? = null,

    @SerializedName("requiresConfirmation", alternate = ["RequiresConfirmation"])
    val requiresConfirmation: Boolean? = null,

    @SerializedName("serviceName", alternate = ["ServiceName"])
    val serviceName: String? = null,

    @SerializedName("totalPrice", alternate = ["TotalPrice", "finalPrice", "FinalPrice"])
    val totalPrice: Double? = null,

    @SerializedName("notes", alternate = ["Notes"])
    val notes: String? = null
) {
    fun toDomain(): Appointment {
        val fullNameCustomer = buildString {
            if (!clientName.isNullOrBlank()) append(clientName)
            if (!clientLastName.isNullOrBlank()) {
                if (isNotEmpty()) append(" ")
                append(clientLastName)
            }
        }.ifEmpty { "Cliente General" }

        val fullNameProf = buildString {
            if (!professionalName.isNullOrBlank()) append(professionalName)
            if (!professionalLastName.isNullOrBlank()) {
                if (isNotEmpty()) append(" ")
                append(professionalLastName)
            }
        }.ifEmpty { "Profesional" }

        val titleText = if (!serviceName.isNullOrBlank()) serviceName else "Cita en Agenda"

        return Appointment(
            id = appointmentId,
            title = titleText,
            customerName = fullNameCustomer,
            serviceName = serviceName ?: "Servicio General",
            date = appointmentDate ?: "",
            startTime = parseTimeString(startTime, "09:00"),
            endTime = parseTimeString(endTime, "10:00"),
            status = AppointmentStatus.mapFrom(status),
            notes = notes,
            price = totalPrice,
            professionalId = professionalId,
            professionalName = fullNameProf,
            customerPhone = clientPhone
        )
    }

    private fun parseTimeString(raw: String?, fallback: String): String {
        if (raw.isNullOrBlank()) return fallback
        val parts = raw.split("T").last().split(":")
        return if (parts.size >= 2) "${parts[0]}:${parts[1]}" else raw
    }
}

data class AppointmentServiceItemDTO(
    @SerializedName("serviceId", alternate = ["ServiceId", "idServicio"])
    val serviceId: Int,

    @SerializedName("quantity", alternate = ["Quantity", "cantidad"])
    val quantity: Int = 1
)

data class RegisterAppointmentRequestDTO(
    @SerializedName("clientId", alternate = ["ClientId"])
    val clientId: Int? = null,

    @SerializedName("visitorName", alternate = ["VisitorName"])
    val visitorName: String = "",

    @SerializedName("visitorPhone", alternate = ["VisitorPhone"])
    val visitorPhone: String = "",

    @SerializedName("visitorEmail", alternate = ["VisitorEmail"])
    val visitorEmail: String = "",

    @SerializedName("professionalId", alternate = ["ProfessionalId"])
    val professionalId: Int,

    @SerializedName("appointmentDate", alternate = ["AppointmentDate"])
    val appointmentDate: String,

    @SerializedName("startTime", alternate = ["StartTime"])
    val startTime: String,

    @SerializedName("endTime", alternate = ["EndTime"])
    val endTime: String,

    @SerializedName("services", alternate = ["Services"])
    val services: List<AppointmentServiceItemDTO>,

    @SerializedName("reservationType", alternate = ["ReservationType"])
    val reservationType: String = "Presencial",

    @SerializedName("requiresConfirmation", alternate = ["RequiresConfirmation"])
    val requiresConfirmation: Boolean = true,

    @SerializedName("allowsRescheduling", alternate = ["AllowsRescheduling"])
    val allowsRescheduling: Boolean = true,

    @SerializedName("sendReminder", alternate = ["SendReminder"])
    val sendReminder: Boolean = true,

    @SerializedName("allowsOverlap", alternate = ["AllowsOverlap"])
    val allowsOverlap: Boolean = false,

    @SerializedName("allowsClientDuplicate", alternate = ["AllowsClientDuplicate"])
    val allowsClientDuplicate: Boolean = false,

    @SerializedName("description", alternate = ["Description"])
    val description: String = "",

    @SerializedName("clientNotes", alternate = ["ClientNotes"])
    val clientNotes: String = "",

    @SerializedName("alreadyPaid", alternate = ["AlreadyPaid"])
    val alreadyPaid: Boolean = false,

    @SerializedName("discountAmount", alternate = ["DiscountAmount"])
    val discountAmount: Double = 0.0,

    @SerializedName("paidAmount", alternate = ["PaidAmount"])
    val paidAmount: Double? = null,

    @SerializedName("requiresInvoice", alternate = ["RequiresInvoice"])
    val requiresInvoice: Boolean = false,

    @SerializedName("clientRFC", alternate = ["ClientRFC"])
    val clientRFC: String = "",

    @SerializedName("clientLegalName", alternate = ["ClientLegalName"])
    val clientLegalName: String = ""
)

data class RegisterAppointmentResponseWrapper(
    @SerializedName("appointmentId", alternate = ["AppointmentId", "idCita", "id"])
    val appointmentId: Int = 0,

    @SerializedName("serviceName", alternate = ["ServiceName"])
    val serviceName: String? = null,

    @SerializedName("clientName", alternate = ["ClientName"])
    val clientName: String? = null
)

data class OccupiedSlotDTO(
    @SerializedName("startTime", alternate = ["StartTime"])
    val startTime: String = "",

    @SerializedName("endTime", alternate = ["EndTime"])
    val endTime: String = "",

    @SerializedName("professionalId", alternate = ["ProfessionalId"])
    val professionalId: Int? = null,

    @SerializedName("professionalName", alternate = ["ProfessionalName"])
    val professionalName: String? = null
)

data class CalendarProfessionalListWrapper(
    @SerializedName("professionals", alternate = ["Professionals"])
    val professionals: List<CalendarProfessionalDTO>? = null
)

data class CalendarProfessionalDTO(
    @SerializedName("professionalId", alternate = ["ProfessionalId", "idProfesional"])
    val professionalId: Int = 0,

    @SerializedName("name", alternate = ["Name"])
    val name: String? = null,

    @SerializedName("lastName", alternate = ["LastName"])
    val lastName: String? = null,

    @SerializedName("fullName", alternate = ["FullName"])
    val fullName: String? = null,

    @SerializedName("specialty", alternate = ["Specialty"])
    val specialty: String? = null,

    @SerializedName("color", alternate = ["Color"])
    val color: String? = null,

    @SerializedName("photo", alternate = ["Photo"])
    val photo: String? = null,

    @SerializedName("phone", alternate = ["Phone"])
    val phone: String? = null
)

data class CalendarServiceListWrapper(
    @SerializedName("services", alternate = ["Services"])
    val services: List<CalendarServiceDTO>? = null
)

data class CalendarServiceDTO(
    @SerializedName("serviceId", alternate = ["ServiceId", "idServicio"])
    val serviceId: Int = 0,

    @SerializedName("name", alternate = ["Name"])
    val name: String? = null,

    @SerializedName("categoryName", alternate = ["CategoryName", "categoria"])
    val categoryName: String? = null,

    @SerializedName("price", alternate = ["Price"])
    val price: Double? = null,

    @SerializedName("durationMinutes", alternate = ["DurationMinutes"])
    val durationMinutes: Int? = null
)

data class CalendarClientListWrapper(
    @SerializedName("clients", alternate = ["Clients"])
    val clients: List<CalendarClientDTO>? = null
)

data class CalendarClientDTO(
    @SerializedName("clientId", alternate = ["ClientId", "idCliente"])
    val clientId: Int = 0,

    @SerializedName("name", alternate = ["Name"])
    val name: String? = null,

    @SerializedName("phone", alternate = ["Phone"])
    val phone: String? = null,

    @SerializedName("email", alternate = ["Email"])
    val email: String? = null
)
