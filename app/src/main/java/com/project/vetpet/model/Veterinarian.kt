package com.project.vetpet.model

import java.io.Serializable
import java.net.Inet4Address

data class Veterinarian(
    var fullName:String,
    var workplace:String,
    var address: String
) : Serializable{
    constructor(): this("","","")
}