package com.project.vetpet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.model.Pet
import com.project.vetpet.view.pets.MyPetsViewModel

class PetAdapter(private val listener: EditButtonClickListener,
                 private val list: MutableList<Pet>,
                 private val viewModel: MyPetsViewModel,
                 val context: Context): RecyclerView.Adapter<PetAdapter.ItemHolder>() {

    class ItemHolder(view: View): RecyclerView.ViewHolder(view){
        val photo: ImageView = view.findViewById(R.id.petPhoto)
        val name: TextView = view.findViewById(R.id.petName)
        val age: TextView = view.findViewById(R.id.petAge)
        val breed: TextView = view.findViewById(R.id.petBreed)
        val btn: ImageButton = view.findViewById(R.id.petInfoEditBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.design_pet_block,parent,false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {return list.count()}


    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.name.text = list[position].name
        holder.age.text = list[position].age.toString()
        holder.breed.text = list[position].breed

        holder.photo.setImageResource(R.drawable.ic_cat_photo)

        holder.btn.setOnClickListener {
            viewModel.setSelectedItem(list[position])
            listener.onEditButtonClick(position)
        }
    }
}

interface EditButtonClickListener {
    fun onEditButtonClick(position: Int)
}