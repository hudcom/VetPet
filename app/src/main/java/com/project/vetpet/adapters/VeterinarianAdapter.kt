package com.project.vetpet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.model.Veterinarian

class VeterinarianAdapter (private val listener: MoreButtonClickListener,
                           private val list: MutableList<Veterinarian>,
                           val context: Context
): RecyclerView.Adapter<VeterinarianAdapter.ItemHolder>() {

    class ItemHolder(view: View): RecyclerView.ViewHolder(view){
        val vetBlock:ConstraintLayout = view.findViewById(R.id.vetBlock)
        val vetPhoto: ImageView = view.findViewById(R.id.photo)
        val vetName: TextView = view.findViewById(R.id.nameTextView)
        val vetWorkplace: TextView = view.findViewById(R.id.workplaceTextView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.design_veteririnarian_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.vetName.text = list[position].fullName
        holder.vetWorkplace.text = list[position].workplace

        holder.vetPhoto.setImageResource(R.drawable.ic_bm_profile)

        holder.vetBlock.setOnClickListener{
            listener.onMoreButtonClick(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface MoreButtonClickListener {
    fun onMoreButtonClick(veterinarian: Veterinarian)
}