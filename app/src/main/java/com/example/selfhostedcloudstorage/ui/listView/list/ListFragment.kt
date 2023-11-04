package com.example.selfhostedcloudstorage.ui.listView.list

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.ui.listView.base.BaseAdapter
import com.example.selfhostedcloudstorage.ui.listView.base.BaseFragment

class ListFragment() : BaseFragment() {
    override fun createAdapter(): BaseAdapter {
        return ListAdapter(emptyList(), requireActivity() as MainActivity)
    }
    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireContext())
    }
}
