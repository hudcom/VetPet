package com.project.vetpet.utils

import android.view.Menu

class MenuItem(
    val id: Int,
    val title: String,
    val image: String) {

    companion object{
        fun addAccountMenuItems(): ArrayList<MenuItem> {
            return arrayListOf(
                MenuItem(1,"Мої улюбленці", "ic_acc_menu_paw"),
                MenuItem(2,"Прийоми", "ic_acc_menu_calendar"),
                MenuItem(3,"Благодійність", "ic_acc_menu_charity"),
                MenuItem(4,"Клініки поруч", "ic_acc_menu_clinic")
            )
        }

        fun addVeterinarianMenuItems(): ArrayList<MenuItem>{
            return arrayListOf(
                MenuItem(1,"Записатися на прийом", "ic_acc_menu_calendar"),
                MenuItem(2,"Знайти клініку", "ic_bm_map"),
                MenuItem(3,"Відгуки", "ic_reviews")
            )
        }

        fun addClinicMenuItems(): ArrayList<MenuItem>{
            return arrayListOf(
                MenuItem(1,"Розташування клініки", "ic_bm_map"),
                MenuItem(2,"Ветеринари", "ic_bm_profile"),
                MenuItem(3,"Відгуки", "ic_reviews")
            )
        }
    }
}
