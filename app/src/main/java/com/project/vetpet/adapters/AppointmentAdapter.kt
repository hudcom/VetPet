package com.project.vetpet.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.model.AppointmentSlot
import com.project.vetpet.view.TAG

class AppointmentAdapter(
    private val slots: List<AppointmentSlot>,
    private val onBookClick: (AppointmentSlot, Int) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.appointmentTime)
        val bookButton: Button = itemView.findViewById(R.id.bookButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.appointment_slot_item, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        if (position < slots.size) {
            val slot = slots[position]
            holder.timeTextView.text = slot.time
            holder.bookButton.text = if (slot.isBooked) "Зайнято" else "Записатися"
            if (slot.isBooked) {
                holder.bookButton.setBackgroundResource(R.drawable.style_btn_red)
            } else {
                holder.bookButton.setBackgroundResource(R.drawable.style_btn_purple)
            }

            holder.bookButton.setOnClickListener {
                onBookClick(slot, position)
            }
        }
    }

    override fun getItemCount(): Int = slots.size
}