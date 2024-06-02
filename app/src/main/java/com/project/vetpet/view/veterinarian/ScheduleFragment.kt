package com.project.vetpet.view.veterinarian

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.adapters.AppointmentAdapter
import com.project.vetpet.model.AppointmentSlot
import com.project.vetpet.model.User
import com.project.vetpet.model.Veterinarian
import com.project.vetpet.view.TAG
import com.project.vetpet.view.factory
import java.io.Serializable

class ScheduleFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter


    private val viewModel: ScheduleFragmentViewModel by viewModels{ factory() }

    companion object {
        private const val ARG_DATE = "date"
        private lateinit var veterinarian:Veterinarian
        private var fileDate: String? = null

        fun newInstance(date: String, serializableExtra: Serializable?): ScheduleFragment {
            val fragment = ScheduleFragment()
            val args = Bundle()
            args.putString(ARG_DATE, date)
            fragment.arguments = args

            fileDate = date
            veterinarian = serializableExtra as Veterinarian

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fileDate = it.getString(ARG_DATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val vetName = veterinarian.fullName

        // Отримання розкладу з бази даних
        viewModel.getSchedule(vetName, fileDate ?: "") { slots ->
            if (slots.isNotEmpty()) {
                adapter = AppointmentAdapter(slots) { slot, position ->
                       if (slot.isBooked) {
                        Log.d(TAG,"\"Цей час вже зайнятий\"")
                        Toast.makeText(requireContext(), "Цей час вже зайнятий", Toast.LENGTH_SHORT).show()
                    } else {

                        showDialog(slot, position, vetName)
                    }
                }
                recyclerView.adapter = adapter
            } else {
                Toast.makeText(requireContext(), "Помилка при отриманні розкладу", Toast.LENGTH_SHORT).show()
                viewModel.crateSchedule(vetName, fileDate.toString())
            }
        }
        return view
    }

    private fun showDialog(slot: AppointmentSlot, position: Int, name:String) {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
        builder.setTitle("Підтвердження")
            .setMessage("Ви дійсно хочете записатися на ${slot.day} ${slot.time}?")
            .setCancelable(true)
            .setPositiveButton("Так") { _, _ ->
                slot.isBooked = true
                slot.client = User.currentUser?.email ?: ""
                writeDataToDB(slot,position,name)
            }
            .setNegativeButton("Ні") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun writeDataToDB(slot:AppointmentSlot,position: Int,vetName:String){
        viewModel.writeAppointmentSlot(slot,vetName){
            if (it){
                adapter.notifyItemChanged(position)
            }
        }
    }
}