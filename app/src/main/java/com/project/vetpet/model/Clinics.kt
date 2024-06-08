package com.project.vetpet.model

import java.io.Serializable

data class Clinics(
    val email: String,
    val password: String,
    val name: String,
    val address: String,
    val employee: List<String> = emptyList()
): Serializable {
    constructor(): this("","","","")

    companion object{
        var currentClinic:Clinics? = null
    }
}
