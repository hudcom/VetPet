package com.project.vetpet.model

import java.io.Serializable

data class Veterinarian(
    var fullName:String,
    var workplace:String,
) : Serializable{
    constructor(): this("","")
}