package com.example.selfhostedcloudstorage.ui.listView.grid

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.ui.listView.base.BaseAdapter
import com.example.selfhostedcloudstorage.ui.listView.base.BaseFragment
import com.example.selfhostedcloudstorage.ui.listView.base.viewModel.RecyclerViewModel

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
