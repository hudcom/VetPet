package com.project.vetpet.view.tabs.map

import com.project.vetpet.view.BaseViewModel

class MapsViewModel(): BaseViewModel() {

    private var searchQuery: String? = null

    fun setSearchQuery(search: String?){
        searchQuery = search
    }

    fun getSearchQuery() = searchQuery
}