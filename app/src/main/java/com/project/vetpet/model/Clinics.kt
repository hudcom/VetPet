package com.project.vetpet.model

import java.io.Serializable

data class Clinics(
    val name: String,
    val address: String,
    val employee: List<String> = emptyList()
): Serializable {
    constructor(): this("","")
}
