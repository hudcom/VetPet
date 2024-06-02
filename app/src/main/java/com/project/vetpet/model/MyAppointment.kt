package com.project.vetpet.model

import android.os.Parcel
import android.os.Parcelable

data class MyAppointment(
    val doctor: String,
    val address: String,
    val date: String,
    val time: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(doctor)
        parcel.writeString(address)
        parcel.writeString(date)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyAppointment> {
        override fun createFromParcel(parcel: Parcel): MyAppointment {
            return MyAppointment(parcel)
        }

        override fun newArray(size: Int): Array<MyAppointment?> {
            return arrayOfNulls(size)
        }
    }
}