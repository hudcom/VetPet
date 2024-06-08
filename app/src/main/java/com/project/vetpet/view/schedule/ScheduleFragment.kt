package com.project.vetpet.view.schedule

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
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
import com.project.vetpet.view.clinic.ClinicViewModel
import com.project.vetpet.view.factory
import java.io.Serializable

class ScheduleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private val viewModel: ScheduleFragmentViewModel by viewModels { factory() }
    private val clinicViewModel: ClinicViewModel by viewModels { factory() }

    companion object {
        private const val ARG_DATE = "date"
        private const val ARG_VET = "veterinarian"

        fun newInstance(date: String, veterinarian: Veterinarian): ScheduleFragment {
            val fragment = ScheduleFragment()
            val args = Bundle()
            args.putString(ARG_DATE, date)
            args.putSerializable(ARG_VET, veterinarian)
            fragment.arguments = args
            return fragment
        }
    }

    private var selectedDate: String? = null
    private lateinit var veterinarian: Veterinarian

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getString(ARG_DATE)
            veterinarian = it.getSerializable(ARG_VET) as Veterinarian
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
        viewModel.getSchedule(vetName, selectedDate ?: "") { slots ->
            if (slots.isNotEmpty()) {
                adapter = AppointmentAdapter(slots) { slot, position ->
                    if (slot.isBooked) {
                        Log.d(TAG, "Цей час вже зайнятий")
                        Toast.makeText(requireContext(), "Цей час вже зайнятий", Toast.LENGTH_SHORT).show()
                    } else {
                        if (clinicViewModel.hasCurrentClinic()) {
                            showInputDialog(slot, position, vetName)
                        } else {
                            showDialog(slot, position, vetName)
                        }
                    }
                }
                recyclerView.adapter = adapter
            } else {
                Toast.makeText(requireContext(), "Помилка при отриманні розкладу", Toast.LENGTH_SHORT).show()
                viewModel.crateSchedule(vetName, selectedDate.toString())
                refreshFragment()
            }
        }
        return view
    }

    private fun showDialog(slot: AppointmentSlot, position: Int, name: String) {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
        builder.setTitle("Підтвердження")
            .setMessage("Ви дійсно хочете записатися на ${slot.day} ${slot.time}?")
            .setCancelable(true)
            .setPositiveButton("Так") { _, _ ->
                slot.isBooked = true
                slot.client = User.currentUser?.email ?: ""
                writeDataToDB(slot, position, name)
            }
            .setNegativeButton("Ні") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showInputDialog(slot: AppointmentSlot, position: Int, name: String) {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
        builder.setTitle("Введення даних").setMessage("Введіть email користувача для запису:")

        val container = FrameLayout(requireContext())
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        val margin = resources.getDimensionPixelSize(R.dimen.medium_space)
        params.setMargins(margin, 0, margin, 0)
        container.layoutParams = params

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.layoutParams = params
        container.addView(input)

        builder.setView(container)
        builder.setPositiveButton("Так") { dialog, _ ->
            val userInput = input.text.toString()
            slot.isBooked = true
            slot.client = userInput
            writeDataToDB(slot, position, name)
            handleUserInput(userInput)
            dialog.dismiss()
        }
        builder.setNegativeButton("Ні") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun handleUserInput(userInput: String) {
        Toast.makeText(requireContext(), "Користувач: $userInput записаний", Toast.LENGTH_SHORT).show()
    }

    private fun refreshFragment() {
        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.detach(this).attach(this).commit()
    }

    private fun writeDataToDB(slot: AppointmentSlot, position: Int, vetName: String) {
        viewModel.writeAppointmentSlot(slot, vetName) {
            if (it) {
                adapter.notifyItemChanged(position)
            }
        }
    }
}