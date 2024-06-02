package com.project.vetpet.model

data class AppointmentSlot(
    val day: String,
    val time: String,
    var isBooked: Boolean = false,
    var client: String? = null
)