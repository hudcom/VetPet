package com.project.vetpet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.utils.ItemClickListener
import com.project.vetpet.utils.MenuItem

class MenuItemAdapter(private var list: List<MenuItem>,
                      private val listener: ItemClickListener,
                      var context: Context
)
    : RecyclerView.Adapter<MenuItemAdapter.ItemHolder>() {


    class ItemHolder(view: View): RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.menu_item_image)
        val title: TextView = view.findViewById(R.id.menu_item_title)
        val layout: RelativeLayout = view.findViewById(R.id.menu_item_layout)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.design_menu_item,parent,false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.title.text = list[position].title

        // Знаходимо id картинки перед меню
        val imageId =
            context.resources.getIdentifier(list[position].image, "drawable", context.packageName)

        holder.image.setImageResource(imageId)

        holder.layout.setOnClickListener {
            listener.onItemClickListener(list[position].title)
        }
    }

}

