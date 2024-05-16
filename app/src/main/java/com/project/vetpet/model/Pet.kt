package com.project.vetpet.model

import android.os.Bundle

data class Pet(
    var name:String?,
    val age: Int?,
    val type: String?,
    val breed: String?,
    val owner: String? ){
    constructor(): this(null,null,null,null,null)

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putInt("age", age ?: -1) // Використовуємо -1 як значення за замовчуванням для Int, якщо age == null
        bundle.putString("type", type)
        bundle.putString("breed", breed)
        bundle.putString("owner", owner)
        return bundle
    }

    companion object {
        fun fromBundle(bundle: Bundle?): Pet {
            return if (bundle != null) {
                Pet(
                    bundle.getString("name"),
                    bundle.getInt("age", -1).takeIf { it != -1 }, // Використовуємо takeIf для перевірки -1
                    bundle.getString("type"),
                    bundle.getString("breed"),
                    bundle.getString("owner")
                )
            } else {
                Pet()
            }
        }
    }
}