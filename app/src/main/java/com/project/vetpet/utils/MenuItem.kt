package com.project.vetpet.utils

class MenuItem(
    val id: Int,
    val title: String,
    val image: String) {

    companion object{
        fun addAccountMenuItems(): ArrayList<MenuItem> {
            val items = arrayListOf<MenuItem>()
            items.add(MenuItem(1,"Мої улюбленці", "ic_acc_menu_paw"))
            items.add(MenuItem(2,"Прийоми", "ic_acc_menu_calendar"))
            items.add(MenuItem(3,"Благодійність", "ic_acc_menu_charity"))
            items.add(MenuItem(4,"Клініки поруч", "ic_acc_menu_clinic"))
            return items
        }

        fun addVeterinarianMenuItems(): ArrayList<MenuItem>{
            return arrayListOf(
                MenuItem(1,"Записатися на прийом", "ic_acc_menu_calendar"),
                MenuItem(2,"Знайти клініку", "ic_bm_map"),
                MenuItem(3,"Відгуки", "ic_reviews")
            )
        }
    }
}
