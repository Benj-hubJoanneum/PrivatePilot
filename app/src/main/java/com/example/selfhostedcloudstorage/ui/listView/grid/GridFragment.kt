package com.example.selfhostedcloudstorage.ui.listView.grid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.databinding.FragmentListviewBinding
import com.example.selfhostedcloudstorage.ui.listView.base.BaseAdapter
import com.example.selfhostedcloudstorage.ui.listView.base.BaseFragment
import com.example.selfhostedcloudstorage.ui.listView.list.ListAdapter
import com.example.selfhostedcloudstorage.ui.listView.viewModel.RecyclerViewModel

class GridFragment : BaseFragment() {
    override fun createAdapter(): BaseAdapter {
        return GridAdapter(emptyList(), requireActivity() as MainActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewModel.setValues("Grid View", R.drawable.ic_grid)
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(requireContext(), 2)
    }
}
