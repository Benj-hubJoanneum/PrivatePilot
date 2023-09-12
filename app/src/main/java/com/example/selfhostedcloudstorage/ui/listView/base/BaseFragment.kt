package com.example.selfhostedcloudstorage.ui.listView.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.databinding.FragmentListviewBinding
import com.example.selfhostedcloudstorage.ui.listView.viewModel.RecyclerViewModel

abstract class BaseFragment(): Fragment() {
    private var _binding: FragmentListviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : BaseAdapter
    private lateinit var recyclerViewModel: RecyclerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerViewModel = ViewModelProvider(this)[RecyclerViewModel::class.java]

        // Initialize fileAdapter
        recyclerViewModel.itemList.observe(viewLifecycleOwner) { newItemList ->
            adapter.updateList(newItemList)
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = createLayoutManager()
        recyclerView.adapter = adapter

        val textView: TextView = binding.textAllFiles
        recyclerViewModel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    abstract fun createLayoutManager(): RecyclerView.LayoutManager

}