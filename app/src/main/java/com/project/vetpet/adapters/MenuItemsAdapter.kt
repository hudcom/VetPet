package com.project.vetpet.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.utils.MenuItem

class MenuItemsAdapter(private var items: List<MenuItem>, private val callback: AccountFragmentCallback, var context: Context): RecyclerView.Adapter<MenuItemsAdapter.ItemHolder>() {

    // Знаходимо графічні елементи з якими будемо працювати
    class ItemHolder(view: View): RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.menu_item_image)
        val title: TextView = view.findViewById(R.id.menu_item_title)
        val layout: RelativeLayout = view.findViewById(R.id.menu_item_layout)
    }

    // В цьому методі визначаємо дизайн для кожного елементу меню
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.design_menu_item,parent,false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
}

    // Вказуємо які дані підставляємо в кожний елемент
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.title.text = items[position].title

        // Знаходимо id картинки перед меню
        val imageId = context.resources.getIdentifier(items[position].image, "drawable", context.packageName)

        holder.image.setImageResource(imageId)

        holder.layout.setOnClickListener {
            when (items[position].id){
                1 -> {
                    callback.onMyPetsSelected()
                    Log.d("Project log","Select a ${items[position].title}");
                }
                2 -> Log.d("Project log","Select a ${items[position].title}")
                3 -> Log.d("Project log","Select a ${items[position].title}")
                4 -> Log.d("Project log","Select a ${items[position].title}")
                5 -> Log.d("Project log","Select a ${items[position].title}")
                6 -> Log.d("Project log","Select a ${items[position].title}")
            }
        }
    }

    interface AccountFragmentCallback {
        fun onMyPetsSelected()
    }


}
