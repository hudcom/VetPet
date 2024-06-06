package com.project.vetpet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.model.Clinics

class ClinicsAdapter(
    private val listener: MoreClinicButtonClickListener,
    private val list: MutableList<Clinics>,
    val context: Context
) : RecyclerView.Adapter<ClinicsAdapter.ItemHolder>() {

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clinicBlock: ConstraintLayout = view.findViewById(R.id.clinicBlock)
        val clinicName: TextView = view.findViewById(R.id.clinicNameTextView)
        val clinicAddress: TextView = view.findViewById(R.id.clinicAddressTextView)
        val moreButton: Button = view.findViewById(R.id.moreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.design_clinic_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.clinicName.text = list[position].name
        holder.clinicAddress.text = list[position].address

        holder.moreButton.setOnClickListener {
            listener.onMoreButtonClick(list[position])
        }

        holder.clinicBlock.setOnClickListener {
            listener.onMoreButtonClick(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface MoreClinicButtonClickListener {
    fun onMoreButtonClick(clinic: Clinics)
}