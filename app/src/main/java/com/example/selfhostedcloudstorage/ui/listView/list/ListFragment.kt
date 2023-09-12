package com.example.selfhostedcloudstorage.ui.listView.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.databinding.FragmentListviewBinding
import com.example.selfhostedcloudstorage.ui.listView.base.BaseAdapter
import com.example.selfhostedcloudstorage.ui.listView.base.BaseFragment
import com.example.selfhostedcloudstorage.ui.listView.grid.GridAdapter
import com.example.selfhostedcloudstorage.ui.listView.viewModel.RecyclerViewModel

class ListFragment : BaseFragment() {
    override fun createAdapter(): BaseAdapter {
        return ListAdapter(emptyList(), requireActivity() as MainActivity)
    }
    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireContext())
    }
}
