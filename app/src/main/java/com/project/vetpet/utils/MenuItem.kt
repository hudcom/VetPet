package com.project.vetpet.utils

class MenuItem(
    val id: Int,
    val title: String,
    val image: String) {

    companion object{
        fun addMenuItems(): ArrayList<MenuItem> {
            val items = arrayListOf<MenuItem>()
            items.add(MenuItem(1,"Мої улюбленці", "ic_paw"))
            items.add(MenuItem(2,"Прийоми", "ic_calendar_menu"))
            items.add(MenuItem(3,"Благодійність", "ic_charity"))
            items.add(MenuItem(4,"Клініки поруч", "ic_pet_clinic"))
            return items
        }
    }
}
