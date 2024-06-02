package com.project.vetpet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.model.MyAppointment

class MyAppointmentAdapter(private val appointments: List<MyAppointment>) :
    RecyclerView.Adapter<MyAppointmentAdapter.MyAppointmentViewHolder>() {

    class MyAppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorTextView: TextView = itemView.findViewById(R.id.doctorTextView)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_appointment, parent, false)
        return MyAppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyAppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.doctorTextView.text = appointment.doctor
        holder.addressTextView.text = appointment.address
        holder.dateTextView.text = appointment.date
        holder.timeTextView.text = appointment.time
    }

    override fun getItemCount(): Int = appointments.size
}