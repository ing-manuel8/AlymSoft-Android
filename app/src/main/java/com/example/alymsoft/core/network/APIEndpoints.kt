package com.example.alymsoft.core.network

object APIEndpoints {
    const val BASE_URL = "https://api.alymsoft.com/api/"

    object Auth {
        const val LOGIN = "auth/login"
        const val LOGOUT = "auth/logout"
        const val CHECK_SESSION = "auth/check-session"
        const val FORGOT_PASSWORD = "auth/forgot-password"
        const val RESET_PASSWORD = "auth/reset-password"
    }

    object Calendar {
        const val APPOINTMENTS = "calendar/appointments"
        const val UPCOMING = "calendar/appointments/upcoming"
        const val PROFESSIONALS = "calendar/professionals"
        const val SERVICES = "calendar/services"
        const val CLIENTS = "calendar/clients"
        const val PAYMENT_METHODS = "calendar/payment-methods"
        const val PAYMENT_TYPES = "calendar/payment-types"
        const val STATS = "calendar/stats"
        const val OCCUPIED_SLOTS = "calendar/occupied-slots"

        fun confirm(id: Int) = "calendar/appointments/$id/confirm"
        fun cancel(id: Int) = "calendar/appointments/$id/cancel"
        fun markAttendance(id: Int) = "calendar/appointments/$id/mark-attendance"
        fun reschedule(id: Int) = "calendar/appointments/$id/reschedule"
        fun details(id: Int) = "calendar/appointments/$id"
        fun edit(id: Int) = "calendar/appointments/$id/edit"
    }

    object Appointments {
        const val BASE = "appointment"
        const val MY_APPOINTMENTS = "appointment/my-appointments"
    }

    object Customers {
        const val BASE = "customerClient"
    }

    object Services {
        const val BASE = "service"
    }

    object Companies {
        const val ADVANCED_CONFIG = "companies/getAdvancedConfig"
    }
}
