package com.example.selfhostedcloudstorage.ui.listView.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.databinding.FragmentListviewBinding
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository
import com.example.selfhostedcloudstorage.ui.listView.base.viewModel.HorizontalListViewModel
import com.example.selfhostedcloudstorage.ui.listView.base.viewModel.RecyclerViewModel
import com.example.selfhostedcloudstorage.ui.listView.base.horizontalList.HorizontalListAdapter

abstract class BaseFragment : Fragment() {
    private var _binding: FragmentListviewBinding? = null
    val binding get() = _binding!!
    private lateinit var adapter : BaseAdapter
    lateinit var recyclerViewModel: RecyclerViewModel
    private val nodeRepository = NodeRepository.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerViewModel = ViewModelProvider(this)[RecyclerViewModel::class.java]
        val horizontalListViewModel = ViewModelProvider(this)[HorizontalListViewModel::class.java]

        // Initialize fileAdapter
        adapter = createAdapter()

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = createLayoutManager()
        recyclerView.adapter = adapter

        val horizontalRecyclerView: RecyclerView = binding.horizontalView

        val horizontalListAdapter = HorizontalListAdapter()
        horizontalRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        horizontalRecyclerView.adapter = horizontalListAdapter

        horizontalListViewModel.itemList.observe(viewLifecycleOwner) { horizontalListAdapter.updateList(it) }
        nodeRepository.directoryPointer.observe(viewLifecycleOwner) { horizontalListViewModel.loadList(it) }

        val textView: TextView = binding.listDescription
        recyclerViewModel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }

        val imageView: ImageView = binding.switchImage
        recyclerViewModel.imageResource.observe(viewLifecycleOwner) { resource ->
            imageView.setImageResource(resource)
        }

        imageView.setOnClickListener {
            // Check the current fragment and navigate to the other fragment
            val currentFragmentId = requireActivity().findNavController(R.id.nav_host_fragment_content_main).currentDestination?.id
            val newFragmentId = if (currentFragmentId == R.id.nav_listview) {
                R.id.nav_gridview // Switch to the GridFragment
            } else {
                R.id.nav_listview // Switch to the ListFragment
            }
            requireActivity().findNavController(R.id.nav_host_fragment_content_main).navigate(newFragmentId)
        }

        nodeRepository.displayedList.observe(viewLifecycleOwner) {
            recyclerViewModel.loadFileList(it)
        }

        recyclerViewModel.itemList.observe(viewLifecycleOwner) { newItemList ->
            adapter.updateList(newItemList)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    abstract fun createAdapter(): BaseAdapter
    abstract fun createLayoutManager(): RecyclerView.LayoutManager

}
