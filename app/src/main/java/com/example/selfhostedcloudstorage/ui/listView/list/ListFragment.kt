package com.example.selfhostedcloudstorage.ui.listView.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.databinding.FragmentListviewBinding
import com.example.selfhostedcloudstorage.ui.listView.viewModel.RecyclerViewModel

class ListFragment : Fragment() {
    private var _binding: FragmentListviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var listAdapter: ListAdapter
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
        listAdapter = ListAdapter(emptyList(), requireActivity() as MainActivity)
        recyclerViewModel.itemList.observe(viewLifecycleOwner) { newItemList ->
            listAdapter.updateList(newItemList)
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = listAdapter

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
}
