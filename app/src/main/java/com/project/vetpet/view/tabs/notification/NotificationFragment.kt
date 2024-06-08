package com.project.vetpet.view.tabs.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.vetpet.R
import com.project.vetpet.adapters.MessageAdapter
import com.project.vetpet.model.Message
import com.project.vetpet.view.BaseFragment


class NotificationFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessageAdapter(messages)
        recyclerView.adapter = adapter
        return view
    }

    companion object{
        private lateinit var adapter: MessageAdapter
        private val messages = mutableListOf<Message>()
        fun addMessage(message: Message) {
            messages.add(message)
        }
    }
}
