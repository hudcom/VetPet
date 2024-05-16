
package com.project.vetpet.model.service

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.project.vetpet.model.Pet
import com.project.vetpet.model.User
import com.project.vetpet.view.TAG

class PetService {

    private val petList: MutableList<Pet> = mutableListOf()

    private fun getDatabase(): FirebaseDatabase = Firebase.database

    fun addPet(pet: Pet){
        // Отримання посилання на вашу таблицю у базі даних
        val myRef = getDatabase().getReference(REFERENCE)

        myRef.child(pet.name!!).setValue(createObject(pet))
    }

    fun getPetList(): MutableList<Pet> {
        petList.clear()
        readPetList()
        return petList
    }

    fun editPet(oldName:String?, pet: Pet){
        val myRef = getDatabase().getReference("$REFERENCE/$oldName")

        myRef.updateChildren(getUpdates(oldName,pet))
    }

    fun deletePet(name: String){
        val myRef = getDatabase().getReference(REFERENCE)
        // Видалення файла з Realtime Database
        myRef.child(name).removeValue()
    }

    private fun getUpdates(oldName:String?, pet: Pet): HashMap<String, Any>{
        val updates = HashMap<String, Any>()
        //Оновлюємо поля запису
        updates[PET_AGE] = pet.age!!
        updates[PET_TYPE] = pet.type!!
        updates[PET_BREED] = pet.breed!!
        //Документуємо
        Log.d(TAG,"EDIT ELEMENT $oldName")
        //Викликаємо метод  для створення нового та видалення старого запису
        if (oldName != pet.name){
            editPetInRD(oldName!!,pet.name!!)
        }
        return updates
    }

    private fun createObject(pet: Pet): Map<String,Any?> {
        // Створення об'єкта, який ви хочете додати до таблиці
        return mapOf(
            PET_AGE to pet.age,
            PET_TYPE to pet.type,
            PET_BREED to pet.breed,
            OWNER to pet.owner)
    }

    private fun readPetList(){
        val myRef = getDatabase().getReference(REFERENCE)
        val query = myRef.orderByChild(OWNER).equalTo(User.currentUser?.email)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataChange(dataSnapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun dataChange(dataSnapshot: DataSnapshot){
        // Цей метод буде викликаний при зміні даних відповідно до запиту
        for (snapshot in dataSnapshot.children) {
            // Отримуємо дані з кожного запису
            val value = snapshot.getValue<Pet>()
            if (value != null) {
                value.name = snapshot.key
                petList.add(value) // Додаємо елемент до petList
            }
        }
    }

    private fun editPetInRD(oldName: String, newName: String){
        val oldRef = getDatabase().getReference("$REFERENCE/$oldName")

        oldRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Зчитуємо дані з запису
                val data = dataSnapshot.value
                //Видалення старого запису
                oldRef.removeValue()
                //Створення нового запису
                val newRef = getDatabase().getReference("$REFERENCE/$newName")
                newRef.setValue(data)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG,"Error while create new file: $databaseError")
            }
        })
    }


    companion object{
        private const val REFERENCE = "pets"

        private const val PET_NAME = "name"
        private const val PET_AGE = "age"
        private const val PET_TYPE = "type"
        private const val PET_BREED = "breed"
        private const val OWNER = "owner"
    }
}
