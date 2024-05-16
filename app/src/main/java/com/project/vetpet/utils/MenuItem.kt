package com.project.vetpet.utils

class MenuItem(
    val id: Int,
    val title: String,
    val image: String) {

    companion object{
        fun addMenuItems(): ArrayList<MenuItem> {
            val items = arrayListOf<MenuItem>()
            items.add(MenuItem(1,"Мої улюбленці", "ic_acc_menu_paw"))
            items.add(MenuItem(2,"Прийоми", "ic_acc_menu_calendar"))
            items.add(MenuItem(3,"Благодійність", "ic_acc_menu_charity"))
            items.add(MenuItem(4,"Клініки поруч", "ic_acc_menu_clinic"))
            return items
        }
    }
}
